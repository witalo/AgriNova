package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agrinova.di.models.GrupoVariableDomainModel

@Entity(
    tableName = "grupovariable",
    foreignKeys = [
        ForeignKey(
            entity = CartillaEvaluacionEntity::class,
            parentColumns = ["id"],
            childColumns = ["cartillaEvaluacionId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["cartillaEvaluacionId"])]
)
data class GrupoVariableEntity(
    @PrimaryKey val id: Int,
    val calculado: Boolean,
    val grupoCodigo: String,
    val grupoNombre: String,
    val grupoId: Int,
    val cartillaEvaluacionId: Int
) {
    fun toDomainModel(): GrupoVariableDomainModel {
        return GrupoVariableDomainModel(
            id = this.id,
            calculado = this.calculado,
            grupoCodigo = this.grupoCodigo,
            grupoNombre = this.grupoNombre,
            grupoId = this.grupoId,
            cartillaEvaluacionId = this.cartillaEvaluacionId
        )
    }
}