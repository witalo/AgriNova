package com.example.agriapp.ui.home.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.agrinova.ui.home.screens.ProfileViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.maps.android.compose.*

@Composable
fun ReportScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
//    navController: NavHostController
) {
    // Coordenadas del polígono
    val polygonCoordinates = listOf(
        LatLng(19.4326, -99.1332),  // Ciudad de México (ejemplo)
        LatLng(19.4420, -99.1670),
        LatLng(19.4280, -99.1490),
        LatLng(19.4180, -99.1230)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "En proceso de desarrollo",
            style = MaterialTheme.typography.headlineMedium
        )
        // Estado para controlar el polígono
        var polygon by remember { mutableStateOf<Polygon?>(null) }

        // Posición inicial de la cámara
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(polygonCoordinates.first(), 10f)
        }

        // Configuración de Google Maps
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Dibujar polígono
            Polygon(
                points = polygonCoordinates,
                strokeColor = Color(0xFFFF0000),  // Rojo en formato hexadecimal
                fillColor = Color(0x33FF0000),    // Rojo con transparencia
                strokeWidth = 5f,
                clickable = true,
                // Evento de clic en el polígono
                onClick = {
                    // Acciones al hacer clic en el polígono
                }
            )
        }
    }
}
// Configuración en el archivo AndroidManifest.xml
// Añade dentro de <application>:
// <meta-data
//     android:name="com.google.android.geo.API_KEY"
//     android:value="TU_CLAVE_DE_API_DE_GOOGLE_MAPS" />

// Dependencias necesarias en build.gradle (Module)