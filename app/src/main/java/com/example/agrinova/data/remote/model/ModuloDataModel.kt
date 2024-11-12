package com.example.agrinova.data.remote.model

import com.example.agrinova.data.local.entity.ModuloEntity

data class ModuloDataModel(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val fundoId: Int,
    val activo: Boolean,
    val loteSet: List<LoteDataModel>?
)
{
    fun toEntity(): ModuloEntity {
        return ModuloEntity(
            id = this.id,
            codigo = this.codigo,
            nombre = this.nombre,
            activo = this.activo,
            zonaId = this.zonaId
        )
    }
}