package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.agrinova.data.local.entity.EmpresaEntity

@Dao
interface EmpresaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmpresa(empresa: EmpresaEntity)

    @Update
    suspend fun updateEmpresa(empresa: EmpresaEntity)

    @Transaction
    suspend fun upsertEmpresa(empresa: EmpresaEntity) {
        val existingEmpresa = getEmpresaById(empresa.id)
        if (existingEmpresa != null) {
            updateEmpresa(empresa)
        } else {
            insertEmpresa(empresa)
        }
    }

    @Query("SELECT * FROM empresa WHERE id = :empresaId")
    suspend fun getEmpresaById(empresaId: Int): EmpresaEntity?

    @Query("DELETE FROM empresa")
    suspend fun clearAll()
}