package com.example.agrinova.ui.login.second
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrinova.di.models.FundoDomainModel
import com.example.agrinova.di.UsePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.agrinova.data.repository.EmpresaRepository
import com.example.agrinova.data.repository.UsuarioRepository
import javax.inject.Inject

@HiltViewModel
class SecondLoginViewModel @Inject constructor(
    private val userPreferences: UsePreferences,
    private val empresaRepository: EmpresaRepository,
//    private val fundoRepository: FundoRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {
    // Acceso directo al companyId desde UserPreferences
    val companyId: Flow<Int?> = userPreferences.companyId
    val companyName: Flow<String?> = userPreferences.companyName

    private val _fundos = MutableStateFlow<List<FundoDomainModel>>(emptyList())
    val fundos: StateFlow<List<FundoDomainModel>> = _fundos.asStateFlow()


    private val _validationState = MutableStateFlow<ValidationState>(ValidationState.Idle)
    val validationState: StateFlow<ValidationState> = _validationState.asStateFlow()

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    init {
        // Cargar fundos automáticamente cuando se inicializa el ViewModel y companyId tiene valor
        viewModelScope.launch {
            companyId.collect { id ->
                id?.let {
                    loadFundos() // Llamar a loadFundos cuando companyId tenga valor
                }
            }
        }
    }

    fun loadFundos() {
        viewModelScope.launch {
            companyId.collect { id ->
                id?.let {
                    try {
                        empresaRepository.getFundos().collect { fundosList ->
                            _fundos.value = fundosList
                        }

                    } catch (e: Exception) {
                        _fundos.value = emptyList()
                    }
                }
            }
        }
    }
    fun syncData(empresaId: Int) {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            try {
//                empresaRepository.syncEmpresaData(empresaId)
                _syncState.value = SyncState.Success
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e.toString())
            }
        }
    }

    fun validateUser(dni: String, fundoId: Int?, moduleId: String) {
        viewModelScope.launch {
            when {
                dni.isBlank() -> {
                    _validationState.value = ValidationState.Invalid("Ingresa un DNI válido")
                }
                fundoId == null -> {
                    _validationState.value = ValidationState.Invalid("Selecciona un fundo")
                }
                else -> {
                    _validationState.value = ValidationState.Loading
                    try {
                        Log.d("Italo", "DNI: $dni, Fundo ID: $fundoId, Módulo ID: $moduleId")
                        val isUserValid = usuarioRepository.validateUser(dni, fundoId)
                        _validationState.value = if (isUserValid) {
                            Log.d("Italo login success", "Valido")
                            ValidationState.Valid
                        } else {
                            Log.d("Italo login error", "No Valido")
                            ValidationState.Invalid("Usuario no válido")
                        }
                    } catch (e: Exception) {
                        _validationState.value = ValidationState.Invalid("Error de validación: ${e.message}")
                    }
                }
            }
        }
    }
}

sealed class ValidationState {
    object Idle : ValidationState()
    object Loading : ValidationState()
    object Valid : ValidationState()
    data class Invalid(val message: String) : ValidationState()
}

sealed class SyncState {
    object Idle : SyncState()
    object Loading : SyncState()
    object Success : SyncState()
    data class SuccessResponse(val message: String) : SyncState()  // Éxito con un mensaje
    data class Error(val errorMessage: String) : SyncState()  // Error con un mensaje de error
}