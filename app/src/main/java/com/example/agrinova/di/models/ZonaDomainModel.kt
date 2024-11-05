package com.example.agrinova.di.models

data class ZonaDomainModel(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val activo: Boolean,
    val empresaId: Int,
//    val fundoSet: List<Fundo>
)