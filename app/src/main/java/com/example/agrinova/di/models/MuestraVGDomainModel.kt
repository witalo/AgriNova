package com.example.agrinova.di.models

data class MuestraVGDomainModel(
    val id: Int,
    val muestra: Float,
    val fecha: String,
    val latitud: Float,
    val longitud: Float,
    val variableGrupoId: Int
)