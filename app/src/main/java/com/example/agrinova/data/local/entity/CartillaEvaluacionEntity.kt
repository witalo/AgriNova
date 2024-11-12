package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.agrinova.di.models.CartillaEvaluacionDomainModel

@Entity(tableName = "cartillaevaluacion")
data class CartillaEvaluacionEntity(
    @PrimaryKey val id: Int,
    val codigo: String,
    val nombre: String,
    val activo: Boolean,
    val cultivoId: Int
){
    companion object {
        fun fromDataModel(dataModel: CartillaEvaluacionDomainModel): CartillaEvaluacionEntity {
            return CartillaEvaluacionEntity(
                id = dataModel.id,
                codigo = dataModel.codigo,
                nombre = dataModel.nombre,
                activo = dataModel.activo,
                cultivoId = dataModel.cultivoId
            )
        }
    }
}