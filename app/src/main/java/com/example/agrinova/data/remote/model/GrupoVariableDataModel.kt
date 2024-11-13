package com.example.agrinova.data.remote.model

import com.example.agrinova.data.local.entity.GrupoVariableEntity

data class GrupoVariableDataModel(
    val id: Int,
    val calculado: Boolean,
    val grupoCodigo: String,
    val grupoNombre: String,
    val grupoId: Int,
    val cartillaEvaluacionId: Int,
    val variableGrupoSet: List<VariableGrupoDataModel>?
)
{
    fun toEntity(): GrupoVariableEntity {
        return GrupoVariableEntity(
            id = this.id,
            calculado = this.calculado,
            grupoCodigo = this.grupoCodigo,
            grupoNombre = this.grupoNombre,
            grupoId = this.grupoId,
            cartillaEvaluacionId = this.cartillaEvaluacionId
        )
    }
}