package com.example.agrinova.ui.home.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrinova.di.UsePreferences
import com.example.agrinova.ui.home.screens.ProfileViewModel.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InitialViewModel @Inject constructor(
    private val usePreferences: UsePreferences,
) : ViewModel() {
    private val _uiData = MutableStateFlow(UserUiState())
    val uiData: StateFlow<UserUiState> = _uiData.asStateFlow()
    data class UserUiState(
        val userFirstName: String = "",
        val userLastName: String = "",
    )
    init {
        userData()
    }

    private fun userData() {
        viewModelScope.launch {
            try {
                combine(
                    usePreferences.userFirstName,
                    usePreferences.userLastName
                ) { firstName, lastName ->
                    UserUiState(
                        userFirstName = firstName ?: "",
                        userLastName = lastName ?: ""
                    )
                }.collect { state ->
                    _uiData.value = state
                }
            } catch (e: Exception) {
                // Maneja el error aquí
                e.printStackTrace()
            }
        }
    }
}