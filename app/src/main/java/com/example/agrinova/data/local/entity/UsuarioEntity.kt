package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.agrinova.data.remote.model.EmpresaDataModel
import com.example.agrinova.data.remote.model.UsuarioDataModel

@Entity(tableName = "usuario")
data class UsuarioEntity(
    @PrimaryKey val id: Int,
    val document: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
    val isActive: Boolean
){
    companion object {
        fun fromDataModel(dataModel: UsuarioDataModel): UsuarioEntity {
            return UsuarioEntity(
                id = dataModel.id,
                document = dataModel.document,
                firstName = dataModel.firstName,
                lastName = dataModel.lastName,
                phone = dataModel.phone,
                email = dataModel.email,
                isActive = dataModel.isActive
            )
        }
    }
}