package com.example.agrinova.di

import android.content.Context
import com.example.agrinova.data.local.AppDatabase
import com.example.agrinova.data.local.dao.EmpresaDao
import com.example.agrinova.data.local.dao.FundoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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