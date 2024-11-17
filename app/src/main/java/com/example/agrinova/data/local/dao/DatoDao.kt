package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
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

    @Query("SELECT * FROM dato WHERE DATE(fecha) = DATE(:fecha) AND cartillaId = :cartillaId")
    fun getDatosByDateAndCartillaId(fecha: String, cartillaId: Int): Flow<List<DatoEntity>>

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
}