package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.agrinova.data.remote.model.EmpresaDataModel
import com.example.agrinova.data.remote.model.UsuarioDataModel
import com.example.agrinova.di.models.FundoDomainModel
import com.example.agrinova.di.models.UsuarioDomainModel

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
    fun toDomainModel(): UsuarioDomainModel {
        return UsuarioDomainModel(
            id = this.id,
            firstName = this.firstName,
            lastName = this.lastName,
            document = this.document,
            phone = this.phone,
            email = this.email,
            isActive = this.isActive
        )
    }
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