package com.example.agrinova.di

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.agrinova.data.local.AppDatabase
import com.example.agrinova.data.local.dao.CampaniaDao
import com.example.agrinova.data.local.dao.CartillaEvaluacionDao
import com.example.agrinova.data.local.dao.CultivoDao
import com.example.agrinova.data.local.dao.DatoDao
import com.example.agrinova.data.local.dao.DatoDetalleDao
import com.example.agrinova.data.local.dao.EmpresaDao
import com.example.agrinova.data.local.dao.FundoDao
import com.example.agrinova.data.local.dao.GrupoVariableDao
import com.example.agrinova.data.local.dao.LoteDao
import com.example.agrinova.data.local.dao.ModuloDao
import com.example.agrinova.data.local.dao.PoligonoDao
import com.example.agrinova.data.local.dao.UsuarioDao
import com.example.agrinova.data.local.dao.ValvulaDao
import com.example.agrinova.data.local.dao.VariableGrupoDao
import com.example.agrinova.data.local.dao.ZonaDao
import com.example.agrinova.data.remote.GraphQLClient
import com.example.agrinova.data.repository.EmpresaRepository
import com.example.agrinova.ui.home.screens.ProfileViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideEmpresaDao(appDatabase: AppDatabase): EmpresaDao {
        return appDatabase.empresaDao()
    }

    @Provides
    @Singleton
    fun provideUsuarioDao(appDatabase: AppDatabase): UsuarioDao {
        return appDatabase.usuarioDao()
    }

    @Provides
    @Singleton
    fun provideZonaDao(appDatabase: AppDatabase): ZonaDao {
        return appDatabase.zonaDao()
    }

    @Provides
    @Singleton
    fun provideFundoDao(appDatabase: AppDatabase): FundoDao {
        return appDatabase.fundoDao()
    }

    @Provides
    @Singleton
    fun provideModuloDao(appDatabase: AppDatabase): ModuloDao {
        return appDatabase.moduloDao()
    }
    @Provides
    @Singleton
    fun provideLoteDao(appDatabase: AppDatabase): LoteDao {
        return appDatabase.loteDao()
    }
    @Provides
    @Singleton
    fun provideCampaniaDao(appDatabase: AppDatabase): CampaniaDao {
        return appDatabase.campaniaDao()
    }
    @Provides
    @Singleton
    fun provideValvulaDao(appDatabase: AppDatabase): ValvulaDao {
        return appDatabase.valvulaDao()
    }
    @Provides
    @Singleton
    fun providePoligonoDao(appDatabase: AppDatabase): PoligonoDao {
        return appDatabase.poligonoDao()
    }
    @Provides
    @Singleton
    fun provideCultivoDao(appDatabase: AppDatabase): CultivoDao {
        return appDatabase.cultivoDao()
    }
    @Provides
    @Singleton
    fun provideCartillaEvaluacionDao(appDatabase: AppDatabase): CartillaEvaluacionDao {
        return appDatabase.cartillaEvaluacionDao()
    }
    @Provides
    @Singleton
    fun provideGrupoVariableDao(appDatabase: AppDatabase): GrupoVariableDao {
        return appDatabase.grupoVariableDao()
    }
    @Provides
    @Singleton
    fun provideVariableGrupoDao(appDatabase: AppDatabase): VariableGrupoDao {
        return appDatabase.variableGrupoDao()
    }
    @Provides
    @Singleton
    fun provideDatoDao(appDatabase: AppDatabase): DatoDao {
        return appDatabase.datoDao()
    }
    @Provides
    @Singleton
    fun provideDatoDetalleDao(appDatabase: AppDatabase): DatoDetalleDao {
        return appDatabase.datoDetalleDao()
    }

    @Provides
    @Singleton
    fun provideEmpresaRepository(
        empresaDao: EmpresaDao,
        usuarioDao: UsuarioDao,
        zonaDao: ZonaDao,
        fundoDao: FundoDao,
        moduloDao: ModuloDao,
        loteDao: LoteDao,
        campaniaDao: CampaniaDao,
        valvulaDao: ValvulaDao,
        poligonoDao: PoligonoDao,
        cartillaEvaluacionDao: CartillaEvaluacionDao,
        grupoVariableDao: GrupoVariableDao,
        variableGrupoDao: VariableGrupoDao,
        cultivoDao: CultivoDao,
        datoDao: DatoDao,
        datoDetalleDao: DatoDetalleDao,
        ): EmpresaRepository {
        // Usa GraphQLClient.apolloClient directamente
        return EmpresaRepository(
            empresaDao = empresaDao,
            usuarioDao = usuarioDao,
            zonaDao = zonaDao,
            fundoDao = fundoDao,
            moduloDao = moduloDao,
            loteDao = loteDao,
            campaniaDao = campaniaDao,
            valvulaDao = valvulaDao,
            poligonoDao = poligonoDao,
            cartillaDao = cartillaEvaluacionDao,
            grupoVariableDao = grupoVariableDao,
            variableGrupoDao = variableGrupoDao,
            cultivoDao = cultivoDao,
            datoDao = datoDao,
            datoDetalleDao = datoDetalleDao,
            graphQLClient = GraphQLClient.apolloClient
        )
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UsePreferences {
        return UsePreferences(context)
    }
}