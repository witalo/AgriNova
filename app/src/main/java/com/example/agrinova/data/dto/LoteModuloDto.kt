package com.example.agrinova.data.dto


data class LoteModuloDto(
    val id: Int,               // ID único del lote
    val codigo: String,        // Código del lote
    val nombre: String,        // Nombre del lote
    val moduloCodigo: String,  // Código del módulo asociado
    val moduloNombre: String   // Nombre del módulo asociado
)
