package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agrinova.di.models.ModuloDomainModel

@Entity(
    tableName = "modulo",
    foreignKeys = [
        ForeignKey(
            entity = FundoEntity::class,
            parentColumns = ["id"],
            childColumns = ["fundoId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["fundoId"])]
)
data class ModuloEntity(
    @PrimaryKey val id: Int,
    val codigo: String,
    val nombre: String,
    val fundoId: Int,
    val activo: Boolean
){
    // Convierte ModuloEntity a FundoDomainModel
    fun toDomainModel(): ModuloDomainModel {
        return ModuloDomainModel(
            id = this.id,
            codigo = this.codigo,
            nombre = this.nombre,
            fundoId = this.fundoId,
            activo = this.activo
        )
    }
}