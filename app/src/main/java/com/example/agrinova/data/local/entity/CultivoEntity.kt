package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.agrinova.data.remote.model.CultivoDataModel

@Entity(tableName = "cultivo")
data class CultivoEntity(
    @PrimaryKey val id: Int,
    val codigo: String,
    val nombre: String,
    val activo: Boolean
){
    companion object {
        fun fromDataModel(dataModel: CultivoDataModel): CultivoEntity {
            return CultivoEntity(
                id = dataModel.id,
                codigo = dataModel.codigo,
                nombre = dataModel.nombre,
                activo = dataModel.activo
            )
        }
    }
}