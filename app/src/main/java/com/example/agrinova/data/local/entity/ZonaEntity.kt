package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "zona",
    foreignKeys = [
        ForeignKey(
            entity = EmpresaEntity::class,
            parentColumns = ["id"],
            childColumns = ["empresaId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["empresaId"])]
)
data class ZonaEntity(
    @PrimaryKey val id: Int,
    val codigo: String,
    val nombre: String,
    val empresaId: Int,
    val activo: Boolean
)