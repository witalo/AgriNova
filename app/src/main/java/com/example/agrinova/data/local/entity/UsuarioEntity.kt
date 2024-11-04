package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class UsuarioEntity(
    @PrimaryKey val id: Int,
    val document: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
    val isActive: Boolean
)