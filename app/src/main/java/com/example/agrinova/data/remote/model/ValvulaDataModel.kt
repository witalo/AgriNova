package com.example.agrinova.data.remote.model

import com.example.agrinova.data.local.entity.ValvulaEntity

data class ValvulaDataModel(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val campaniaId: Int,
    val activo: Boolean,
    val poligonoSet: List<PoligonoDataModel>?
)
{
    fun toEntity(): ValvulaEntity {
        return ValvulaEntity(
            id = this.id,
            codigo = this.codigo,
            nombre = this.nombre,
            campaniaId = this.campaniaId,
            activo = this.activo
        )
    }
}