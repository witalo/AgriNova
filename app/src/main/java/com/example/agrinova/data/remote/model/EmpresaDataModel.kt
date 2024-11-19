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
    val userSet: List<UsuarioDataModel>? = null,
    val zonaSet: List<ZonaDataModel>? = null,
    val cartillaEvaluacionSet: List<CartillaEvaluacionDataModel>? = null,
    val cultivoSet: List<CultivoDataModel>? = null
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
                cartillaEvaluacionSet = empresaData.cartillaEvaluacionSet?.map { cartilla ->
                    CartillaEvaluacionDataModel(
                        id = cartilla?.id ?: 0,
                        codigo = cartilla?.codigo ?: "",
                        nombre = cartilla?.nombre ?: "",
                        activo = cartilla?.activo ?: false,
                        cultivoId = cartilla?.cultivoId ?: 0,
                        userCartillaSet = cartilla?.userCartillaSet?.map { userCartilla ->
                            UsuarioCartillaDataModel(
                                usuarioId = userCartilla?.userId ?: 0,
                                cartillaId = userCartilla?.cartillaId ?: 0
                            )
                        } ?: emptyList(),
                        grupoVariableSet = cartilla?.grupovariableSet?.map { grupo ->
                            GrupoVariableDataModel(
                                id = grupo.id ?: 0,
                                calculado = grupo.calculado,
                                grupoCodigo = grupo.grupoCodigo ?: "",
                                grupoNombre = grupo.grupoNombre ?: "",
                                grupoId = grupo.grupoId ?: 0,
                                cartillaEvaluacionId = grupo.cartillaEvaluacionId ?: 0,
                                variableGrupoSet = grupo.variableGrupoSet?.map { variable ->
                                    VariableGrupoDataModel(
                                        id = variable?.id ?: 0,
                                        minimo = variable?.minimo ?: 0,
                                        maximo = variable?.maximo ?: 0,
                                        calculado = variable?.calculado ?: false,
                                        variableEvaluacionNombre = variable?.variableEvaluacionNombre ?: "",
                                        grupoVariableId = variable?.grupoVariableId ?: 0
                                    )
                                } ?: emptyList()
                            )
                        } ?: emptyList()
                    )
                },
                cultivoSet = empresaData.cultivoSet?.map { cultivo ->
                    CultivoDataModel(
                        id = cultivo?.id ?: 0,
                        codigo = cultivo?.codigo ?: "",
                        nombre = cultivo?.nombre ?: "",
                        activo = cultivo?.activo ?: false
                    )
                } ?: emptyList()
            )
        }
    }
}