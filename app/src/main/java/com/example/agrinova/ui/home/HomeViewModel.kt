package com.example.agrinova.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    // Cambia el tipo de _selectedTab a BottomNavItem para aceptar cualquier pestaña
    private val _selectedTab = MutableStateFlow<BottomNavItem>(BottomNavItem.Home)
    val selectedTab: StateFlow<BottomNavItem> = _selectedTab.asStateFlow()

    // Función para cambiar la pestaña seleccionada
    fun onTabSelected(tab: BottomNavItem) {
        _selectedTab.value = tab // Ahora acepta cualquier BottomNavItem
    }
    //Nueva navegacion con texto
    fun onRouteChanged(route: String) {
        _selectedTab.value = when {
            route == BottomNavItem.Home.route -> BottomNavItem.Home
            route == BottomNavItem.Profile.route -> BottomNavItem.Profile
            route == BottomNavItem.Report.route -> BottomNavItem.Report
            route == BottomNavItem.Evaluation.route -> BottomNavItem.Evaluation
            route.startsWith("newEvaluation/") -> {
                val cartillaId = route.substringAfter("newEvaluation/")
                BottomNavItem.NewEvaluation(cartillaId)
            }
            else -> BottomNavItem.Unknown
        }
    }
}
//Nueva navegacion con texto
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedColor: Color = Color(0xFF43BD28),
    val unselectedColor: Color = Color.Gray,
    val selectedBackgroundColor: Color = Color(0xFFE0F7E0)
) {
    object Home : BottomNavItem("home", "Inicio", Icons.Default.Home)
    object Profile : BottomNavItem("profile", "Perfil", Icons.Default.Person)
    object Report : BottomNavItem("report", "Reporte", Icons.Default.Settings)
    object Evaluation : BottomNavItem("evaluation", "Evaluaciones", Icons.Default.List)
    data class NewEvaluation(val cartillaId: String) : BottomNavItem(
        "newEvaluation/$cartillaId",
        "Nueva Evaluación",
        Icons.Default.Create
    )
    object Unknown : BottomNavItem("unknown", "Pantalla Desconocida", Icons.Default.Home)
}
// BottomNavItem.kt
//sealed class BottomNavItem(
//    val route: String,
//    val title: String,
//    val icon: ImageVector,
//    val selectedColor: Color = Color(0xFF43BD28), // Color al estar seleccionada
//    val unselectedColor: Color = Color.Gray,       // Color al estar deseleccionada
//    val selectedBackgroundColor: Color = Color(0xFFE0F7E0) // Fondo seleccionado
//) {
//    object Home : BottomNavItem("home", "Inicio", Icons.Default.Home)
//    object Profile : BottomNavItem("profile", "Perfil", Icons.Default.Person)
//    object Report : BottomNavItem("report", "Reporte", Icons.Default.Settings)
//}