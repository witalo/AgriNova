package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agrinova.data.local.entity.CartillaEvaluacionEntity
import com.example.agrinova.data.local.entity.UsuarioCartillaCrossRef

@Dao
interface CartillaEvaluacionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartillaEvaluacion(cartillaevaluacion: CartillaEvaluacionEntity)

    @Update
    suspend fun updateCartillaEvaluacion(cartillaevaluacion: CartillaEvaluacionEntity)

    @Query("SELECT * FROM cartillaevaluacion WHERE id = :id")
    suspend fun getCartillaEvaluacionById(id: Int): CartillaEvaluacionEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsuarioCartillaCrossRef(usuarioCartillaCrossRef: UsuarioCartillaCrossRef)

    @Query("SELECT COUNT(*) FROM UsuarioCartillaCrossRef WHERE usuarioId = :userId AND cartillaId = :cartillaId")
    suspend fun checkUsuarioCartillaExists(userId: Int, cartillaId: Int): Int
}