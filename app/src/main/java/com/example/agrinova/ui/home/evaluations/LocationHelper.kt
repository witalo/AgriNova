package com.example.agrinova.ui.home.evaluations
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.annotation.SuppressLint
class LocationHelper(private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun checkAndEnableGPS(): Boolean {
        return if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Open GPS settings
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
            false
        } else {
            true
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(): Pair<Double, Double>? {
        return try {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            location?.let {
                Pair(it.latitude, it.longitude)
            }
        } catch (e: Exception) {
            null
        }
    }
}