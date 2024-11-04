package com.example.agrinova.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.agrinova.data.local.dao.EmpresaDao
import com.example.agrinova.data.local.dao.FundoDao
import com.example.agrinova.data.local.dao.UsuarioDao
import com.example.agrinova.data.local.dao.ZonaDao
import com.example.agrinova.data.local.entity.EmpresaEntity
import com.example.agrinova.data.local.entity.FundoEntity
import com.example.agrinova.data.local.entity.UsuarioEntity
import com.example.agrinova.data.local.entity.UsuarioFundoCrossRef
import com.example.agrinova.data.local.entity.ZonaEntity
import com.example.agrinova.util.Constants

@Database(entities = [
    EmpresaEntity::class,
    FundoEntity::class,
    ZonaEntity::class,
    UsuarioEntity::class,
    UsuarioFundoCrossRef::class],
    version = 1,
    exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun empresaDao(): EmpresaDao
    abstract fun fundoDao(): FundoDao
    abstract fun zonaDao(): ZonaDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}