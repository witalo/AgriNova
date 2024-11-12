package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agrinova.di.models.VariableGrupoDomainModel
import kotlin.math.min

@Entity(
    tableName = "variablegrupo",
    foreignKeys = [
        ForeignKey(
            entity = GrupoVariableEntity::class,
            parentColumns = ["id"],
            childColumns = ["grupoVariableId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["grupoVariableId"])]
)
data class VariableGrupoEntity(
    @PrimaryKey val id: Int,
    val minimo: Int,
    val maximo: Int,
    val calculado: Boolean,
    val variableEvaluacionNombre: String,
    val grupoVariableId: Int
){
    fun toDomainModel(): VariableGrupoDomainModel {
        return VariableGrupoDomainModel(
            id = this.id,
            minimo = this.minimo,
            maximo = this.maximo,
            calculado = this.calculado,
            variableEvaluacionNombre = this.variableEvaluacionNombre,
            grupoVariableId = this.grupoVariableId
        )
    }
}