package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agrinova.di.models.LoteDomainModel
import com.example.agrinova.di.models.ModuloDomainModel

@Entity(
    tableName = "lote",
    foreignKeys = [
        ForeignKey(
            entity = ModuloEntity::class,
            parentColumns = ["id"],
            childColumns = ["moduloId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["moduloId"])]
)
data class LoteEntity(
    @PrimaryKey val id: Int,
    val codigo: String,
    val nombre: String,
    val moduloId: Int,
    val activo: Boolean
){
    // Convierte ModuloEntity a FundoDomainModel
    fun toDomainModel(): LoteDomainModel {
        return LoteDomainModel(
            id = this.id,
            codigo = this.codigo,
            nombre = this.nombre,
            moduloId = this.moduloId,
            activo = this.activo
        )
    }
}