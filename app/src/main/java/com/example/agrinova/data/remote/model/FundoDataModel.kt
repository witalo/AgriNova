package com.example.agrinova.data.remote.model

import com.example.agrinova.data.local.entity.FundoEntity

data class FundoDataModel(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val activo: Boolean,
    val zonaId: Int,
    val userFundoSet: List<UsuarioFundoDataModel>? = null,
    val moduloSet: List<ModuloDataModel>? = null
)
{
    fun toEntity(): FundoEntity {
        return FundoEntity(
            id = this.id,
            codigo = this.codigo,
            nombre = this.nombre,
            activo = this.activo,
            zonaId = this.zonaId
        )
    }
}