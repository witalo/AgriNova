package com.example.agrinova.ui.home.screens

import android.Manifest
import android.location.LocationProvider
import android.widget.Toast
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
import androidx.compose.material.icons.filled.MyLocation
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
import androidx.compose.ui.text.font.FontWeight
import com.example.agrinova.di.LocationHandlerRural
import com.example.agrinova.di.models.ValvulaDomainModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay

@Composable
fun ReportScreen(
    viewModel: ReportViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    // Default location (e.g., Lima, Peru)
    val defaultLatitud = -16.409047
    val defaultLongitud = -71.536960
    // Estados para manejar la ubicación
    // Use viewModel states instead of local states
    val latitud by viewModel.latitud
    val longitud by viewModel.longitud
    val mensajeError by viewModel.mensajeError

    // Estados del ViewModel
    val polygonCoordinates by viewModel.polygonCoordinates
    val closestValvulaNombre by viewModel.closestValvulaNombre
    // Posición inicial de la cámara
    // Initial camera position state with default location
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(defaultLatitud, defaultLongitud),
            12f
        )
    }

    // Set initial coordinates in ViewModel
    LaunchedEffect(Unit) {
        viewModel.setInitialLocation(defaultLatitud, defaultLongitud)
    }
    // Launcher para solicitar permisos
    val permisoUbicacionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permisoConcedido ->
        if (permisoConcedido) {
            viewModel.obtenerUbicacion(
                context,
                onPermisoDenegado = {
                    viewModel.setError("Permisos de ubicación denegados")
                },
                onUbicacionObtenida = { lat, lon ->
                    // Mover la cámara si es necesario
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 18f)
                    )
                }
            )
        } else {
            viewModel.setError("Permisos de ubicación denegados")
        }
    }

//    -----------------------------------------------------------------------------------------------
    LaunchedEffect(Unit) {
        while(true) {
            viewModel.obtenerUbicacion(
                context,
                onPermisoDenegado = {
                    permisoUbicacionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                },
                onUbicacionObtenida = { lat, lon ->
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 18f)
                    )
                }
            )
            delay(60000) // Espera 1 minuto (60000 milisegundos)
        }
    }
//    -----------------------------------------------------------------------------------------------
//   Estructura del Card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .shadow(8.dp, shape = RoundedCornerShape(16.dp)), // Sombra para destacar el Card
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp),
//        backgroundColor = Color(0xFFF5F5F5) // Fondo gris claro
    ) {
        Column {
            // Card Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
                    .background(Color(0xFF0573CB)) // Fondo azul
                    .clip(RoundedCornerShape(30.dp)), // Espaciado interno
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.obtenerUbicacion(
                            context,
                            onPermisoDenegado = {
                                // Solicitar permisos si no están concedidos
                                permisoUbicacionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            },
                            onUbicacionObtenida = { lat, lon ->
                                // Opcional: Mover la cámara a la ubicación
                                cameraPositionState.move(
                                    CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 18f)
                                )
                            }
                        )
                    }
                    ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation, // Ícono GPS moderno
                        contentDescription = "Ir a mi ubicación",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp) // Tamaño del ícono
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Mostrar coordenadas o mensaje de error
                // Conditional text rendering
                Text(
                    text = when {
                        mensajeError.isNotEmpty() -> mensajeError
                        latitud != 0.0 && longitud != 0.0 -> "Lat: $latitud Long: $longitud"
                        else -> "Lat: ${viewModel.latitud.value} Long: ${viewModel.longitud.value}"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                if (mensajeError.isNotEmpty()) {
                    Text(
                        mensajeError,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

            }

            // Card Body (Mapa con zoom y borde)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight() // Altura ajustada para el mapa
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    ) // Bordes redondeados
                    .border(
                        1.dp,
                        Color.LightGray,
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
            ) {
                // Configuración de Google Maps
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    // ... otros ajustes
                ) {
                    if (polygonCoordinates.isNotEmpty()) {
                        // Dibujar polígono de la válvula más cercana
                        Polygon(
                            points = polygonCoordinates,
                            strokeColor = Color(0xFFFF5722),
                            fillColor = Color(0x33FF5722),
                            strokeWidth = 5f,
                            clickable = true
                        )

                        // Calcular centro del polígono
                        val polygonCenter = calculatePolygonCenter(polygonCoordinates)

                        // Marker en el centro con nombre de la válvula
                        Marker(
                            state = MarkerState(position = polygonCenter),
                            title = "Válvula: $closestValvulaNombre",
                            snippet = "Válvula más cercana"
                        )
                    }
                }
            }
        }
    }
}

fun calculatePolygonCenter(coordinates: List<LatLng>): LatLng {
    val centerLat = coordinates.map { it.latitude }.average()
    val centerLng = coordinates.map { it.longitude }.average()
    return LatLng(centerLat, centerLng)
}
//    Column {
//        Button(
//            onClick = {
//                // Solicitar permisos si no están concedidos
//                if (!locationHandler.verificarPermisos(context)) {
//                    permisoUbicacionLauncher.launch(
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    )
//                } else {
//                    // Si ya tiene permisos, obtener ubicación directamente
//                    locationHandler.obtenerUbicacionRural(
//                        context,
//                        onUbicacionObtenida = { lat, lon ->
//                            latitud = lat
//                            longitud = lon
//                        },
//                        onError = { error ->
//                            mensajeError = error
//                        }
//                    )
//                }
//            }
//        ) {
//            Text("Obtener Ubicación Rural")
//        }
//
//        // Mostrar coordenadas o mensaje de error
//        if (latitud != 0.0 && longitud != 0.0) {
//            Text("Latitud: $latitud")
//            Text("Longitud: $longitud")
//        }
//
//        if (mensajeError.isNotEmpty()) {
//            Text("Error: $mensajeError")
//        }
//    }