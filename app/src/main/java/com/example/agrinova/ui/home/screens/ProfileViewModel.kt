package com.example.agrinova.ui.home.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrinova.data.repository.UsuarioRepository
import com.example.agrinova.di.UsePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val usePreferences: UsePreferences,
) : ViewModel() {

    data class ProfileUiState(
        val userId: Int? = null,
        val userFirstName: String = "",
        val userLastName: String = "",
        val userDni: String = "",
        val userPhone: String = "",
        val userEmail: String = "",
        val userActive: Boolean = false,
        val isLoading: Boolean = true
    )

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            combine(
                usePreferences.userId,
                usePreferences.userFirstName,
                usePreferences.userLastName,
                usePreferences.userDni,
                usePreferences.userPhone,
                usePreferences.userEmail,
                usePreferences.userActive
            ) { values: Array<Any?> ->
                // Mapea cada valor en `values` para asignarlos al estado del UI
                ProfileUiState(
                    userId = values[0] as? Int ?: 0,
                    userFirstName = values[1] as? String ?: "",
                    userLastName = values[2] as? String ?: "",
                    userDni = values[3] as? String ?: "",
                    userPhone = values[4] as? String ?: "",
                    userEmail = values[5] as? String ?: "",
                    userActive =  values[6] as? Boolean ?: false,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

}