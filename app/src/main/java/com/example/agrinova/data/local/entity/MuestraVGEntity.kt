package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agrinova.di.models.MuestraVGDomainModel

@Entity(
    tableName = "muestravg",
    foreignKeys = [
        ForeignKey(
            entity = VariableGrupoEntity::class,
            parentColumns = ["id"],
            childColumns = ["variableGrupoId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["variableGrupoId"])]
)
data class MuestraVGEntity(
    @PrimaryKey val id: Int,
    val muestra: Float,
    val fecha: String,
    val latitud: Float,
    val longitud: Float,
    val variableGrupoId: Int
){
    fun toDomainModel(): MuestraVGDomainModel {
        return MuestraVGDomainModel(
            id = this.id,
            muestra = this.muestra,
            fecha = this.fecha,
            latitud = this.latitud,
            longitud = this.longitud,
            variableGrupoId = this.variableGrupoId
        )
    }
}