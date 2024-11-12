package com.example.agrinova.di.models

data class CartillaEvaluacionDomainModel(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val activo: Boolean,
    val cultivoId: Int,
//    val userFundoSet: List<UserFundo>
)