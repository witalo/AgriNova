package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.agrinova.data.remote.model.EmpresaDataModel


@Entity(tableName = "empresa")
data class EmpresaEntity(
    @PrimaryKey val id: Int,
    val ruc: String,
    val razonSocial: String,
    val direccion: String,
    val telefono: String,
    val correo: String
){
    companion object {
        fun fromDataModel(dataModel: EmpresaDataModel): EmpresaEntity {
            return EmpresaEntity(
                id = dataModel.id,
                ruc = dataModel.ruc,
                razonSocial = dataModel.razonSocial,
                direccion = dataModel.direccion,
                telefono = dataModel.telefono,
                correo = dataModel.correo
            )
        }
    }
}