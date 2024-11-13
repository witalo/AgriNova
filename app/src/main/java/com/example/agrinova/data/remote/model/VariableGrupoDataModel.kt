package com.example.agrinova.data.remote.model

import com.example.agrinova.data.local.entity.VariableGrupoEntity

data class VariableGrupoDataModel(
    val id: Int,
    val minimo: Int,
    val maximo: Int,
    val calculado: Boolean,
    val variableEvaluacionNombre: String,
    val grupoVariableId: Int
)
{
    fun toEntity(): VariableGrupoEntity {
        return VariableGrupoEntity(
            id = this.id,
            minimo = this.minimo,
            maximo = this.maximo,
            calculado = this.calculado,
            variableEvaluacionNombre = this.variableEvaluacionNombre,
            grupoVariableId = this.grupoVariableId
        )
    }
}