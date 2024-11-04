package com.example.agrinova.data.remote.model

//import com.example.agrinova.GetEmpresaDataQuery
import com.example.agrinova.GetEmpresaDataQuery
import com.example.agrinova.data.local.entity.EmpresaEntity

data class EmpresaDataModel(
    val id: Int,
    val ruc: String,
    val razonSocial: String,
    val correo: String,
    val telefono: String,
    val direccion: String,
    val userSet: List<UsuarioDataModel>?,
    val zonaSet: List<ZonaDataModel>?
){
    // Convierte `EmpresaDataModel` en `EmpresaEntity` para guardarla en la base de datos
    fun toEntity(): EmpresaEntity {
        return EmpresaEntity(
            id = this.id,
            ruc = this.ruc,
            razonSocial = this.razonSocial,
            correo = this.correo,
            telefono = this.telefono,
            direccion = this.direccion
        )
    }
    companion object {
        fun fromGraphQL(empresaData: GetEmpresaDataQuery.EmpresaById): EmpresaDataModel {
            return EmpresaDataModel(
                id = empresaData.id!!,
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
        }
    }
}