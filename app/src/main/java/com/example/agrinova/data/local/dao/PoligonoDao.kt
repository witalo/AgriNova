package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agrinova.data.local.entity.PoligonoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PoligonoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoligono(poligono: PoligonoEntity)

    @Update
    suspend fun updatePoligono(poligono: PoligonoEntity)

    @Query("SELECT * FROM poligono WHERE id = :poligonoId")
    suspend fun getPoligonoById(poligonoId: Int): PoligonoEntity?

    @Query("SELECT * FROM poligono")
    fun getAllPoligonos(): Flow<List<PoligonoEntity>>

    @Query("DELETE FROM poligono")
    suspend fun clearAll()

    @Query("SELECT * FROM poligono WHERE valvulaId = :valvulaId ORDER BY id")
    suspend fun getPoligonosByValvulaId(valvulaId: Int): List<PoligonoEntity>
}