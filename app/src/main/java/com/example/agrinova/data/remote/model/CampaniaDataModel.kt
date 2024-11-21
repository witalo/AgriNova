package com.example.agrinova.data.remote.model

import com.example.agrinova.data.local.entity.CampaniaEntity


data class CampaniaDataModel(
    val id: Int,
    val numero: Int,
    val centroCosto: String,
    val loteId: Int,
    val cultivoId: Int,
    val activo: Boolean,
    val valvulaSet: List<ValvulaDataModel>? = null
)
{
    fun toEntity(): CampaniaEntity {
        return CampaniaEntity(
            id = this.id,
            numero = this.numero,
            centroCosto = this.centroCosto,
            loteId = this.loteId,
            cultivoId = this.cultivoId,
            activo = this.activo
        )
    }
}