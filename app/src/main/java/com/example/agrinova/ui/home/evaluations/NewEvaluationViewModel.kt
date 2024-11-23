package com.example.agrinova.ui.home.evaluations

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrinova.data.dto.LocationModel
import com.example.agrinova.data.dto.LoteModuloDto
import com.example.agrinova.data.repository.EmpresaRepository
import com.example.agrinova.di.UsePreferences
import com.example.agrinova.di.models.GrupoVariableDomainModel
import com.example.agrinova.di.models.ValvulaDomainModel
import com.example.agrinova.di.models.VariableGrupoDomainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewEvaluationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val usePreferences: UsePreferences,
    private val empresaRepository: EmpresaRepository,
    private val context: Context
) : ViewModel() {
    private val locationHelper = LocationHelper(context)

    private val _isGpsEnabled = MutableStateFlow(false)
    val isGpsEnabled: StateFlow<Boolean> = _isGpsEnabled.asStateFlow()

    private val _locationData = MutableStateFlow<LocationModel?>(null)
    val locationData: StateFlow<LocationModel?> = _locationData.asStateFlow()
    fun onGpsCheckboxChanged(isChecked: Boolean) {
        viewModelScope.launch {
            try {
                if (isChecked) {
                    val gpsEnabled = locationHelper.checkAndEnableGPS()
                    _isGpsEnabled.value = gpsEnabled

                    if (gpsEnabled) {
                        locationHelper.getCurrentLocation()?.let { location ->
                            _locationData.value = LocationModel(
                                latitude = location.first,
                                longitude = location.second
                            )
                        }
                    }
                } else {
                    _isGpsEnabled.value = false
                    _locationData.value = null
                }
            } catch (e: Exception) {
                Log.e("GPS_ERROR", "Error: ${e.message}")
                _isGpsEnabled.value = false
            }
        }
    }

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
    private val _variableValues = MutableStateFlow<Map<Int, String>>(emptyMap())
    val variableValues: StateFlow<Map<Int, String>> = _variableValues.asStateFlow()

    private val _saveStatus = MutableStateFlow<Result<Unit>?>(null)
    val saveStatus: StateFlow<Result<Unit>?> = _saveStatus.asStateFlow()

    // Controla el estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

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

    // Función para actualizar el valor de una variable
    fun updateVariableValue(variableId: Int, value: String) {
        _variableValues.value += (variableId to value)
    }

    // Función para guardar la evaluación
    @RequiresApi(Build.VERSION_CODES.O)
    fun saveEvaluationDato() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                userId.collect { user ->
                    user?.let {
                        val valvulaId = _selectedValvula.value?.id
                            ?: throw IllegalStateException("No se ha seleccionado una válvula")
                        Log.d("Detalles view:", _variableValues.value.toString())

                        // Enviar directamente los valores sin procesar
                        val result = empresaRepository.insertDatoWithDetalles(
                            valvulaId = valvulaId,
                            cartillaId = cartillaId.toInt(),
                            usuarioId = user,
                            variableValues = _variableValues.value
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
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun saveEvaluationDato() {
//        viewModelScope.launch {
//            try {
//                _isLoading.value = true
//                // Recolectar el userId y usarlo
//                userId.collect { user ->
//                    user?.let {
//                        val valvulaId = _selectedValvula.value?.id
//                            ?: throw IllegalStateException("No se ha seleccionado una válvula")
//
//                        // Solo procesar valores que no estén vacíos y sean números válidos
////                        val validValues = _variableValues.value.filter { (_, value) ->
////                            value.isNotEmpty() && value.toDoubleOrNull() != null
////                        }
//                        // Convertir valores vacíos a "0" y mantener los valores válidos
//                        val processedValues = _variableValues.value.mapValues { (_, value) ->
//                            if (value.isEmpty()) "0" else value
//                        }
//
//                        if (processedValues.isEmpty()) {
//                            throw IllegalStateException("No hay valores válidos para guardar")
//                        }
//
//                        val result = empresaRepository.insertDatoWithDetalles(
//                            valvulaId = valvulaId,
//                            cartillaId = cartillaId.toInt(),
//                            usuarioId = user,
//                            variableValues = processedValues
//                        )
//                        _saveStatus.value = result
//                    }
//                }
//            } catch (e: Exception) {
//                _saveStatus.value = Result.failure(e)
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }


}