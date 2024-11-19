package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agrinova.data.local.entity.FundoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FundoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFundo(fundo: FundoEntity)

    @Update
    suspend fun updateFundo(fundo: FundoEntity)

    @Query("SELECT * FROM fundo WHERE id = :fundoId")
    suspend fun getFundoById(fundoId: Int): FundoEntity?

    @Query("SELECT * FROM fundo")
    fun getAllFundos(): Flow<List<FundoEntity>>

    @Query("DELETE FROM fundo")
    suspend fun clearAll()
}