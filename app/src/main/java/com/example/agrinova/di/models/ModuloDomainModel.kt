package com.example.agrinova.di.models

data class ModuloDomainModel(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val activo: Boolean,
    val fundoId: Int,
//    val userFundoSet: List<UserFundo>
)