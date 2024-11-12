package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agrinova.di.models.CampaniaDomainModel

@Entity(
    tableName = "campania",
    foreignKeys = [
        ForeignKey(
            entity = LoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["loteId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["loteId"])]
)
data class CampaniaEntity(
    @PrimaryKey val id: Int,
    val numero: Int,
    val centroCosto: String,
    val loteId: Int,
    val cultivoId: Int,
    val activo: Boolean
){
    // Convierte CampaniaEntity a CampaniaDomainModel
    fun toDomainModel(): CampaniaDomainModel {
        return CampaniaDomainModel(
            id = this.id,
            numero = this.numero,
            centroCosto = this.centroCosto,
            loteId = this.loteId,
            cultivoId = this.cultivoId,
            activo = this.activo
        )
    }
}