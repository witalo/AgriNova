package com.example.agrinova.data.dto

data class DatoWithDetalle(
    val datoId: Int,
    val valvulaId: Int,
    val fecha: String,
    val muestra: Float,
    val latitud: Float,
    val longitud: Float,
    val variableGrupoId: Int
)