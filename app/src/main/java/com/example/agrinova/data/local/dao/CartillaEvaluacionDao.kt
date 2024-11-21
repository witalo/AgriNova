package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.agrinova.data.local.entity.CartillaEvaluacionEntity
import com.example.agrinova.data.local.entity.FundoEntity
import com.example.agrinova.data.local.entity.UsuarioCartillaCrossRef
import kotlinx.coroutines.flow.Flow

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

    @Query("""
    SELECT cartillaevaluacion.*
    FROM cartillaevaluacion
    INNER JOIN UsuarioCartillaCrossRef 
    ON cartillaevaluacion.id = UsuarioCartillaCrossRef.cartillaId
    WHERE UsuarioCartillaCrossRef.usuarioId = :usuarioId
""")
    fun getCartillasByUsuarioId(usuarioId: Int): Flow<List<CartillaEvaluacionEntity>>

    @Query("DELETE FROM cartillaevaluacion")
    suspend fun clearAllCartilla()

    @Query("DELETE FROM UsuarioCartillaCrossRef")
    suspend fun clearAllUsuarioCartilla()

    @Transaction
    suspend fun clearAll() {
        clearAllUsuarioCartilla()
        clearAllCartilla()
    }
}