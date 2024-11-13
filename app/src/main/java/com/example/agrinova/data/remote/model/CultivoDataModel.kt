package com.example.agrinova.data.remote.model

import com.example.agrinova.data.local.entity.CultivoEntity

data class CultivoDataModel(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val activo: Boolean
)
{
    fun toEntity(): CultivoEntity {
        return CultivoEntity(
            id = this.id,
            codigo = this.codigo,
            nombre = this.nombre,
            activo = this.activo
        )
    }
}