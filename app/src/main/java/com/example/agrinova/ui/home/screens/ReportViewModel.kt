package com.example.agrinova.ui.home.screens

import androidx.lifecycle.ViewModel
import com.example.agrinova.data.local.dao.PoligonoDao
import com.example.agrinova.data.local.dao.ValvulaDao
import com.example.agrinova.data.local.entity.ValvulaEntity
import com.example.agrinova.di.UsePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val usePreferences: UsePreferences,
    private val valvulaDao: ValvulaDao,
    private val poligonoDao: PoligonoDao
) : ViewModel() {
    // Función para encontrar en qué polígono está la ubicación

}