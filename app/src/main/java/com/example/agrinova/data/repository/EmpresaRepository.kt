package com.example.agrinova.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
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
import com.example.agrinova.data.remote.model.CampaniaDataModel
import com.example.agrinova.data.remote.model.LoteDataModel
import com.example.agrinova.data.remote.model.ModuloDataModel
import com.example.agrinova.data.remote.model.PoligonoDataModel
import com.example.agrinova.data.remote.model.ValvulaDataModel
import com.example.agrinova.di.models.FundoDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EmpresaRepository(
    private val empresaDao: EmpresaDao,
    private val usuarioDao: UsuarioDao,
    private val zonaDao: ZonaDao,
    private val fundoDao: FundoDao,
    private val graphQLClient: ApolloClient
) {
      suspend fun syncEmpresaData(empresaId: Int) {
        val response = graphQLClient.query(
            GetEmpresaDataQuery(empresaId.toString())
        ).execute()

        response.data?.empresaById?.let { empresaData ->
            val existingEmpresa = empresaDao.getEmpresaById(empresaData.id!!.toInt())
            val empresaEntity = EmpresaDataModel(
                id = empresaData.id ?: 0,
                ruc = empresaData.ruc ?: "",
                razonSocial = empresaData.razonSocial ?: "",
                correo = empresaData.correo ?: "",
                telefono = empresaData.telefono ?: "",
                direccion = empresaData.direccion ?: "",
                userSet = empresaData.userSet?.map {
                    UsuarioDataModel(
                        id = it?.id ?: 0,
                        firstName = it?.firstName ?: "",
                        lastName = it?.lastName ?: "",
                        document = it?.document ?: "",
                        email = it?.email ?: "",
                        phone = it?.phone ?: "",
                        isActive = it?.isActive ?: false
                    )
                },
                zonaSet = empresaData.zonaSet?.map { zona ->
                    ZonaDataModel(
                        id = zona?.id ?: 0,
                        codigo = zona?.codigo ?: "",
                        nombre = zona?.nombre ?: "",
                        activo = zona?.activo ?: false,
                        empresaId = zona?.empresaId ?: 0,
                        fundoSet = zona?.fundoSet?.map { fundo ->
                            FundoDataModel(
                                id = fundo?.id ?: 0,
                                codigo = fundo?.codigo ?: "",
                                nombre = fundo?.nombre ?: "",
                                activo = fundo?.activo ?: false,
                                zonaId = fundo?.zonaId ?: 0,
                                userFundoSet = fundo?.userFundoSet?.map { userFundo ->
                                    UsuarioFundoDataModel(
                                        userId = userFundo?.userId ?: 0,
                                        fundoId = userFundo?.fundoId ?: 0
                                    )
                                } ?: emptyList(),
                                moduloSet = fundo?.moduloSet?.map { modulo ->
                                    ModuloDataModel(
                                        id = modulo?.id ?: 0,
                                        codigo = modulo?.codigo ?: "",
                                        nombre = modulo?.nombre ?: "",
                                        activo = modulo?.activo ?: false,
                                        fundoId = modulo?.fundoId ?: 0,
                                        loteSet = modulo?.loteSet?.map { lote ->
                                            LoteDataModel(
                                                id = lote?.id ?: 0,
                                                codigo = lote?.codigo ?: "",
                                                nombre = lote?.nombre ?: "",
                                                activo = lote?.activo ?: false,
                                                moduloId = lote?.moduloId ?: 0,
                                                campaniaSet = lote?.campaniaSet?.map { campania ->
                                                    CampaniaDataModel(
                                                        id = campania?.id ?: 0,
                                                        numero = campania?.numero ?: 0,
                                                        centroCosto = campania?.centroCosto ?: "",
                                                        activo = campania?.activo ?: false,
                                                        loteId = campania?.loteId ?: 0,
                                                        cultivoId = campania?.cultivoId ?: 0,
                                                        valvulaSet = campania?.valvulaSet?.map { valvula ->
                                                            ValvulaDataModel(
                                                                id = valvula?.id ?: 0,
                                                                codigo = valvula?.codigo ?: "",
                                                                nombre = valvula?.nombre ?: "",
                                                                activo = valvula?.activo ?: false,
                                                                campaniaId = valvula?.campaniaId ?: 0,
                                                                poligonoSet = valvula?.poligonoSet?.map { poligono ->
                                                                    PoligonoDataModel(
                                                                        id = poligono?.id ?: 0,
                                                                        latitud = poligono?.latitud?.toFloat() ?: 0.0f,
                                                                        longitud = poligono?.longitud?.toFloat() ?: 0.0f,
                                                                        valvulaId = poligono?.valvulaId ?: 0
                                                                    )
                                                                } ?: emptyList()
                                                            )
                                                        } ?: emptyList()
                                                    )
                                                } ?: emptyList()
                                            )
                                        } ?: emptyList()
                                    )
                                } ?: emptyList()
                            )
                        } ?: emptyList()
                    )
                },
//                cartillaEvaluacionSet = empresaData.cartillaEvaluacionSet?.map { cartilla ->
//                    CartillaEvaluacionDataModel(
//                        id = cartilla?.id ?: 0,
//                        codigo = cartilla?.codigo ?: "",
//                        nombre = cartilla?.nombre ?: "",
//                        activo = cartilla?.activo ?: false,
//                        cultivoId = cartilla?.cultivoId ?: 0,
//                        userCartillaSet = cartilla?.userCartillaSet?.map { userCartilla ->
//                            UserCartillaDataModel(
//                                userId = userCartilla?.userId ?: 0,
//                                cartillaId = userCartilla?.cartillaId ?: 0
//                            )
//                        } ?: emptyList(),
//                        grupoVariableSet = cartilla?.grupovariableSet?.map { grupo ->
//                            GrupoVariableDataModel(
//                                id = grupo?.id ?: 0,
//                                calculado = grupo?.calculado ?: false,
//                                grupoCodigo = grupo?.grupoCodigo ?: "",
//                                grupoNombre = grupo?.grupoNombre ?: "",
//                                grupoId = grupo?.grupoId ?: 0,
//                                cartillaEvaluacionId = grupo?.cartillaEvaluacionId ?: 0,
//                                variableGrupoSet = grupo?.variableGrupoSet?.map { variable ->
//                                    VariableGrupoDataModel(
//                                        id = variable?.id ?: 0,
//                                        minimo = variable?.minimo ?: 0.0,
//                                        maximo = variable?.maximo ?: 0.0,
//                                        calculado = variable?.calculado ?: false,
//                                        variableEvaluacionNombre = variable?.variableEvaluacionNombre ?: "",
//                                        grupoVariableId = variable?.grupoVariableId ?: 0
//                                    )
//                                } ?: emptyList()
//                            )
//                        } ?: emptyList()
//                    )
//                },
//                cultivoSet = empresaData.cultivoSet?.map { cultivo ->
//                    CultivoDataModel(
//                        id = cultivo?.id ?: 0,
//                        codigo = cultivo?.codigo ?: "",
//                        nombre = cultivo?.nombre ?: "",
//                        activo = cultivo?.activo ?: false
//                    )
//                } ?: emptyList()
            )

            // Guardar o actualizar la empresa y sus datos relacionados
            if (existingEmpresa != null) {
                empresaDao.updateEmpresa(empresaEntity.toEntity())
            } else {
                empresaDao.insertEmpresa(empresaEntity.toEntity())
            }

            // Sincronización de usuarios, zonas, fondos, módulos, lotes, campañas, válvulas, y polígonos
            // (Implementar similar al ejemplo anterior, según los DAOs correspondientes)
        } ?: throw Exception("Error al sincronizar: Datos de empresa no encontrados.")
    }

    // Función para sincronizar los datos de la empresa desde la API
//    suspend fun syncEmpresaData(empresaId: Int) {
//        val response = graphQLClient.query(
//            GetEmpresaDataQuery(empresaId.toString())
//        ).execute()
//
//        response.data?.empresaById?.let { empresaData ->
//            // Comprobar si la empresa ya existe
//            val existingEmpresa = empresaDao.getEmpresaById(empresaData.id!!.toInt())
//            val empresaEntity = EmpresaDataModel(
//                id = empresaData.id?: 0,
//                ruc = empresaData.ruc?: "",
//                razonSocial = empresaData.razonSocial?: "",
//                correo = empresaData.correo?: "",
//                telefono = empresaData.telefono?: "",
//                direccion = empresaData.direccion?: "",
//                userSet = empresaData.userSet?.map {
//                    UsuarioDataModel(
//                        id = it?.id?: 0,
//                        firstName = it?.firstName?: "",
//                        lastName = it?.lastName?: "",
//                        document = it?.document?: "",
//                        email = it?.email?: "",
//                        phone = it?.phone?: "",
//                        isActive = it?.isActive?: false
//                    )
//                },
//                zonaSet = empresaData.zonaSet?.map { zona ->
//                    ZonaDataModel(
//                        id = zona?.id?: 0,
//                        codigo = zona?.codigo?: "",
//                        nombre = zona?.nombre?: "",
//                        activo = zona?.activo?: false,
//                        empresaId = zona?.empresaId?: 0,
//                        fundoSet = zona?.fundoSet?.map { fundo ->
//                            FundoDataModel(
//                                id = fundo?.id?: 0,
//                                codigo = fundo?.codigo?: "",
//                                nombre = fundo?.nombre?: "",
//                                activo = fundo?.activo?: false,
//                                zonaId = fundo?.zonaId?: 0,
//                                userFundoSet = fundo?.userFundoSet?.map { userFundo ->
//                                    UsuarioFundoDataModel(
//                                        userId = userFundo?.userId ?: 0,
//                                        fundoId = userFundo?.fundoId ?: 0
//                                    )
//                                } ?: emptyList()
//                            )
//                        }
//                    )
//                }
//            )
//            if (existingEmpresa != null) {
//                // Actualizar los datos existentes
//                empresaDao.updateEmpresa(empresaEntity.toEntity())
//            } else {
//                // Almacenar la empresa
//                Log.d("Italo E", empresaData.toString())
//                empresaDao.insertEmpresa(empresaEntity.toEntity())
//            }
//
//            // Sincronizar usuarios
//            Log.d("Italo USET", empresaEntity.userSet.toString())
//            empresaEntity.userSet?.forEach { usuario ->
//                Log.d("Italo User", usuario.toString())
//                val existingUsuario = usuarioDao.getUsuarioById(usuario.id.toInt())
//                Log.d("Italo Bool", existingUsuario.toString())
//                if (existingUsuario != null) {
//                    Log.d("Italo Update", existingUsuario.toString())
//                    usuarioDao.updateUsuario(usuario.toEntity())
//                } else {
//                    Log.d("Italo Insert", empresaData.toString())
//                    usuarioDao.insertUsuario(usuario.toEntity())
//                }
//            }
//
//            // Sincronizar zonas y fundos
//            Log.d("Italo Zonas", empresaEntity.zonaSet.toString())
//            empresaEntity.zonaSet?.forEach { zona ->
//                Log.d("Italo Zona", zona.toString())
//                val existingZona = zonaDao.getZonaById(zona.id)
//                Log.d("Italo Zonas", existingZona.toString())
//                if (existingZona != null) {
//                    zonaDao.updateZona(zona.toEntity())
//                } else {
//                    Log.d("Italo Zona Insert", empresaData.toString())
//                    zonaDao.insertZona(zona.toEntity())
//                }
//
//                zona.fundoSet?.forEach { fundo ->
//                    val existingFundo = fundoDao.getFundoById(fundo.id)
//                    if (existingFundo != null) {
//                        fundoDao.updateFundo(fundo.toEntity())
//                    } else {
//                        Log.d("Italo F", empresaData.toString())
//                        fundoDao.insertFundo(fundo.toEntity())
//                    }
//
//                    // Almacenar relaciones usuario-fundo
//                    fundo.userFundoSet?.forEach { userFundo ->
//                        val userId = userFundo.userId
//                        val fundoId = userFundo.fundoId
//
//                        // Verifica si la relación ya existe
//                        val exists = usuarioDao.checkUsuarioFundoExists(userId, fundoId) > 0
//
//                        if (!exists) {
//                            // Inserta solo si la relación no existe
//                            Log.d("Italo UF", empresaData.toString())
//                            usuarioDao.insertUsuarioFundoCrossRef(userFundo.toEntity())
//                        }
//                    }
//                }
//            }
//        } ?: throw Exception("Error al sincronizar: Datos de empresa no encontrados.")
//    }
    fun getFundos(): Flow<List<FundoDomainModel>> {
        return fundoDao.getAllFundos().map { fundos ->
            fundos.map { it.toDomainModel() } // Mapea cada FundoEntity a FundoDomainModel
        }
    }

}
