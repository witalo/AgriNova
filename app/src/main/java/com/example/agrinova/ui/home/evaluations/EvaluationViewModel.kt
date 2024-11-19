package com.example.agrinova.ui.home.evaluations

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrinova.data.dto.DatoValvulaDto
import com.example.agrinova.data.repository.EmpresaRepository
import com.example.agrinova.di.UsePreferences
import com.example.agrinova.di.models.CartillaEvaluacionDomainModel
import com.example.agrinova.di.models.DatoDomainModel
import com.example.agrinova.di.models.FundoDomainModel
import com.example.agrinova.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EvaluationViewModel @Inject constructor(
    private val empresaRepository: EmpresaRepository,
    private val usePreferences: UsePreferences
) : ViewModel() {
    private val _selectedDate = MutableStateFlow(Constants.DATE_NOW) // Inicializa con la fecha actual
    val selectedDate: StateFlow<String> get() = _selectedDate

    private val _selectedCartilla = MutableStateFlow<CartillaEvaluacionDomainModel?>(null)
    val selectedCartilla: StateFlow<CartillaEvaluacionDomainModel?> = _selectedCartilla

    private val _uploadStatus = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadStatus: StateFlow<UploadState> = _uploadStatus.asStateFlow()

    private val companyId: Flow<Int?> = usePreferences.companyId
    val userId: Flow<Int?> = usePreferences.userId
    private val _cartillas = MutableStateFlow<List<CartillaEvaluacionDomainModel>>(emptyList())
    val cartillas: StateFlow<List<CartillaEvaluacionDomainModel>> = _cartillas.asStateFlow()

    // Estado para los datos filtrados
    private val _filteredDatos = MutableStateFlow<List<DatoValvulaDto>>(emptyList())
    val filteredDatos: StateFlow<List<DatoValvulaDto>> = _filteredDatos.asStateFlow()
    init {
        viewModelScope.launch {
            companyId.collect { id ->
                id?.let {
                    userId.collect { user ->
                        user?.let {
                            loadCartillas(user)
                        }
                    }
                }
            }
        }
    }

    fun loadCartillas(usuarioId: Int) {
        viewModelScope.launch {
            try {
                empresaRepository.getCartillas(usuarioId).collect { cartillaList ->
                    Log.d("Cartilla 2", cartillaList.toString())
                    _cartillas.value = cartillaList
                    Log.d("Cartilla 3", cartillaList.toString())
                }

            } catch (e: Exception) {
                _cartillas.value = emptyList()
            }
        }
    }
    // Cargar datos filtrados por cartilla y fecha
    fun loadDatosByDateAndCartilla(fecha: String, cartillaId: Int) {
        viewModelScope.launch {
            try {
                empresaRepository.getDatosByDateAndCartillaId(fecha, cartillaId).collect { datos ->
                    _filteredDatos.value = datos
                }
            } catch (e: Exception) {
                _filteredDatos.value = emptyList()
            }
        }
    }
    // Función para subir datos
    fun uploadDataToServer(fecha: String, cartillaId: Int) {
        viewModelScope.launch {
            try {
                _uploadStatus.value = UploadState.Loading
                // Subir los datos al servidor
                val result = empresaRepository.uploadMuestraData(fecha, cartillaId)

                result.fold(
                    onSuccess = {
                        _uploadStatus.value = UploadState.Success("Datos subidos exitosamente")
                        // Limpiar los datos de esa fecha y cartillaId
                        empresaRepository.clearDatosAndDetallesByDateAndCartillaId(fecha, cartillaId)
                    },
                    onFailure = { error ->
                        _uploadStatus.value = UploadState.Error("Error al subir datos: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _uploadStatus.value = UploadState.Error("Error inesperado: ${e.message}")
            }
        }
    }
    fun setSelectedCartilla(cartilla: CartillaEvaluacionDomainModel?) {
        _selectedCartilla.value = cartilla
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }
}
// Estados de la subida
sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    data class Success(val message: String) : UploadState()
    data class Error(val message: String) : UploadState()
}
