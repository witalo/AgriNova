package com.example.agrinova.di.models

data class EmpresaDomainModel(
    val id: Int,
    val ruc: String,
    val razonSocial: String,
    val direccion: String,
    val telefono: String,
    val correo: String,
//    val userSet: List<Usuario>,
//    val zonaSet: List<Zona>
)