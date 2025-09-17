package com.example.wisp.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.wisp.domain.exception.LocationUnavailableException
import com.example.wisp.domain.provider.LocationProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Android implementation of LocationProvider using Google Play Services Location API.
 * 
 * Features:
 * - GPS and Network location providers
 * - Permission checking for location access
 * - Location caching with configurable TTL
 * - Fallback strategies for location retrieval
 * - Coroutine-based async operations
 */
@Singleton
class AndroidLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationProvider {

    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    private val locationCache = LocationCache()
    
    companion object {
        private const val LOCATION_TIMEOUT_MS = 30_000L // 30 seconds
        private const val LOCATION_CACHE_TTL_MS = 5 * 60 * 1000L // 5 minutes
        private const val MIN_ACCURACY_METERS = 100f // Minimum accuracy threshold
    }

    override suspend fun currentLatLonOrNull(): Pair<Double, Double>? {
        return try {
            // Check if we have a recent cached location
            locationCache.getCachedLocation()?.let { cachedLocation ->
                return@let cachedLocation
            }
            
            // Check permissions
            if (!hasLocationPermission()) {
                return null
            }
            
            // Try to get current location
            val location = getCurrentLocation()
            location?.let { loc ->
                // Cache the location if it's accurate enough
                if (loc.accuracy <= MIN_ACCURACY_METERS) {
                    locationCache.cacheLocation(loc)
                }
                Pair(loc.latitude, loc.longitude)
            }
        } catch (e: Exception) {
            // Log the error but don't throw - return null instead
            // This allows the app to continue functioning without location
            null
        }
    }
    
    /**
     * Gets the current location using the best available provider.
     * Tries GPS first, then falls back to network location.
     */
    private suspend fun getCurrentLocation(): Location? {
        return try {
            // Try to get the most recent location first (fastest)
            val lastLocation = getLastKnownLocation()
            if (lastLocation != null && isLocationRecent(lastLocation)) {
                lastLocation
            } else {
                // If no recent location, request a new one
                requestCurrentLocation()
            }
        } catch (e: SecurityException) {
            throw LocationUnavailableException("Location permission denied")
        } catch (e: Exception) {
            throw LocationUnavailableException("Failed to get location")
        }
    }
    
    /**
     * Gets the last known location (cached by the system).
     * This is the fastest way to get location but may be stale.
     */
    @SuppressLint("MissingPermission")
    private suspend fun getLastKnownLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    continuation.resume(location)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
    
    /**
     * Requests a fresh location update.
     * This is slower but more accurate than last known location.
     */
    @SuppressLint("MissingPermission")
    private suspend fun requestCurrentLocation(): Location? {
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
                    continuation.resumeWithException(exception)
                }
            
            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
    }
    
    /**
     * Checks if the app has location permissions.
     */
    private fun hasLocationPermission(): Boolean {
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
     * Checks if a location is recent enough to be considered valid.
     */
    private fun isLocationRecent(location: Location): Boolean {
        val currentTime = System.currentTimeMillis()
        val locationTime = location.time
        val age = currentTime - locationTime
        return age <= LOCATION_CACHE_TTL_MS
    }
    
    /**
     * Simple in-memory location cache with TTL.
     */
    private class LocationCache {
        private var cachedLocation: Location? = null
        private var cacheTimestamp: Long = 0
        
        fun cacheLocation(location: Location) {
            cachedLocation = location
            cacheTimestamp = System.currentTimeMillis()
        }
        
        fun getCachedLocation(): Pair<Double, Double>? {
            val currentTime = System.currentTimeMillis()
            val age = currentTime - cacheTimestamp
            
            return if (age <= LOCATION_CACHE_TTL_MS && cachedLocation != null) {
                val location = cachedLocation!!
                Pair(location.latitude, location.longitude)
            } else {
                null
            }
        }
        
        fun clearCache() {
            cachedLocation = null
            cacheTimestamp = 0
        }
    }
}
