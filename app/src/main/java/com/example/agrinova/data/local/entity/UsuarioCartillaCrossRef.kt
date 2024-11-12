package com.example.agrinova.data.local.entity

import androidx.room.Entity

@Entity(primaryKeys = ["usuarioId", "cartillaId"])
data class UsuarioCartillaCrossRef(
    val usuarioId: Int,
    val cartillaId: Int
)