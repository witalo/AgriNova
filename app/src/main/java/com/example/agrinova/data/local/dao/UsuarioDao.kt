package com.example.agrinova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agrinova.data.local.entity.UsuarioEntity
import com.example.agrinova.data.local.entity.UsuarioFundoCrossRef

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: UsuarioEntity)

    @Update
    suspend fun updateUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuario WHERE id = :id")
    suspend fun getUsuarioById(id: Int): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsuarioFundoCrossRef(usuarioFundoCrossRef: UsuarioFundoCrossRef)

    @Query("SELECT COUNT(*) FROM UsuarioFundoCrossRef WHERE usuarioId = :userId AND fundoId = :fundoId")
    suspend fun checkUsuarioFundoExists(userId: Int, fundoId: Int): Int


    @Query("SELECT * FROM usuario WHERE document = :document")
    suspend fun getUsuarioByDocument(document: String): UsuarioEntity?

    // Verificar si un usuario está asociado con un fundo específico
//    @Query(
//        """
//        SELECT EXISTS (
//            SELECT 1 FROM UsuarioFundoCrossRef
//            WHERE usuarioId = (
//                SELECT usuarioId FROM usuario
//                WHERE document = :dni
//                AND isActive = 1  -- Verificar si el usuario está activo
//                LIMIT 1
//            )
//            AND fundoId = :fundoId
//        )
//    """
//    )
//    suspend fun isUserAssociatedWithFundo(dni: String, fundoId: Int): Boolean
    @Query(
        """
        SELECT u.id, u.firstName, u.lastName, u.document, u.phone, u.email, u.isActive 
        FROM usuario u
        JOIN UsuarioFundoCrossRef uf ON u.id = uf.usuarioId
        WHERE u.document = :dni 
        AND u.isActive = 1  -- Verificar si el usuario está activo
        AND uf.fundoId = :fundoId
        LIMIT 1
    """
    )
    suspend fun isUserAssociatedWithFundo(dni: String, fundoId: Int): UsuarioEntity?
    @Query("SELECT * FROM usuario WHERE id = 1 LIMIT 1")
    suspend fun getCurrentUser(): UsuarioEntity?
}