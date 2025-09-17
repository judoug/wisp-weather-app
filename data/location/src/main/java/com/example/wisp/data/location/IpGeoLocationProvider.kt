package com.example.wisp.data.location

import android.content.Context
import com.example.wisp.domain.provider.LocationProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

/**
 * IP-based location provider for Quest flavor that doesn't have Google Play Services.
 * Uses IP geolocation services to determine approximate location.
 * 
 * Features:
 * - IP-based geolocation fallback
 * - Privacy-conscious (approximate location only)
 * - No permissions required
 * - Caching for performance
 */
@Singleton
class IpGeoLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationProvider {

    private val httpClient = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    
    // Cache for IP location to avoid repeated API calls
    private var cachedLocation: Pair<Double, Double>? = null
    private var cacheTimestamp: Long = 0
    private val cacheTtlMs = 30 * 60 * 1000L // 30 minutes

    companion object {
        // Using ipapi.co as it's free and doesn't require API key
        private const val IP_GEOLOCATION_URL = "https://ipapi.co/json/"
        private const val FALLBACK_LAT = 40.7128 // New York City
        private const val FALLBACK_LON = -74.0060
    }

    @Serializable
    private data class IpLocationResponse(
        val latitude: Double,
        val longitude: Double,
        val city: String? = null,
        val country: String? = null
    )

    override suspend fun currentLatLonOrNull(): Pair<Double, Double>? {
        return try {
            // Check cache first
            if (isCacheValid()) {
                return cachedLocation
            }

            // Get location from IP geolocation
            val location = getLocationFromIp()
            if (location != null) {
                // Cache the result
                cachedLocation = location
                cacheTimestamp = System.currentTimeMillis()
                location
            } else {
                // Return fallback location if IP geolocation fails
                Pair(FALLBACK_LAT, FALLBACK_LON)
            }
        } catch (e: Exception) {
            // Return fallback location on any error
            Pair(FALLBACK_LAT, FALLBACK_LON)
        }
    }

    private suspend fun getLocationFromIp(): Pair<Double, Double>? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(IP_GEOLOCATION_URL)
                .addHeader("User-Agent", "WispWeatherApp/1.0")
                .build()

            val response = httpClient.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val ipLocation = json.decodeFromString<IpLocationResponse>(responseBody)
                    Pair(ipLocation.latitude, ipLocation.longitude)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun isCacheValid(): Boolean {
        val currentTime = System.currentTimeMillis()
        val age = currentTime - cacheTimestamp
        return age <= cacheTtlMs && cachedLocation != null
    }

    /**
     * Clears the location cache. Useful for testing or when user wants fresh location.
     */
    fun clearCache() {
        cachedLocation = null
        cacheTimestamp = 0
    }
}
