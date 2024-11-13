package com.example.agrinova.data.remote.model

import com.example.agrinova.data.local.entity.LoteEntity


data class LoteDataModel(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val moduloId: Int,
    val activo: Boolean,
    val campaniaSet: List<CampaniaDataModel>?
)
{
    fun toEntity(): LoteEntity {
        return LoteEntity(
            id = this.id,
            codigo = this.codigo,
            nombre = this.nombre,
            moduloId = this.moduloId,
            activo = this.activo
        )
    }
}