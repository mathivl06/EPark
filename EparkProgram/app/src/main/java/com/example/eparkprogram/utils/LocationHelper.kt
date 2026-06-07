package com.example.eparkprogram.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.eparkprogram.data.model.ParkingZone
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

object LocationHelper {

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    fun hasLocationPermissions(context: Context): Boolean {
        return locationPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context, onLocationReceived: (Location?) -> Unit) {
        if (!hasLocationPermissions(context)) {
            onLocationReceived(null)
            return
        }

        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        // Intentar obtener la última ubicación conocida primero
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onLocationReceived(location)
            } else {
                // Si no hay última ubicación, solicitar una nueva
                val priority = Priority.PRIORITY_HIGH_ACCURACY
                val cancellationTokenSource = CancellationTokenSource()
                
                fusedLocationClient.getCurrentLocation(priority, cancellationTokenSource.token)
                    .addOnSuccessListener { curLocation ->
                        onLocationReceived(curLocation)
                    }
                    .addOnFailureListener {
                        onLocationReceived(null)
                    }
            }
        }.addOnFailureListener {
            onLocationReceived(null)
        }
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    fun sortZonesByDistance(zones: List<ParkingZone>, userLat: Double, userLon: Double): List<ParkingZone> {
        return zones.sortedBy { zone ->
            calculateDistance(userLat, userLon, zone.latitude, zone.longitude)
        }
    }
}
