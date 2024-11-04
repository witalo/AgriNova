package com.example.agrinova.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")
@Singleton
class UsePreferences @Inject constructor(
    context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val COMPANY_REGISTERED = booleanPreferencesKey("company_registered")
        private val COMPANY_NAME = stringPreferencesKey("company_name")
        private val COMPANY_ID = intPreferencesKey("company_id")
    }

    val isCompanyRegistered: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[COMPANY_REGISTERED] ?: false
    }

    val companyName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[COMPANY_NAME]
    }

    val companyId: Flow<Int?> = dataStore.data.map { preferences ->
        preferences[COMPANY_ID]
    }

    suspend fun saveCompanyData(name: String, id: Int) {
        dataStore.edit { preferences ->
            preferences[COMPANY_REGISTERED] = true
            preferences[COMPANY_NAME] = name
            preferences[COMPANY_ID] = id
        }
    }

    suspend fun clearCompanyData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}