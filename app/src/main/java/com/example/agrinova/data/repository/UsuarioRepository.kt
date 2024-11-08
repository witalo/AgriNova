package com.example.agrinova.data.repository
import com.example.agrinova.data.local.dao.UsuarioDao
import javax.inject.Inject


class UsuarioRepository @Inject constructor(
    private val usuarioDao: UsuarioDao
) {
    suspend fun validateUser(dni: String, fundoId: Int): Boolean {
        return usuarioDao.isUserAssociatedWithFundo(dni, fundoId)
    }
}