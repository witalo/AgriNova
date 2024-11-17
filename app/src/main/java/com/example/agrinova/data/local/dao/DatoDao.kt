package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agrinova.data.local.entity.DatoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DatoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDato(dato: DatoEntity)

    @Update
    suspend fun updateDato(dato: DatoEntity)

    @Query("SELECT * FROM dato WHERE id = :datoId")
    suspend fun getDatoById(datoId: Int): DatoEntity?

    @Query("SELECT * FROM dato WHERE fecha = :fecha")
    fun getDatosByDate(fecha: String): Flow<List<DatoEntity>>

}