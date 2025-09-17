package com.example.wisp.data.location

import android.content.Context
import android.content.SharedPreferences
import com.example.wisp.domain.provider.LocationProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Quest-specific location provider that combines manual location selection
 * with IP geolocation fallback.
 * 
 * Strategy:
 * 1. Use manual location if user has set one
 * 2. Fall back to IP geolocation if enabled
 * 3. Use default location as last resort
 * 
 * Features:
 * - Manual location priority
 * - IP geolocation fallback
 * - Privacy controls
 * - No Google Play Services dependency
 */
@Singleton
class QuestLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val manualLocationProvider: ManualLocationProvider,
    private val ipGeoLocationProvider: IpGeoLocationProvider
) : LocationProvider {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "quest_location_prefs", 
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_USE_IP_LOCATION = "use_ip_location"
        private const val DEFAULT_USE_IP_LOCATION = true
    }

    override suspend fun currentLatLonOrNull(): Pair<Double, Double>? {
        return try {
            // 1. Check if user has set a manual location
            if (manualLocationProvider.hasManualLocation()) {
                return manualLocationProvider.currentLatLonOrNull()
            }

            // 2. Check if IP location is enabled
            if (isIpLocationEnabled()) {
                return ipGeoLocationProvider.currentLatLonOrNull()
            }

            // 3. Fall back to default location
            manualLocationProvider.currentLatLonOrNull()
        } catch (e: Exception) {
            // Always return something, even if it's the default
            manualLocationProvider.currentLatLonOrNull()
        }
    }

    /**
     * Sets whether IP geolocation should be used as fallback.
     */
    fun setIpLocationEnabled(enabled: Boolean) {
        prefs.edit()
            .putBoolean(KEY_USE_IP_LOCATION, enabled)
            .apply()
    }

    /**
     * Gets whether IP geolocation is enabled.
     */
    fun isIpLocationEnabled(): Boolean {
        return prefs.getBoolean(KEY_USE_IP_LOCATION, DEFAULT_USE_IP_LOCATION)
    }

    /**
     * Sets a manual location.
     */
    fun setManualLocation(latitude: Double, longitude: Double, locationName: String? = null) {
        manualLocationProvider.setLocation(latitude, longitude, locationName)
    }

    /**
     * Gets the current location name.
     */
    fun getLocationName(): String {
        return manualLocationProvider.getLocationName()
    }

    /**
     * Clears manual location, reverting to IP/default.
     */
    fun clearManualLocation() {
        manualLocationProvider.clearLocation()
    }

    /**
     * Gets the current location source for debugging/UI purposes.
     */
    fun getLocationSource(): LocationSource {
        return when {
            manualLocationProvider.hasManualLocation() -> LocationSource.MANUAL
            isIpLocationEnabled() -> LocationSource.IP_GEOLOCATION
            else -> LocationSource.DEFAULT
        }
    }

    /**
     * Clears all location caches.
     */
    fun clearAllCaches() {
        ipGeoLocationProvider.clearCache()
        // Don't clear manual location as that's user preference
    }
}

/**
 * Enum representing the source of the current location.
 */
enum class LocationSource {
    MANUAL,           // User manually selected location
    IP_GEOLOCATION,   // IP-based approximate location
    DEFAULT          // Fallback default location
}
