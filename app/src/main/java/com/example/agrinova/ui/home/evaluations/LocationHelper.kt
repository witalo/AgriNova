package com.example.agrinova.ui.home.evaluations
import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat

class LocationHelper(private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun checkAndEnableGPS(): Boolean {
        return try {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                false
            } else {
                true
            }
        } catch (e: Exception) {
            Log.e("GPS_ERROR", "Error enabling GPS: ${e.message}")
            false
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(): Pair<Double, Double>? {
        return try {
            if (!hasLocationPermission()) {
                return null
            }

            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            location?.let {
                Pair(it.latitude, it.longitude)
            }
        } catch (e: Exception) {
            Log.e("GPS_ERROR", "Error getting location: ${e.message}")
            null
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
//class LocationHelper(private val context: Context) {
//    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//    fun checkAndEnableGPS(): Boolean {
//        return if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            // Open GPS settings
//            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//            context.startActivity(intent)
//            false
//        } else {
//            true
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    fun getCurrentLocation(): Pair<Double, Double>? {
//        return try {
//            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//            location?.let {
//                Pair(it.latitude, it.longitude)
//            }
//        } catch (e: Exception) {
//            null
//        }
//    }
//}