package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agrinova.data.local.entity.ModuloEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModuloDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModulo(modulo: ModuloEntity)

    @Update
    suspend fun updateModulo(modulo: ModuloEntity)

    @Query("SELECT * FROM modulo WHERE id = :moduloId")
    suspend fun getModuloById(moduloId: Int): ModuloEntity?

    @Query("SELECT * FROM modulo")
    fun getAllModulos(): Flow<List<ModuloEntity>>

}