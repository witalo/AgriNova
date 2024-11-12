package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agrinova.data.local.entity.CampaniaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaniaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampania(campania: CampaniaEntity)

    @Update
    suspend fun updateCampania(campania: CampaniaEntity)

    @Query("SELECT * FROM campania WHERE id = :campaniaId")
    suspend fun getCampaniaById(campaniaId: Int): CampaniaEntity?

    @Query("SELECT * FROM campania")
    fun getAllCampanias(): Flow<List<CampaniaEntity>>

}