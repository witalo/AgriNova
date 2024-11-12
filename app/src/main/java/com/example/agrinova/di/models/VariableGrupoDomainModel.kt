package com.example.agrinova.di.models

data class VariableGrupoDomainModel(
    val id: Int,
    val minimo: Int,
    val maximo: Int,
    val calculado: Boolean,
    val variableEvaluacionNombre: String,
    val grupoVariableId: Int
)