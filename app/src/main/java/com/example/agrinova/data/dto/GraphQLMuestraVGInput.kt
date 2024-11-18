package com.example.agrinova.data.dto

data class MuestraVGInputDto(
    val muestra: Float,
    val latitud: Float,
    val longitud: Float,
    val fecha_hora: String,
    val variable_grupo_id: Int,
    val valvula_id: Int
) {
    fun toGraphQLInput(): GraphQLMuestraVGInput {
        return GraphQLMuestraVGInput(
            muestra = this.muestra.toDouble(),
            latitud = this.latitud.toDouble(),
            longitud = this.longitud.toDouble(),
            fecha_hora = this.fecha_hora,
            variable_grupo_id = this.variable_grupo_id,
            valvula_id = this.valvula_id
        )
    }
}