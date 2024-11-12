package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agrinova.data.local.entity.MuestraVGEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MuestraVGDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMuestraVG(muestraVG: MuestraVGEntity)

    @Update
    suspend fun updateMuestraVG(muestraVG: MuestraVGEntity)

    @Query("SELECT * FROM muestravg WHERE id = :muestraVGId")
    suspend fun getMuestraVGById(muestraVGId: Int): MuestraVGEntity?

    @Query("SELECT * FROM muestravg")
    fun getAllMuestrasVGId(): Flow<List<MuestraVGEntity>>

}