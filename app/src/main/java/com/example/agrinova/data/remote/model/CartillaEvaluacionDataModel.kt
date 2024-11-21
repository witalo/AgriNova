package com.example.agrinova.data.remote.model

import com.example.agrinova.data.local.entity.CartillaEvaluacionEntity

data class CartillaEvaluacionDataModel(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val activo: Boolean,
    val cultivoId: Int,
    val userCartillaSet: List<UsuarioCartillaDataModel>? = null,
    val grupoVariableSet: List<GrupoVariableDataModel>? = null,
)
{
    fun toEntity(): CartillaEvaluacionEntity {
        return CartillaEvaluacionEntity(
            id = this.id,
            codigo = this.codigo,
            nombre = this.nombre,
            activo = this.activo,
            cultivoId = this.cultivoId
        )
    }
}