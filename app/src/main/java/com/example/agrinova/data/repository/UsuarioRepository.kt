package com.example.agrinova.data.repository
import com.example.agrinova.data.local.dao.UsuarioDao
import com.example.agrinova.data.local.entity.UsuarioEntity
import com.example.agrinova.di.models.UsuarioDomainModel
import javax.inject.Inject


class UsuarioRepository @Inject constructor(
    private val usuarioDao: UsuarioDao
) {
    suspend fun validateUser(dni: String, fundoId: Int): UsuarioDomainModel {
        val usuarioEntity = usuarioDao.isUserAssociatedWithFundo(dni, fundoId)
        return usuarioEntity?.let {
            UsuarioDomainModel(
                id = it.id,
                document = it.document,
                firstName = it.firstName?: "",
                lastName = it.lastName ?: "",
                phone = it.phone,
                email = it.email,
                isActive = it.isActive
            )
        } ?: throw IllegalArgumentException("Usuario no encontrado")
    }
//    suspend fun validateUser(dni: String, fundoId: Int): Boolean {
//        return usuarioDao.isUserAssociatedWithFundo(dni, fundoId)
//    }
    suspend fun getCurrentUserId(): Int {
        val usuario = usuarioDao.getCurrentUser()
        return usuario?.id ?: throw IllegalStateException("No se ha iniciado sesión")
    }

    suspend fun getUserById(userId: Int): UsuarioDomainModel {
        val usuarioEntity = usuarioDao.getUsuarioById(userId)
        return usuarioEntity?.let {
            UsuarioDomainModel(
                id = it.id,
                document = it.document,
                firstName = it.firstName.split(" ").firstOrNull() ?: "",
                lastName = it.lastName.split(" ").lastOrNull() ?: "",
                phone = "",
                email = "",
                isActive = true
            )
        } ?: throw IllegalArgumentException("Usuario no encontrado")
    }

    fun getUserStatusText(active: Boolean?): String {
        return if (active == true) {
            "Activo"
        } else {
            "Inactivo"
        }
    }
}