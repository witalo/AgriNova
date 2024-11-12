package com.example.agrinova.di.models

data class LoteDomainModel(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val activo: Boolean,
    val moduloId: Int,
//    val userFundoSet: List<UserFundo>
)