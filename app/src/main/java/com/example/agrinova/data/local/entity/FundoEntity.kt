package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agrinova.di.models.FundoDomainModel

@Entity(
    tableName = "fundo",
    foreignKeys = [
        ForeignKey(
            entity = ZonaEntity::class,
            parentColumns = ["id"],
            childColumns = ["zonaId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["zonaId"])]
)
data class FundoEntity(
    @PrimaryKey val id: Int,
    val codigo: String,
    val nombre: String,
    val zonaId: Int,
    val activo: Boolean
){
    // Convierte FundoEntity a FundoDomainModel
    fun toDomainModel(): FundoDomainModel {
        return FundoDomainModel(
            id = this.id,
            codigo = this.codigo,
            nombre = this.nombre,
            zonaId = this.zonaId,
            activo = this.activo
        )
    }
}