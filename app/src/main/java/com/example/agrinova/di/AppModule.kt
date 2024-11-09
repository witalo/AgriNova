package com.example.agrinova.di

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.agrinova.data.local.AppDatabase
import com.example.agrinova.data.local.dao.EmpresaDao
import com.example.agrinova.data.local.dao.FundoDao
import com.example.agrinova.data.local.dao.UsuarioDao
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
    fun provideEmpresaRepository(
        empresaDao: EmpresaDao,
        usuarioDao: UsuarioDao,
        zonaDao: ZonaDao,
        fundoDao: FundoDao
    ): EmpresaRepository {
        // Usa GraphQLClient.apolloClient directamente
        return EmpresaRepository(
            empresaDao = empresaDao,
            usuarioDao = usuarioDao,
            zonaDao = zonaDao,
            fundoDao = fundoDao,
            graphQLClient = GraphQLClient.apolloClient
        )
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UsePreferences {
        return UsePreferences(context)
    }
//    @Provides
//    @Singleton
//    fun provideFundoRepository(fundoDao: FundoDao): FundoRepository {
//        return FundoRepository(fundoDao)
//    }

//    @Provides
//    @Singleton
//    fun provideFundoDao(appDatabase: AppDatabase): FundoDao {
//        return appDatabase.fundoDao() // Asegúrate de que tienes este método en tu AppDatabase
//    }
}