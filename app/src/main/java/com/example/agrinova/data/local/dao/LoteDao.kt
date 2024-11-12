package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agrinova.data.local.entity.LoteEntity
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

}