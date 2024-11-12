package com.example.agrinova.di.models

data class ValvulaDomainModel(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val activo: Boolean,
    val campaniaId: Int,
//    val userFundoSet: List<UserFundo>
)