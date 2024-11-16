package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agrinova.data.local.entity.GrupoVariableEntity
import com.example.agrinova.data.local.entity.VariableGrupoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VariableGrupoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariableGrupo(variableGrupo: VariableGrupoEntity)

    @Update
    suspend fun updateVariableGrupo(variableGrupo: VariableGrupoEntity)

    @Query("SELECT * FROM variablegrupo WHERE id = :variableGrupoId")
    suspend fun getVariableGrupoById(variableGrupoId: Int): VariableGrupoEntity?

    @Query("SELECT * FROM variablegrupo")
    fun getAllVariablesGrupo(): Flow<List<VariableGrupoEntity>>

    @Query("SELECT * FROM variablegrupo WHERE grupoVariableId = :grupoVariableId")
    fun getVariablesGrupoByGrupoVariableId(grupoVariableId: Int):   Flow<List<VariableGrupoEntity>>
}