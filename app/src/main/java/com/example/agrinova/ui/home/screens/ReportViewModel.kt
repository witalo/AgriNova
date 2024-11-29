package com.example.agrinova.ui.home.screens

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrinova.data.local.dao.PoligonoDao
import com.example.agrinova.data.local.dao.ValvulaDao
import com.example.agrinova.data.local.entity.ValvulaEntity
import com.example.agrinova.data.repository.EmpresaRepository
import com.example.agrinova.data.repository.UsuarioRepository
import com.example.agrinova.di.LocationHandlerRural
import com.example.agrinova.di.UsePreferences
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val locationHandler: LocationHandlerRural,
    private val usuarioRepository: UsuarioRepository,
    private val poligonoDao: PoligonoDao // Add this dependency
) : ViewModel() {
    // Estados de ubicación
    private val _latitud = mutableStateOf(0.0)
    val latitud: State<Double> = _latitud

    private val _longitud = mutableStateOf(0.0)
    val longitud: State<Double> = _longitud

    private val _mensajeError = mutableStateOf("")
    val mensajeError: State<String> = _mensajeError


    // Método actualizado para obtener ubicación y encontrar válvula cercana
    fun obtenerUbicacion(context: Context, onPermisoDenegado: () -> Unit, onUbicacionObtenida: (Double, Double) -> Unit) {
        if (locationHandler.verificarPermisos(context)) {
            locationHandler.obtenerUbicacionRural(
                context,
                onUbicacionObtenida = { lat, lon ->
                    // Actualizar estados de ubicación
                    _latitud.value = lat
                    _longitud.value = lon

                    // Llamar a la función para encontrar la válvula más cercana
                    findNearestValvulaPolygon(lat, lon)

                    // Callback opcional para el llamador
                    onUbicacionObtenida(lat, lon)
                },
                onError = { error ->
                    _mensajeError.value = error
                }
            )
        } else {
            onPermisoDenegado()
        }
    }

    private val _polygonCoordinates = mutableStateOf<List<LatLng>>(emptyList())
    val polygonCoordinates: State<List<LatLng>> = _polygonCoordinates

    private val _closestValvulaNombre = mutableStateOf("")
    val closestValvulaNombre: State<String> = _closestValvulaNombre

    fun findNearestValvulaPolygon(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val poligonosWithValvula = poligonoDao.getPoligonoWithValvula()
                val userLocation = LatLng(lat, lon)

                val closestPoligono = usuarioRepository.findClosestValvula(userLocation, poligonosWithValvula)

                closestPoligono?.let { polygon ->
                    // Filter polygons for this specific valve
                    val valvePolygonCoordinates = poligonosWithValvula
                        .filter { it.valvulaCodigo == polygon.valvulaCodigo }
                        .map { LatLng(it.latitud.toDouble(), it.longitud.toDouble()) }

                    _polygonCoordinates.value = valvePolygonCoordinates
                    _closestValvulaNombre.value = polygon.valvulaCodigo
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error finding nearest valve: ${e.message}"
            }
        }
    }
    // In ViewModel
    fun setError(errorMessage: String) {
        _mensajeError.value = errorMessage
    }
    // In ViewModel
    fun setInitialLocation(lat: Double, lon: Double) {
        _latitud.value = lat
        _longitud.value = lon
    }
}