package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.agrinova.data.dto.DatoValvulaDto
import com.example.agrinova.data.dto.DatoWithDetalle
import com.example.agrinova.data.local.entity.DatoDetalleEntity
import com.example.agrinova.data.local.entity.DatoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DatoDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertDato(dato: DatoEntity)

//    @Update
//    suspend fun updateDato(dato: DatoEntity): Long

    @Query("SELECT * FROM dato WHERE id = :datoId")
    suspend fun getDatoById(datoId: Int): DatoEntity?

//    @Query("SELECT * FROM dato WHERE DATE(fecha) = DATE(:fecha) AND cartillaId = :cartillaId")
//    fun getDatosByDateAndCartillaId(fecha: String, cartillaId: Int): Flow<List<DatoEntity>>

    @Query("SELECT d.id as datoId, d.fecha as datoFecha, d.cartillaId as cartillaId, v.id as valvulaId, v.codigo as valvulaCodigo, l.id as loteId, l.codigo as loteCodigo FROM dato d INNER JOIN valvula v ON v.id = d.valvulaId INNER JOIN campania c ON c.id = v.campaniaId  INNER JOIN lote l ON l.id = c.loteId WHERE DATE(d.fecha) = DATE(:fecha) AND d.cartillaId = :cartillaId")
    fun getDatosByDateAndCartillaId(fecha: String, cartillaId: Int): Flow<List<DatoValvulaDto>>

    @Insert
    suspend fun insertDato(dato: DatoEntity): Long

    @Insert
    suspend fun insertDatoDetalles(detalles: List<DatoDetalleEntity>)

    @Transaction
    suspend fun insertDatoWithDetalles(dato: DatoEntity, detalles: List<DatoDetalleEntity>) {
        val datoId = insertDato(dato)
        val detallesWithDatoId = detalles.map { it.copy(datoId = datoId.toInt()) }
        insertDatoDetalles(detallesWithDatoId)
    }
    @Query(
        """
        SELECT d.id AS datoId, d.valvulaId, d.fecha, dd.muestra, dd.latitud, dd.longitud, dd.variableGrupoId
        FROM dato d
        INNER JOIN datodetalle dd ON d.id = dd.datoId
        WHERE DATE(d.fecha) = DATE(:fecha) AND d.cartillaId = :cartillaId
        """
    )
    suspend fun getDatoWithDetalleByDateAndCartillaId(
        fecha: String,
        cartillaId: Int
    ): List<DatoWithDetalle>
}