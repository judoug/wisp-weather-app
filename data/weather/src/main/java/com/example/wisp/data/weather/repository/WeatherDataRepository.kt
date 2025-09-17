package com.example.wisp.data.weather.repository

import com.example.wisp.data.db.repository.DatabaseWeatherRepository
import com.example.wisp.data.weather.network.NetworkConnectivityManager
import com.example.wisp.data.weather.service.OpenWeatherProvider
import com.example.wisp.domain.exception.WeatherApiException
import com.example.wisp.domain.exception.WeatherException
import com.example.wisp.domain.model.Place
import com.example.wisp.domain.model.WeatherBundle
import com.example.wisp.domain.provider.WeatherProvider
import com.example.wisp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Integrated weather data repository that combines API client with database caching.
 * 
 * This repository implements intelligent caching strategies:
 * - Fetches fresh data from API when cache is stale or missing
 * - Stores API responses in local database for offline access
 * - Handles network failures gracefully with cached data fallback
 * - Optimizes API calls by respecting cache TTL
 * - Provides reactive data streams for UI updates
 */
@Singleton
class WeatherDataRepository @Inject constructor(
    private val weatherProvider: WeatherProvider,
    private val databaseRepository: DatabaseWeatherRepository,
    private val networkConnectivityManager: NetworkConnectivityManager
) : WeatherRepository {
    
    companion object {
        private const val CACHE_TTL_MS = 15 * 60 * 1000L // 15 minutes
    }
    
    override suspend fun weatherFor(place: Place, forceRefresh: Boolean): WeatherBundle {
        return try {
            // Check if we're offline
            val isOffline = !networkConnectivityManager.isNetworkConnected()
            
            // Try to get cached data first (unless force refresh is requested)
            if (!forceRefresh) {
                val cachedWeather = getCachedWeatherIfValid(place.id)
                if (cachedWeather != null) {
                    return cachedWeather
                }
            }
            
            // If we're offline, try to return any cached data (even if stale)
            if (isOffline) {
                val cachedWeather = getCachedWeatherIfValid(place.id, ignoreStaleness = true)
                if (cachedWeather != null) {
                    return cachedWeather
                } else {
                    throw WeatherApiException("No internet connection and no cached data available")
                }
            }
            
            // Fetch fresh data from API
            val freshWeather = fetchFreshWeatherData(place)
            
            // Cache the fresh data
            databaseRepository.cacheWeatherData(freshWeather)
            
            freshWeather
            
        } catch (e: WeatherApiException) {
            // If API fails, try to return cached data (even if stale)
            val cachedWeather = getCachedWeatherIfValid(place.id, ignoreStaleness = true)
            if (cachedWeather != null) {
                cachedWeather
            } else {
                throw e
            }
        } catch (e: Exception) {
            // For any other exception, try cached data as fallback
            val cachedWeather = getCachedWeatherIfValid(place.id, ignoreStaleness = true)
            if (cachedWeather != null) {
                cachedWeather
            } else {
                throw WeatherApiException("Failed to fetch weather data: ${e.message}", e)
            }
        }
    }
    
    override suspend fun savedPlaces(): List<Place> {
        return databaseRepository.savedPlaces()
    }
    
    override suspend fun addPlace(place: Place) {
        databaseRepository.addPlace(place)
    }
    
    override suspend fun removePlace(id: String) {
        databaseRepository.removePlace(id)
    }
    
    override suspend fun setPrimary(id: String) {
        databaseRepository.setPrimary(id)
    }
    
    /**
     * Fetches fresh weather data from the API for a given place.
     */
    private suspend fun fetchFreshWeatherData(place: Place): WeatherBundle {
        return weatherProvider.fetchByLatLon(place.lat, place.lon)
    }
    
    /**
     * Gets cached weather data if it exists and is valid (not stale).
     * 
     * @param placeId The place ID to get cached data for
     * @param ignoreStaleness If true, returns cached data even if stale
     * @return Cached weather bundle or null if not available/valid
     */
    private suspend fun getCachedWeatherIfValid(
        placeId: String, 
        ignoreStaleness: Boolean = false
    ): WeatherBundle? {
        return try {
            // Check if we have cached data
            val cachedTimestamp = databaseRepository.getCacheTimestamp(placeId)
            if (cachedTimestamp == null) {
                return null
            }
            
            // Check if cache is still valid (unless ignoring staleness)
            if (!ignoreStaleness && !isCacheValid(cachedTimestamp)) {
                return null
            }
            
            // Get the cached weather data
            databaseRepository.getCachedWeather(placeId)
            
        } catch (e: Exception) {
            // If there's any error accessing cached data, return null
            null
        }
    }
    
    /**
     * Checks if cached data is still valid based on TTL.
     */
    private fun isCacheValid(timestamp: Long): Boolean {
        return (System.currentTimeMillis() - timestamp) < CACHE_TTL_MS
    }
    
    /**
     * Gets saved places as a Flow for reactive UI updates.
     */
    fun savedPlacesFlow(): Flow<List<Place>> {
        return databaseRepository.savedPlacesFlow()
    }
    
    /**
     * Gets weather data for a place as a Flow for reactive UI updates.
     */
    fun weatherForFlow(placeId: String): Flow<WeatherBundle?> {
        return databaseRepository.weatherForFlow(placeId)
    }
    
    /**
     * Refreshes weather data for all saved places in the background.
     * This can be called periodically to keep data fresh.
     */
    suspend fun refreshAllPlaces(): Map<String, Result<WeatherBundle>> {
        val places = savedPlaces()
        val results = mutableMapOf<String, Result<WeatherBundle>>()
        
        places.forEach { place ->
            try {
                val weather = weatherFor(place, forceRefresh = true)
                results[place.id] = Result.success(weather)
            } catch (e: Exception) {
                results[place.id] = Result.failure(e)
            }
        }
        
        return results
    }
    
    /**
     * Checks if the device is currently offline.
     * Uses network connectivity manager for accurate offline detection.
     */
    fun isOffline(): Boolean {
        return !networkConnectivityManager.isNetworkConnected()
    }
    
    /**
     * Gets the network connectivity status as a Flow for reactive updates.
     */
    fun connectivityFlow(): Flow<Boolean> {
        return networkConnectivityManager.isConnectedFlow
    }
    
    /**
     * Checks if we have any cached data available for offline use.
     */
    suspend fun hasCachedData(): Boolean {
        val places = savedPlaces()
        if (places.isEmpty()) {
            return false
        }
        
        // Check if we have any cached data (even if stale)
        return places.any { place ->
            getCachedWeatherIfValid(place.id, ignoreStaleness = true) != null
        }
    }
    
    /**
     * Gets the cache timestamp for a place.
     */
    suspend fun getCacheTimestamp(placeId: String): Long? {
        return databaseRepository.getCacheTimestamp(placeId)
    }
}
