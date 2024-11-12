package com.example.agrinova.di.models

data class CampaniaDomainModel(
    val id: Int,
    val numero: Int,
    val centroCosto: String,
    val activo: Boolean,
    val loteId: Int,
    val cultivoId: Int
//    val userFundoSet: List<UserFundo>
)