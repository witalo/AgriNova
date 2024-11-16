package com.example.agrinova.ui.home.evaluations

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrinova.data.dto.LoteModuloDto
import com.example.agrinova.data.repository.EmpresaRepository
import com.example.agrinova.di.UsePreferences
import com.example.agrinova.di.models.LoteDomainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewEvaluationViewModel @Inject constructor(
    private val usePreferences: UsePreferences,
    private val empresaRepository: EmpresaRepository
) : ViewModel() {
    val userId: Flow<Int?> = usePreferences.userId
    val fundoId: Flow<Int?> = usePreferences.fundoId

    private val _lotes = MutableStateFlow<List<LoteModuloDto>>(emptyList())
    val lotes: StateFlow<List<LoteModuloDto>> = _lotes.asStateFlow()
    // Estados para los combos
    var isCombo1Expanded = mutableStateOf(false)
    var isCombo2Expanded = mutableStateOf(false)
    var isCombo3Expanded = mutableStateOf(false)

    // Elementos seleccionados
    var selectedCombo1 = mutableStateOf<ComboItem?>(null)
    var selectedCombo2 = mutableStateOf<ComboItem?>(null)
    var selectedCombo3 = mutableStateOf<ComboItem?>(null)

    // Listas de items para cada combo
    var combo1Items = mutableStateListOf<ComboItem>()
    var combo2Items = mutableStateListOf<ComboItem>()
    var combo3Items = mutableStateListOf<ComboItem>()

    // Lista de evaluaciones
    var evaluationList = mutableStateListOf<EvaluationItem>()

    init {
        viewModelScope.launch {
            userId.collect { user ->
                user?.let {
                    fundoId.collect { id ->
                        id?.let {
                            loadLotes(id)
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
                    }
                }

            } catch (e: Exception) {
                _lotes.value = emptyList()
            }
        }
    }

    fun onCombo1Selected(item: ComboItem) {
        selectedCombo1.value = item
        updateEvaluationList()
    }

    fun onCombo2Selected(item: ComboItem) {
        selectedCombo2.value = item
        updateEvaluationList()
    }

    fun onCombo3Selected(item: ComboItem) {
        selectedCombo3.value = item
        updateEvaluationList()
    }

    private fun updateEvaluationList() {
        viewModelScope.launch {
            val combo1Id = selectedCombo1.value?.id
            val combo2Id = selectedCombo2.value?.id
            val combo3Id = selectedCombo3.value?.id

            if (combo1Id != null && combo2Id != null && combo3Id != null) {
//                val newList = repository.getEvaluationList(combo1Id, combo2Id, combo3Id)
                evaluationList.clear()
//                evaluationList.addAll(newList)
            }
        }
    }
}