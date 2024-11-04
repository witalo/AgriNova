package com.example.agrinova.data.remote.model

import com.example.agrinova.data.local.entity.ZonaEntity


data class ZonaDataModel(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val activo: Boolean,
    val empresaId: Int,
    val fundoSet: List<FundoDataModel>?
){
    fun toEntity(): ZonaEntity {
        return ZonaEntity(
            id = this.id,
            codigo = this.codigo,
            nombre = this.nombre,
            activo = this.activo,
            empresaId = this.empresaId
        )
    }
}