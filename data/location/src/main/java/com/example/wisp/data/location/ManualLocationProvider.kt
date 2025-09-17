package com.example.wisp.data.location

import android.content.Context
import android.content.SharedPreferences
import com.example.wisp.domain.provider.LocationProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manual location provider that allows users to set their location manually.
 * Stores the selected location in SharedPreferences.
 * 
 * Features:
 * - Manual location selection
 * - Persistent storage
 * - No permissions required
 * - Privacy-friendly (user controls their location)
 */
@Singleton
class ManualLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationProvider {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "manual_location_prefs", 
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_LATITUDE = "manual_latitude"
        private const val KEY_LONGITUDE = "manual_longitude"
        private const val KEY_LOCATION_NAME = "manual_location_name"
        
        // Default location (New York City)
        private const val DEFAULT_LAT = 40.7128
        private const val DEFAULT_LON = -74.0060
        private const val DEFAULT_NAME = "New York, NY"
    }

    override suspend fun currentLatLonOrNull(): Pair<Double, Double>? {
        val lat = prefs.getFloat(KEY_LATITUDE, DEFAULT_LAT.toFloat()).toDouble()
        val lon = prefs.getFloat(KEY_LONGITUDE, DEFAULT_LON.toFloat()).toDouble()
        return Pair(lat, lon)
    }

    /**
     * Sets the manual location coordinates.
     */
    fun setLocation(latitude: Double, longitude: Double, locationName: String? = null) {
        prefs.edit()
            .putFloat(KEY_LATITUDE, latitude.toFloat())
            .putFloat(KEY_LONGITUDE, longitude.toFloat())
            .putString(KEY_LOCATION_NAME, locationName)
            .apply()
    }

    /**
     * Gets the stored location name.
     */
    fun getLocationName(): String {
        return prefs.getString(KEY_LOCATION_NAME, DEFAULT_NAME) ?: DEFAULT_NAME
    }

    /**
     * Clears the manual location, reverting to default.
     */
    fun clearLocation() {
        prefs.edit()
            .remove(KEY_LATITUDE)
            .remove(KEY_LONGITUDE)
            .remove(KEY_LOCATION_NAME)
            .apply()
    }

    /**
     * Checks if a manual location has been set (different from default).
     */
    fun hasManualLocation(): Boolean {
        val lat = prefs.getFloat(KEY_LATITUDE, DEFAULT_LAT.toFloat())
        val lon = prefs.getFloat(KEY_LONGITUDE, DEFAULT_LON.toFloat())
        return lat != DEFAULT_LAT.toFloat() || lon != DEFAULT_LON.toFloat()
    }
}
