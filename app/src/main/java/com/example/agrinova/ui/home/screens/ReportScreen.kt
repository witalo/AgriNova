package com.example.agrinova.ui.home.screens

import android.Manifest
import android.location.LocationProvider
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.agrinova.di.LocationHandlerRural
import com.example.agrinova.di.models.ValvulaDomainModel
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.maps.android.compose.*

@Composable
fun ReportScreen(
    viewModel: ReportViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val locationHandler = remember { LocationHandlerRural() }

    // Estados para manejar la ubicación
    var latitud by remember { mutableStateOf(0.0) }
    var longitud by remember { mutableStateOf(0.0) }
    var mensajeError by remember { mutableStateOf("") }

    // Launcher para solicitar permisos
    val permisoUbicacionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permisoConcedido ->
        if (permisoConcedido) {
            // Permisos concedidos, obtener ubicación
            locationHandler.obtenerUbicacionRural(
                context,
                onUbicacionObtenida = { lat, lon ->
                    latitud = lat
                    longitud = lon
                },
                onError = { error ->
                    mensajeError = error
                }
            )
        } else {
            mensajeError = "Permisos de ubicación denegados"
        }
    }

    Column {
        Button(
            onClick = {
                // Solicitar permisos si no están concedidos
                if (!locationHandler.verificarPermisos(context)) {
                    permisoUbicacionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } else {
                    // Si ya tiene permisos, obtener ubicación directamente
                    locationHandler.obtenerUbicacionRural(
                        context,
                        onUbicacionObtenida = { lat, lon ->
                            latitud = lat
                            longitud = lon
                        },
                        onError = { error ->
                            mensajeError = error
                        }
                    )
                }
            }
        ) {
            Text("Obtener Ubicación Rural")
        }

        // Mostrar coordenadas o mensaje de error
        if (latitud != 0.0 && longitud != 0.0) {
            Text("Latitud: $latitud")
            Text("Longitud: $longitud")
        }

        if (mensajeError.isNotEmpty()) {
            Text("Error: $mensajeError")
        }
    }

//     Coordenadas del polígono
    val polygonCoordinates = listOf(
        LatLng(19.4326, -99.1332),  // Ciudad de México (ejemplo)
        LatLng(19.4420, -99.1670),
        LatLng(19.4280, -99.1490),
        LatLng(19.4180, -99.1230)
    )
    // Calcular el centroide del polígono
    val polygonCenter = calculatePolygonCenter(polygonCoordinates)
    // Posición inicial de la cámara
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(polygonCoordinates.first(), 12f)
    }

    // Estructura del Card
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(2.dp)
//            .shadow(8.dp, shape = RoundedCornerShape(16.dp)), // Sombra para destacar el Card
//        elevation = CardDefaults.cardElevation(8.dp),
//        shape = RoundedCornerShape(16.dp),
////        backgroundColor = Color(0xFFF5F5F5) // Fondo gris claro
//    ) {
//        Column {
//            // Card Header
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//                    .background(Color(0xFF3F51B5)) // Fondo azul
//                    .padding(5.dp), // Espaciado interno
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Map,
//                    contentDescription = "Mapa",
//                    tint = Color.White,
//                    modifier = Modifier.size(24.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    text = "Mapa de Polígonos",
//                    style = MaterialTheme.typography.titleSmall.copy(color = Color.White),
//                    modifier = Modifier.weight(1f)
//                )
//                IconButton(onClick = { /* Acción adicional si se requiere */ }) {
//                    Icon(
//                        imageVector = Icons.Default.MoreVert,
//                        contentDescription = "Más opciones",
//                        tint = Color.White
//                    )
//                }
//            }
//
//            // Card Body (Mapa con zoom y borde)
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .fillMaxHeight() // Altura ajustada para el mapa
//                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)) // Bordes redondeados
//                    .border(1.dp, Color.LightGray, shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
//            ) {
//                // Configuración de Google Maps
//                GoogleMap(
//                    modifier = Modifier.fillMaxSize(),
//                    cameraPositionState = cameraPositionState,
//                    uiSettings = MapUiSettings(
//                        zoomControlsEnabled = true, // Habilitar controles de zoom
//                        compassEnabled = true       // Habilitar brújula
//                    )
//                ) {
//                    // Dibujar polígono
//                    Polygon(
//                        points = polygonCoordinates,
//                        strokeColor = Color(0xFFFF5722),  // Naranja vibrante
//                        fillColor = Color(0x33FF5722),    // Naranja transparente
//                        strokeWidth = 5f,
//                        clickable = true,
//                        onClick = {
//                            // Acción al hacer clic en el polígono
//                        }
//                    )
//                    // Etiqueta en el centro del polígono
//                    Marker(
//                        state = MarkerState(position = polygonCenter),
//                        title = "Centro del Polígono",
//                        snippet = "Etiqueta personalizada"
//                    )
//                }
//            }
//        }
//    }
}
// Función para calcular el centroide del polígono
fun calculatePolygonCenter(points: List<LatLng>): LatLng {
    val latitudeSum = points.sumOf { it.latitude }
    val longitudeSum = points.sumOf { it.longitude }
    return LatLng(latitudeSum / points.size, longitudeSum / points.size)
}
