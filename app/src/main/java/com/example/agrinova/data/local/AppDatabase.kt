package com.example.agrinova.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.agrinova.data.local.dao.CampaniaDao
import com.example.agrinova.data.local.dao.CartillaEvaluacionDao
import com.example.agrinova.data.local.dao.CultivoDao
import com.example.agrinova.data.local.dao.EmpresaDao
import com.example.agrinova.data.local.dao.FundoDao
import com.example.agrinova.data.local.dao.GrupoVariableDao
import com.example.agrinova.data.local.dao.LoteDao
import com.example.agrinova.data.local.dao.ModuloDao
import com.example.agrinova.data.local.dao.MuestraVGDao
import com.example.agrinova.data.local.dao.PoligonoDao
import com.example.agrinova.data.local.dao.UsuarioDao
import com.example.agrinova.data.local.dao.ValvulaDao
import com.example.agrinova.data.local.dao.VariableGrupoDao
import com.example.agrinova.data.local.dao.ZonaDao
import com.example.agrinova.data.local.entity.CampaniaEntity
import com.example.agrinova.data.local.entity.CartillaEvaluacionEntity
import com.example.agrinova.data.local.entity.CultivoEntity
import com.example.agrinova.data.local.entity.EmpresaEntity
import com.example.agrinova.data.local.entity.FundoEntity
import com.example.agrinova.data.local.entity.GrupoVariableEntity
import com.example.agrinova.data.local.entity.LoteEntity
import com.example.agrinova.data.local.entity.ModuloEntity
import com.example.agrinova.data.local.entity.PoligonoEntity
import com.example.agrinova.data.local.entity.UsuarioCartillaCrossRef
import com.example.agrinova.data.local.entity.UsuarioEntity
import com.example.agrinova.data.local.entity.UsuarioFundoCrossRef
import com.example.agrinova.data.local.entity.ValvulaEntity
import com.example.agrinova.data.local.entity.VariableGrupoEntity
import com.example.agrinova.data.local.entity.ZonaEntity
import com.example.agrinova.data.local.entity.MuestraVGEntity
import com.example.agrinova.util.Constants

@Database(entities = [
    EmpresaEntity::class,
    FundoEntity::class,
    ZonaEntity::class,
    UsuarioEntity::class,
    UsuarioFundoCrossRef::class,
    CartillaEvaluacionEntity::class,
    UsuarioCartillaCrossRef::class,
    GrupoVariableEntity::class,
    VariableGrupoEntity::class,
    CultivoEntity::class,
    ModuloEntity::class,
    LoteEntity::class,
    CampaniaEntity::class,
    ValvulaEntity::class,
    PoligonoEntity::class,
    MuestraVGEntity::class],
    version = 1,
    exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun empresaDao(): EmpresaDao
    abstract fun fundoDao(): FundoDao
    abstract fun zonaDao(): ZonaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun cartillaEvaluacionDao(): CartillaEvaluacionDao
    abstract fun grupoVariableDao(): GrupoVariableDao
    abstract fun variableGrupoDao(): VariableGrupoDao
    abstract fun cultivoDao(): CultivoDao
    abstract fun moduloDao(): ModuloDao
    abstract fun loteDao(): LoteDao
    abstract fun campaniaDao(): CampaniaDao
    abstract fun valvulaDao(): ValvulaDao
    abstract fun poligonoDao(): PoligonoDao
    abstract fun muestraVGDao(): MuestraVGDao

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