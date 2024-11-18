package com.example.agrinova.data.dto

data class MuestraVGInput(
    val muestra: Float,
    val latitud: Float,
    val longitud: Float,
    val fecha_hora: String,
    val variable_grupo_id: Int,
    val valvula_id: Int
)