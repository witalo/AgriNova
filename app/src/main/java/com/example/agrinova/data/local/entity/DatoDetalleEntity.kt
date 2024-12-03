package com.example.agrinova.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agrinova.di.models.DatoDetalleDomainModel
import java.util.Locale
import kotlin.math.roundToInt

@Entity(
    tableName = "datodetalle",
    foreignKeys = [
        ForeignKey(
            entity = VariableGrupoEntity::class,
            parentColumns = ["id"],
            childColumns = ["variableGrupoId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DatoEntity::class,
            parentColumns = ["id"],
            childColumns = ["datoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["variableGrupoId"]),
        Index(value = ["datoId"])
    ]
)
data class DatoDetalleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val muestra: Float,
    val latitud: Float,
    val longitud: Float,
    val datoId: Int,
    val variableGrupoId: Int
){
    fun toDomainModel(): DatoDetalleDomainModel {
        return DatoDetalleDomainModel(
            id = this.id,
            muestra = this.muestra.roundTo2Decimals(),
            latitud = this.latitud,
            longitud = this.longitud,
            datoId = this.datoId,
            variableGrupoId = this.variableGrupoId
        )
    }
}
// Extensión para redondear Float a 2 decimales
fun Float.roundTo2Decimals(): Float {
    return (this * 100).roundToInt() / 100f
}