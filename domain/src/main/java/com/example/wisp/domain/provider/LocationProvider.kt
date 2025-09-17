package com.example.wisp.domain.provider

/**
 * Provider interface for obtaining device location.
 * Returns null if location cannot be determined or permission is denied.
 */
interface LocationProvider {
    
    /**
     * Attempts to get the current device location.
     * 
     * @return Pair of (latitude, longitude) if location is available, null otherwise
     */
    suspend fun currentLatLonOrNull(): Pair<Double, Double>?
}
