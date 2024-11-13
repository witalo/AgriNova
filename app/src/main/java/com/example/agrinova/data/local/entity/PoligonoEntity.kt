package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agrinova.di.models.LoteDomainModel
import com.example.agrinova.di.models.PoligonoDomainModel

@Entity(
    tableName = "poligono",
    foreignKeys = [
        ForeignKey(
            entity = ValvulaEntity::class,
            parentColumns = ["id"],
            childColumns = ["valvulaId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["valvulaId"])]
)
data class PoligonoEntity(
    @PrimaryKey val id: Int,
    val latitud: Float,
    val longitud: Float,
    val valvulaId: Int
){
    // Convierte ModuloEntity a FundoDomainModel
    fun toDomainModel(): PoligonoDomainModel {
        return PoligonoDomainModel(
            id = this.id,
            latitud = this.latitud,
            longitud = this.longitud,
            valvulaId = this.valvulaId
        )
    }
}