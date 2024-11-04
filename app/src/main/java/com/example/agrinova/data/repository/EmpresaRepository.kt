package com.example.agrinova.data.repository

import com.example.agrinova.data.local.dao.EmpresaDao
import com.example.agrinova.data.local.dao.UsuarioDao
import com.example.agrinova.data.local.dao.ZonaDao
import com.example.agrinova.data.local.dao.FundoDao
import com.example.agrinova.data.remote.GraphQLClient
import com.example.agrinova.data.remote.model.EmpresaDataModel
import com.example.agrinova.data.remote.model.UsuarioDataModel
import com.example.agrinova.data.remote.model.UsuarioFundoDataModel
import com.example.agrinova.data.remote.model.ZonaDataModel
import com.example.agrinova.data.remote.model.FundoDataModel
import com.example.agrinova.GetEmpresaDataQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EmpresaRepository(
    private val empresaDao: EmpresaDao,
    private val usuarioDao: UsuarioDao,
    private val zonaDao: ZonaDao,
    private val fundoDao: FundoDao,
    private val graphQLClient: GraphQLClient
) {

    // Función para sincronizar los datos de la empresa desde la API
    suspend fun syncEmpresaData(empresaId: Int) {
        val response = graphQLClient.apolloClient.query(
            GetEmpresaDataQuery(empresaId.toString())
        ).execute()

        response.data?.empresaById?.let { empresaData ->
            // Comprobar si la empresa ya existe
            val existingEmpresa = empresaDao.getEmpresaById(empresaData.id!!.toInt())

            val empresaEntity = EmpresaDataModel(
                id = empresaData.id,
                ruc = empresaData.ruc!!,
                razonSocial = empresaData.razonSocial!!,
                correo = empresaData.correo!!,
                telefono = empresaData.telefono!!,
                direccion = empresaData.direccion!!,
                userSet = empresaData.userSet?.map {
                    UsuarioDataModel(
                        id = it?.id!!,
                        firstName = it.firstName,
                        lastName = it.lastName,
                        document = it.document!!,
                        email = it.email,
                        phone = it.phone!!,
                        isActive = it.isActive
                    )
                },
                zonaSet = empresaData.zonaSet?.map { zona ->
                    ZonaDataModel(
                        id = zona?.id!!,
                        codigo = zona.codigo!!,
                        nombre = zona.nombre!!,
                        activo = zona.activo,
                        empresaId = zona.empresaId!!,
                        fundoSet = zona.fundoSet?.map { fundo ->
                            FundoDataModel(
                                id = fundo?.id!!,
                                codigo = fundo.codigo!!,
                                nombre = fundo.nombre!!,
                                activo = fundo.activo,
                                zonaId = fundo.zonaId!!,
                                userFundoSet = fundo.userFundoSet?.map { userFundo ->
                                    UsuarioFundoDataModel(
                                        userId = userFundo?.userId ?: 0,
                                        fundoId = userFundo?.fundoId ?: 0
                                    )
                                } ?: emptyList()
                            )
                        }
                    )
                }
            )

            if (existingEmpresa != null) {
                // Actualizar los datos existentes
                empresaDao.updateEmpresa(empresaEntity.toEntity())
            } else {
                // Almacenar la empresa
                empresaDao.insertEmpresa(empresaEntity.toEntity())
            }

            // Sincronizar usuarios
            empresaEntity.userSet?.forEach { usuario ->
                val existingUsuario = usuarioDao.getUsuarioById(usuario.id.toInt())
                if (existingUsuario != null) {
                    usuarioDao.updateUsuario(usuario.toEntity())
                } else {
                    usuarioDao.insertUsuario(usuario.toEntity())
                }
            }

            // Sincronizar zonas y fundos
            empresaEntity.zonaSet?.forEach { zona ->
                val existingZona = zonaDao.getZonaById(zona.id)
                if (existingZona != null) {
                    zonaDao.updateZona(zona.toEntity())
                } else {
                    zonaDao.insertZona(zona.toEntity())
                }

                zona.fundoSet?.forEach { fundo ->
                    val existingFundo = fundoDao.getFundoById(fundo.id)
                    if (existingFundo != null) {
                        fundoDao.updateFundo(fundo.toEntity())
                    } else {
                        fundoDao.insertFundo(fundo.toEntity())
                    }

                    // Almacenar relaciones usuario-fundo
                    fundo.userFundoSet?.forEach { userFundo ->
                        val userId = userFundo.userId
                        val fundoId = userFundo.fundoId

                        // Verifica si la relación ya existe
                        val exists = usuarioDao.checkUsuarioFundoExists(userId, fundoId) > 0

                        if (!exists) {
                            // Inserta solo si la relación no existe
                            usuarioDao.insertUsuarioFundoCrossRef(userFundo.toEntity())
                        }
                    }
                }
            }
        } ?: throw Exception("Error al sincronizar: Datos de empresa no encontrados.")
    }

}
