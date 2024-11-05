package com.example.agrinova.di.models

data class UsuarioDomainModel(
    val id: Int,
    val document: String,
    val firstName : String,
    val lastName: String,
    val phone: String,
    val email: String,
    val isActive: Boolean
)