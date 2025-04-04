package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Transaction
import com.example.agrinova.data.dto.DatoValvulaDto
import com.example.agrinova.data.dto.DatoWithDetalleDto
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
//    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
//    @Query(
//        """
//        SELECT d.id AS datoId, d.valvulaId, d.fecha, dd.muestra, dd.latitud, dd.longitud, dd.variableGrupoId
//        FROM dato d
//        INNER JOIN datodetalle dd ON d.id = dd.datoId
//        WHERE DATE(d.fecha) = DATE(:fecha) AND d.cartillaId = :cartillaId
//        """
//    )
//    suspend fun getDatoWithDetalleByDateAndCartillaId(
//        fecha: String,
//        cartillaId: Int
//    ): List<DatoWithDetalleDto>

    @Transaction
    @Query("""
        SELECT d.*, dd.*
        FROM dato d
        LEFT JOIN datodetalle dd ON d.id = dd.datoId
        WHERE d.cartillaId = :cartillaId AND DATE(d.fecha) = DATE(:fecha)
    """)
    suspend fun getDatosWithDetalles(cartillaId: Int, fecha: String): Map<DatoEntity, List<DatoDetalleEntity>>
    @Query("SELECT * FROM dato WHERE cartillaId = :cartillaId AND DATE(fecha) = DATE(:fecha)")
    suspend fun getDatosByCartilla(cartillaId: Int, fecha: String): List<DatoEntity>

    @Query("SELECT * FROM datodetalle WHERE datoId IN (:datoIds)")
    suspend fun getDatoDetallesByDatoIds(datoIds: List<Int>): List<DatoDetalleEntity>



    // Obtiene los IDs de Dato según la fecha y cartillaId
    @Query("SELECT id FROM dato WHERE cartillaId = :cartillaId AND DATE(fecha) = DATE(:fecha)")
    suspend fun getIdsDatoByDateAndCartillaId(fecha: String, cartillaId: Int): List<Int>
    // Elimina los registros en Dato
    @Query("DELETE FROM dato WHERE cartillaId = :cartillaId AND DATE(fecha) = DATE(:fecha)")
    suspend fun clearDatoByDateAndCartillaId(fecha: String, cartillaId: Int)

    @Query("DELETE FROM cultivo")
    suspend fun clearAll()
}