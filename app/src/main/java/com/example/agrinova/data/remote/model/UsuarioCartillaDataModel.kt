package com.example.agrinova.data.remote.model

import com.example.agrinova.data.local.entity.UsuarioCartillaCrossRef

data class UsuarioCartillaDataModel(
    val usuarioId: Int,
    val cartillaId: Int
){
    fun toEntity(): UsuarioCartillaCrossRef {
        return UsuarioCartillaCrossRef(
            usuarioId = this.usuarioId,
            cartillaId = this.cartillaId
        )
    }
}