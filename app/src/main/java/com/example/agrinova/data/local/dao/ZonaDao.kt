package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agrinova.data.local.entity.ZonaEntity

@Dao
interface ZonaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZona(zona: ZonaEntity)

    @Update
    suspend fun updateZona(zona: ZonaEntity)

    @Query("SELECT * FROM zona WHERE id = :zonaId")
    suspend fun getZonaById(zonaId: Int): ZonaEntity?
}