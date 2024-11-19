package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agrinova.data.dto.LoteModuloDto
import com.example.agrinova.data.local.entity.ValvulaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ValvulaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertValvula(valvula: ValvulaEntity)

    @Update
    suspend fun updateValvula(valvula: ValvulaEntity)

    @Query("SELECT * FROM valvula WHERE id = :valvulaId")
    suspend fun getValvulaById(valvulaId: Int): ValvulaEntity?

    @Query("SELECT * FROM valvula")
    fun getAllValvulas(): Flow<List<ValvulaEntity>>

    @Query("SELECT valvula.id, valvula.codigo, valvula.nombre, valvula.campaniaId, valvula.activo " +
            "FROM valvula " +
            "WHERE valvula.campaniaId = (SELECT campania.id FROM campania WHERE campania.loteId = :loteId AND campania.activo = 1 ORDER BY campania.id DESC LIMIT 1)")
    fun getValvulasByLoteId(loteId: Int): Flow<List<ValvulaEntity>>

    @Query("DELETE FROM valvula")
    suspend fun clearAll()
}