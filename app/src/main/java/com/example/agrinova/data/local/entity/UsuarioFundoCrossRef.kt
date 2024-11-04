package com.example.agrinova.data.local.entity

import androidx.room.Entity

@Entity(primaryKeys = ["usuarioId", "fundoId"])
data class UsuarioFundoCrossRef(
    val usuarioId: Int,
    val fundoId: Int
)