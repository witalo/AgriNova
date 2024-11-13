package com.example.agrinova.data.remote.model

import com.example.agrinova.data.local.entity.CampaniaEntity
import com.example.agrinova.data.local.entity.PoligonoEntity

data class PoligonoDataModel(
    val id: Int,
    val latitud: Float,
    val longitud: Float,
    val valvulaId: Int
)
{
    fun toEntity(): PoligonoEntity {
        return PoligonoEntity(
            id = this.id,
            latitud = this.latitud,
            longitud = this.longitud,
            valvulaId = this.valvulaId
        )
    }
}