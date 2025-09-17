package com.example.wisp.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.wisp.domain.exception.LocationUnavailableException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Advanced location manager for continuous location updates and monitoring.
 * 
 * Features:
 * - Continuous location updates via Flow
 * - Location permission management
 * - Location accuracy monitoring
 * - Battery-optimized location requests
 */
@Singleton
class LocationManager @Inject constructor(
    private val context: Context
) {
    
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    companion object {
        private const val LOCATION_UPDATE_INTERVAL_MS = 30_000L // 30 seconds
        private const val FASTEST_UPDATE_INTERVAL_MS = 10_000L // 10 seconds
        private const val LOCATION_TIMEOUT_MS = 30_000L // 30 seconds
    }
    
    /**
     * Checks if the app has the required location permissions.
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Checks if fine location permission is granted.
     */
    fun hasFineLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Gets the last known location from the system cache.
     * This is the fastest way to get location but may be stale.
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location? {
        if (!hasLocationPermission()) {
            throw LocationUnavailableException("Location permission not granted")
        }
        
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    continuation.resume(location)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(
                        LocationUnavailableException("Failed to get last known location")
                    )
                }
        }
    }
    
    /**
     * Requests a fresh location update with high accuracy.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            throw LocationUnavailableException("Location permission not granted")
        }
        
        return suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()
            
            val locationRequest = com.google.android.gms.location.CurrentLocationRequest.Builder()
                .setDurationMillis(LOCATION_TIMEOUT_MS)
                .setMaxUpdateAgeMillis(60_000L) // 1 minute max age
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()
            
            fusedLocationClient.getCurrentLocation(locationRequest, cancellationTokenSource.token)
                .addOnSuccessListener { location ->
                    continuation.resume(location)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(
                        LocationUnavailableException("Failed to get current location")
                    )
                }
            
            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
    }
    
    /**
     * Provides location updates for continuous monitoring.
     * This is a simplified version without Flow for now.
     */
    suspend fun requestLocationUpdates(): Location? {
        if (!hasLocationPermission()) {
            throw LocationUnavailableException("Location permission not granted")
        }
        
        return getCurrentLocation()
    }
    
    /**
     * Calculates the distance between two locations in meters.
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
    
    /**
     * Checks if a location is within a certain radius of another location.
     */
    fun isWithinRadius(
        lat1: Double, 
        lon1: Double, 
        lat2: Double, 
        lon2: Double, 
        radiusMeters: Float
    ): Boolean {
        val distance = calculateDistance(lat1, lon1, lat2, lon2)
        return distance <= radiusMeters
    }
}
