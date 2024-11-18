package com.example.agrinova.ui.home.evaluations

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrinova.data.dto.DatoValvulaDto
import com.example.agrinova.data.repository.EmpresaRepository
import com.example.agrinova.di.UsePreferences
import com.example.agrinova.di.models.CartillaEvaluacionDomainModel
import com.example.agrinova.di.models.DatoDomainModel
import com.example.agrinova.di.models.FundoDomainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EvaluationViewModel @Inject constructor(
    private val empresaRepository: EmpresaRepository,
    private val usePreferences: UsePreferences,
) : ViewModel() {
    val companyId: Flow<Int?> = usePreferences.companyId
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
                            Log.d("Cartilla 1", user.toString())
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
}