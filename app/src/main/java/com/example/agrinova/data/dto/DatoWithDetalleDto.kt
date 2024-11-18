package com.example.agrinova.data.dto


data class DatoWithDetalleDto(
    val valvulaId: Int,
    val fecha: String,
    val muestra: Float,
    val latitud: Float,
    val longitud: Float,
    val variableGrupoId: Int
)