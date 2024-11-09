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
        // User related keys
        private val USER_ID = intPreferencesKey("user_id")
        private val USER_FIRST_NAME= stringPreferencesKey("user_first_name")
        private val USER_LAST_NAME = stringPreferencesKey("user_last_name")
        private val USER_DNI = stringPreferencesKey("user_dni")
        private val USER_PHONE = stringPreferencesKey("user_phone")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_ACTIVE = booleanPreferencesKey("user_active")
        private val FUNDO_ID = intPreferencesKey("fundo_id")
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
    suspend fun saveUserData(
        id: Int,
        firstName: String,
        lastName: String,
        dni: String,
        phone: String,
        email: String,
        active: Boolean,
        fundoId: Int
    ) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = id
            preferences[USER_FIRST_NAME] = firstName
            preferences[USER_LAST_NAME] = lastName
            preferences[USER_DNI] = dni
            preferences[USER_PHONE] = phone
            preferences[USER_EMAIL] = email
            preferences[USER_ACTIVE] = active
            preferences[FUNDO_ID] = fundoId
        }
    }
    // New user related flows
    val userId: Flow<Int?> = dataStore.data.map { preferences ->
        preferences[USER_ID]
    }
    val userFirstName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_FIRST_NAME]
    }
    val userLastName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_LAST_NAME]
    }
    val userDni: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_DNI]
    }
    val userPhone: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_PHONE]
    }
    val userEmail: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_EMAIL]
    }
    val userActive: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[USER_ACTIVE]
    }
    val fundoId: Flow<Int?> = dataStore.data.map { preferences ->
        preferences[FUNDO_ID]
    }
    suspend fun clearCompanyData() {
        dataStore.edit { preferences ->
            preferences.remove(COMPANY_REGISTERED)
            preferences.remove(COMPANY_NAME)
            preferences.remove(COMPANY_ID)
        }
    }
    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID)
            preferences.remove(USER_FIRST_NAME)
            preferences.remove(USER_LAST_NAME)
            preferences.remove(USER_DNI)
            preferences.remove(USER_PHONE)
            preferences.remove(USER_EMAIL)
            preferences.remove(USER_ACTIVE)
            preferences.remove(FUNDO_ID)
        }
    }
    suspend fun clearAllData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}