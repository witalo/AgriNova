package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agrinova.di.models.ValvulaDomainModel

@Entity(
    tableName = "valvula",
    foreignKeys = [
        ForeignKey(
            entity = CampaniaEntity::class,
            parentColumns = ["id"],
            childColumns = ["campaniaId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["campaniaId"])]
)
data class ValvulaEntity(
    @PrimaryKey val id: Int,
    val codigo: String,
    val nombre: String,
    val campaniaId: Int,
    val activo: Boolean
){
    fun toDomainModel(): ValvulaDomainModel {
        return ValvulaDomainModel(
            id = this.id,
            codigo = this.codigo,
            nombre = this.nombre,
            campaniaId = this.campaniaId,
            activo = this.activo
        )
    }
}