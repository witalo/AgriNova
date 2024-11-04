package com.example.agrinova.data.remote.model

import com.example.agrinova.data.local.entity.UsuarioFundoCrossRef

data class UsuarioFundoDataModel(
    val userId: Int,
    val fundoId: Int
){
    fun toEntity(): UsuarioFundoCrossRef {
        return UsuarioFundoCrossRef(
            usuarioId = this.userId,
            fundoId = this.fundoId
        )
    }
}