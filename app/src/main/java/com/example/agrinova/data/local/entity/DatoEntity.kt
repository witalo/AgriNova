package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agrinova.di.models.DatoDomainModel

@Entity(
    tableName = "dato",
    foreignKeys = [
        ForeignKey(
            entity = ValvulaEntity::class,
            parentColumns = ["id"],
            childColumns = ["valvulaId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = CartillaEvaluacionEntity::class, // Entidad relacionada con cartillaId
            parentColumns = ["id"],
            childColumns = ["cartillaId"],
            onDelete = ForeignKey.CASCADE // Ejemplo: elimina datos si la cartilla es eliminada
        ),
        ForeignKey(
            entity = UsuarioEntity::class, // Entidad relacionada con cartillaId
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE // Ejemplo: elimina datos si la cartilla es eliminada
        )
    ],
    indices = [
        Index(value = ["valvulaId"]), // Índice para optimizar consultas por valvulaId
        Index(value = ["cartillaId"]), // Índice para optimizar consultas por cartillaId
        Index(value = ["usuarioId"]) // Índice para optimizar consultas por usuarioId
    ]
)
data class DatoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val valvulaId: Int,
    val cartillaId: Int,
    val usuarioId: Int,
    val fecha: String
) {
    fun toDomainModel(): DatoDomainModel {
        return DatoDomainModel(
            id = this.id,
            valvulaId = this.valvulaId,
            cartillaId = this.cartillaId,
            usuarioId = this.usuarioId,
            fecha = this.fecha
        )
    }
}