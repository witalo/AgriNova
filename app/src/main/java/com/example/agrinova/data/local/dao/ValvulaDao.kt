package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

}