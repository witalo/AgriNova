package com.example.agrinova.di

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

class LocationHandlerRural {
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

    fun obtenerUbicacionRural(
        context: Context,
        onUbicacionObtenida: (Double, Double) -> Unit,
        onError: (String) -> Unit
    ) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Verificar si el GPS está habilitado
        if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onError("El GPS está desactivado")
            return
        }

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Detener actualizaciones después de obtener la primera ubicación precisa
                locationManager?.removeUpdates(this)

                // Verificar precisión de la ubicación
                if (location.accuracy <= 20.0) { // Precisión menor a 20 metros
                    onUbicacionObtenida(location.latitude, location.longitude)
                } else {
                    onError("Precisión insuficiente")
                }
            }

            // Métodos adicionales para manejar cambios en el proveedor
            override fun onProviderDisabled(provider: String) {
                onError("Proveedor GPS desactivado")
            }

            override fun onProviderEnabled(provider: String) {
                // Proveedor habilitado
            }
        }

        // Configuración de solicitud de ubicación para zonas rurales
        try {
            // Solicitar actualizaciones cada 1 segundo o cada 1 metro
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,   // Intervalo mínimo de tiempo (milisegundos)
                1f,     // Distancia mínima de cambio (metros)
                locationListener!!
            )
        } catch (e: SecurityException) {
            onError("Error de permiso: ${e.message}")
        }
    }

    // Verificar permisos de ubicación
    fun verificarPermisos(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    // Limpiar recursos
    fun limpiarRecursos() {
        locationManager?.removeUpdates(locationListener!!)
    }
}