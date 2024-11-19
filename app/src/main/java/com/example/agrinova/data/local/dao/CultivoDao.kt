package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agrinova.data.local.entity.CultivoEntity

@Dao
interface CultivoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCultivo(cultivo: CultivoEntity)

    @Update
    suspend fun updateCultivo(cultivo: CultivoEntity)

    @Query("SELECT * FROM cultivo WHERE id = :cultivoId")
    suspend fun getCultivoById(cultivoId: Int): CultivoEntity?

    @Query("DELETE FROM cultivo")
    suspend fun clearAll()
}