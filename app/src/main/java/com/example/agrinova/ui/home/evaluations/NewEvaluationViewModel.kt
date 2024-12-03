package com.example.agrinova.ui.home.evaluations

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrinova.data.dto.LocationModel
import com.example.agrinova.data.dto.LoteModuloDto
import com.example.agrinova.data.local.entity.roundTo2Decimals
import com.example.agrinova.data.repository.EmpresaRepository
import com.example.agrinova.di.LocationHandlerRural
import com.example.agrinova.di.UsePreferences
import com.example.agrinova.di.models.GrupoVariableDomainModel
import com.example.agrinova.di.models.ValvulaDomainModel
import com.example.agrinova.di.models.VariableGrupoDomainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NewEvaluationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val usePreferences: UsePreferences,
    private val empresaRepository: EmpresaRepository,
    private val context: Context
) : ViewModel() {
    private val _isGpsEnabled = MutableStateFlow(false)
    val isGpsEnabled: StateFlow<Boolean> = _isGpsEnabled.asStateFlow()

    val cartillaId: String = savedStateHandle["cartillaId"] ?: ""
    val userId: Flow<Int?> = usePreferences.userId
    val fundoId: Flow<Int?> = usePreferences.fundoId

    // Estado para el primer combo (Lotes)
    private val _lotes = MutableStateFlow<List<LoteModuloDto>>(emptyList())
    val lotes: StateFlow<List<LoteModuloDto>> = _lotes.asStateFlow()

    // Estado para el segundo combo (Válvulas)
    private val _valvulas = MutableStateFlow<List<ValvulaDomainModel>>(emptyList())
    val valvulas: StateFlow<List<ValvulaDomainModel>> = _valvulas.asStateFlow()

    // Estados para los items seleccionados
    private val _selectedLote = MutableStateFlow<LoteModuloDto?>(null)
    val selectedLote: StateFlow<LoteModuloDto?> = _selectedLote.asStateFlow()

    private val _selectedValvula = MutableStateFlow<ValvulaDomainModel?>(null)
    val selectedValvula: StateFlow<ValvulaDomainModel?> = _selectedValvula.asStateFlow()

    // Estados para los combos
    var isCombo1Expanded = mutableStateOf(false)
    var isCombo2Expanded = mutableStateOf(false)

    private val _grupos = MutableStateFlow<List<GrupoVariableDomainModel>>(emptyList())
    val grupos: StateFlow<List<GrupoVariableDomainModel>> = _grupos.asStateFlow()

    private val _variables = MutableStateFlow<List<VariableGrupoDomainModel>>(emptyList())
    val variables: StateFlow<List<VariableGrupoDomainModel>> = _variables.asStateFlow()

    // Estado para controlar qué grupos están expandidos
    private val _expandedGroups = MutableStateFlow<Set<Int>>(emptySet())
    val expandedGroups: StateFlow<Set<Int>> = _expandedGroups.asStateFlow()

    // Estado para almacenar los valores ingresados en los TextFields
    private val _variableValues =
        MutableStateFlow<Map<Int, Pair<String, LocationModel?>>>(emptyMap())
    val variableValues: StateFlow<Map<Int, Pair<String, LocationModel?>>> =
        _variableValues.asStateFlow()

    private val _saveStatus = MutableStateFlow<Result<Unit>?>(null)
    val saveStatus: StateFlow<Result<Unit>?> = _saveStatus.asStateFlow()

    // Controla el estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    //    ------------------------------------------------------------------------------------------
    private val _showSuccessDialog = MutableStateFlow(false)
    val showSuccessDialog: StateFlow<Boolean> get() = _showSuccessDialog

    fun onSaveSuccess() {
        _showSuccessDialog.value = true
    }
//    ------------------------------------------------------------------------------------------

    init {
        viewModelScope.launch {
            userId.collect { user ->
                user?.let {
                    fundoId.collect { id ->
                        id?.let {
                            loadLotes(id)
                            loadGruposVariable(cartillaId.toInt())
                        }
                    }
                }
            }
        }
    }

    private fun loadLotes(fundoId: Int?) {
        viewModelScope.launch {
            try {
                if (fundoId != null) {
                    empresaRepository.getLotesByFundo(fundoId).collect { loteList ->
                        _lotes.value = loteList
                        _selectedLote.value = null
                    }
                }

            } catch (e: Exception) {
                _lotes.value = emptyList()
                _selectedLote.value = null
            }
        }
    }

    private fun loadValvulas(loteId: Int) {
        viewModelScope.launch {
            try {
                empresaRepository.getValvulasByLoteId(loteId).collect { valvulasList ->
                    _valvulas.value = valvulasList
                    // Limpiamos la válvula seleccionada cuando cambia el lote
                    _selectedValvula.value = null
                }
            } catch (e: Exception) {
                _valvulas.value = emptyList()
                _selectedValvula.value = null
            }
        }
    }

    private fun loadGruposVariable(cartillaId: Int) {
        viewModelScope.launch {
            try {
                empresaRepository.getGruposVariableByCartillaId(cartillaId).collect { grupoList ->
                    _grupos.value = grupoList
                }
            } catch (e: Exception) {
                _grupos.value = emptyList()
            }
        }
    }

    private fun loadVariablesGrupo(grupoId: Int) {
        viewModelScope.launch {
            try {
                empresaRepository.getVariablesGrupoByGrupoVariableId(grupoId)
                    .collect { variableList ->
                        _variables.value = variableList
                    }
            } catch (e: Exception) {
                _variables.value = emptyList()
            }
        }
    }

    // Función para manejar la selección del primer combo
    fun onLoteSelected(lote: LoteModuloDto) {
        _selectedLote.value = lote
        _selectedValvula.value = null // Limpiar válvula seleccionada
        loadValvulas(lote.loteId)
    }

    // Función para manejar la selección del segundo combo
    fun onValvulaSelected(valvula: ValvulaDomainModel) {
        _selectedValvula.value = valvula
    }

    // Función para alternar la expansión de un grupo
    fun toggleGroupExpansion(grupoId: Int) {
        viewModelScope.launch {
            // Actualizamos el estado de expansión
            val newExpandedGroups = if (_expandedGroups.value.contains(grupoId)) {
                _expandedGroups.value - grupoId
            } else {
                _expandedGroups.value + grupoId
            }

            _expandedGroups.value = newExpandedGroups

            // Si el grupo se expandió, cargamos sus variables
            if (newExpandedGroups.contains(grupoId)) {
                loadVariablesGrupo(grupoId)
            }
        }
    }

    // Modify the updateVariableValue function
    fun updateVariableValue(variableId: Int, value: String, location: LocationModel?) {
        val formattedValue = try {
            value.toFloat().roundTo2Decimals()
        } catch (e: NumberFormatException) {
            0f
        }
        _variableValues.value += (variableId to (formattedValue.toString() to location))
    }

    // Update the saveEvaluationDato function to handle location
    @RequiresApi(Build.VERSION_CODES.O)
    fun saveEvaluationDato() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                if (_variableValues.value.isNullOrEmpty()) {
                    _isLoading.value = false
                    Toast.makeText(context, "Debe ingresar al menos un valor.", Toast.LENGTH_SHORT)
                        .show()
                    return@launch
                }

                userId.collect { user ->
                    user?.let {
                        val valvulaId = _selectedValvula.value?.id
                            ?: throw IllegalStateException("No se ha seleccionado una válvula")

                        // Convert to the format expected by insertDatoWithDetalles
                        val processedValues = _variableValues.value.mapValues { (_, pair) ->
                            pair.first
                        }

                        // Prepare location details
                        val locationDetails = _variableValues.value.mapValues { (_, pair) ->
                            pair.second ?: LocationModel(0.0, 0.0)
                        }

                        val result = empresaRepository.insertDatoWithDetalles(
                            valvulaId = valvulaId,
                            cartillaId = cartillaId.toInt(),
                            usuarioId = user,
                            variableValues = processedValues,
                            locationDetails = locationDetails
                        )
                        _saveStatus.value = result
                    }
                }
            } catch (e: Exception) {
                _saveStatus.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private lateinit var locationHandler: LocationHandlerRural

    init {
        // Inicializar el LocationHandlerRural
        locationHandler = LocationHandlerRural()
    }

    fun onGpsCheckboxChanged(isEnabled: Boolean) {
        _isGpsEnabled.value = isEnabled
    }
    fun captureLocationAsync(
        context: Context,
        variableId: Int,
        onLocationCaptured: (LocationModel) -> Unit
    ) {
        // Only attempt location capture if GPS is enabled
        if (!isGpsEnabled.value) {
            onLocationCaptured(LocationModel(0.0, 0.0))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val location = obtenerUbicacionActual(context)
                withContext(Dispatchers.Main) {
                    if (location != null) {
                        onLocationCaptured(
                            LocationModel(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                isValid = true
                            )
                        )
                    } else {
                        onLocationCaptured(LocationModel(0.0, 0.0))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("LocationCapture", "Error al obtener ubicación", e)
                    onLocationCaptured(LocationModel(0.0, 0.0))
                }
            }
        }
    }
    private fun obtenerUbicacionActual(context: Context): Location? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Verificar permisos
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        // Intentar obtener la última ubicación conocida
        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER
        )

        return providers.firstNotNullOfOrNull { provider ->
            locationManager.getLastKnownLocation(provider)
        }
    }
}