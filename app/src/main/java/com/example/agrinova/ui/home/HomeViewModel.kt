package com.example.agrinova.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _selectedTab = MutableStateFlow(BottomNavItem.Home)
    val selectedTab: StateFlow<BottomNavItem> = _selectedTab.asStateFlow()

    fun onTabSelected(tab: BottomNavItem) {
        _selectedTab.value = tab as BottomNavItem.Home
    }
}

// BottomNavItem.kt
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Inicio", Icons.Default.Home)
    object Profile : BottomNavItem("profile", "Perfil", Icons.Default.Person)
    object Report : BottomNavItem("report", "Reporte", Icons.Default.Settings)
}