package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agrinova.data.dto.LoteModuloDto
import com.example.agrinova.data.local.entity.GrupoVariableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrupoVariableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrupoVariable(grupoVariable: GrupoVariableEntity)

    @Update
    suspend fun updateGrupoVariable(grupoVariable: GrupoVariableEntity)

    @Query("SELECT * FROM grupovariable WHERE id = :grupoVariableId")
    suspend fun getGrupoVariableById(grupoVariableId: Int): GrupoVariableEntity?

    @Query("SELECT * FROM grupovariable")
    fun getAllGruposVaribale(): Flow<List<GrupoVariableEntity>>

    @Query("SELECT * FROM grupovariable WHERE cartillaEvaluacionId = :cartillaId")
    fun getGruposVariableByCartillaId(cartillaId: Int):   Flow<List<GrupoVariableEntity>>

    @Query("DELETE FROM grupovariable")
    suspend fun clearAll()
}