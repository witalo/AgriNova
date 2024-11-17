package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agrinova.data.local.entity.DatoDetalleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DatoDetalleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatoDetalle(datoDetalle: DatoDetalleEntity)

    @Update
    suspend fun updateDatoDetalle(datoDetalle: DatoDetalleEntity)

    @Query("SELECT * FROM datodetalle WHERE id = :datoDetalleId")
    suspend fun getDatoDetalleById(datoDetalleId: Int): DatoDetalleEntity?

    @Query("SELECT * FROM datodetalle WHERE datoId = :datoId")
    fun getDatosDetalleByDatoId(datoId: Int): Flow<List<DatoDetalleEntity>>

}