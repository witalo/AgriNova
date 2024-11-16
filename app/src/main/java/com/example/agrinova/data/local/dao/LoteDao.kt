package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agrinova.data.dto.LoteModuloDto
import com.example.agrinova.data.local.entity.LoteEntity
import com.example.agrinova.data.local.entity.ModuloEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLote(lote: LoteEntity)

    @Update
    suspend fun updateLote(lote: LoteEntity)

    @Query("SELECT * FROM lote WHERE id = :loteId")
    suspend fun getLoteById(loteId: Int): LoteEntity?

    @Query("SELECT * FROM lote")
    fun getAllLotes(): Flow<List<LoteEntity>>

    @Query("SELECT l.id as loteId, l.codigo as loteCodigo, l.nombre as loteNombre, " +
            "m.codigo as moduloCodigo, m.nombre as moduloNombre " +
            "FROM lote l INNER JOIN modulo m ON m.id = l.moduloId WHERE m.fundoId = :fundoId")
    fun getAllLotesByFundo(fundoId: Int):   Flow<List<LoteModuloDto>>

}