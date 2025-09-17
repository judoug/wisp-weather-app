package com.example.wisp.data.weather.sync

import com.example.wisp.data.db.repository.DatabaseWeatherRepository
import com.example.wisp.data.weather.network.NetworkConnectivityManager
import com.example.wisp.data.weather.repository.WeatherDataRepository
import com.example.wisp.domain.exception.WeatherApiException
import com.example.wisp.domain.exception.WeatherException
import com.example.wisp.domain.model.Place
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for synchronizing weather data between API and local database.
 * Handles background updates, cache management, and data freshness.
 */
@Singleton
class WeatherDataSyncService @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository,
    private val networkConnectivityManager: NetworkConnectivityManager,
    private val databaseRepository: DatabaseWeatherRepository
) {
    
    companion object {
        private const val SYNC_INTERVAL_MS = 15 * 60 * 1000L // 15 minutes
        private const val MAX_RETRY_ATTEMPTS = 3
    }
    
    /**
     * Synchronizes weather data for all saved places.
     * Only syncs when network is available and data is stale.
     * 
     * @return SyncResult containing success/failure information
     */
    suspend fun syncAllPlaces(): SyncResult {
        if (!networkConnectivityManager.isNetworkConnected()) {
            return SyncResult.NoNetwork
        }
        
        val places = weatherDataRepository.savedPlaces()
        if (places.isEmpty()) {
            return SyncResult.NoPlaces
        }
        
        val results = mutableListOf<PlaceSyncResult>()
        var successCount = 0
        var failureCount = 0
        
        places.forEach { place ->
            val result = syncPlace(place)
            results.add(result)
            
            when (result) {
                is PlaceSyncResult.Success -> successCount++
                is PlaceSyncResult.Failure -> failureCount++
                is PlaceSyncResult.Skipped -> {
                    // Skipped doesn't count as success or failure
                }
            }
        }
        
        return when {
            successCount > 0 && failureCount == 0 -> SyncResult.Success(successCount)
            successCount > 0 && failureCount > 0 -> SyncResult.PartialSuccess(successCount, failureCount)
            failureCount > 0 -> SyncResult.Failure("Failed to sync $failureCount places")
            else -> SyncResult.NoDataToSync
        }
    }
    
    /**
     * Synchronizes weather data for a specific place.
     * 
     * @param place The place to sync
     * @param forceRefresh If true, bypasses cache and forces API call
     * @return PlaceSyncResult indicating success, failure, or skip
     */
    suspend fun syncPlace(place: Place, forceRefresh: Boolean = false): PlaceSyncResult {
        return try {
            if (!networkConnectivityManager.isNetworkConnected()) {
                return PlaceSyncResult.Failure("No network connection")
            }
            
            // Check if we need to sync (data is stale or force refresh)
            if (!forceRefresh && !shouldSyncPlace(place)) {
                return PlaceSyncResult.Skipped("Data is still fresh")
            }
            
            // Fetch fresh data with retry logic
            val weatherBundle = fetchWithRetry(place)
            
            // Cache the fresh data
            databaseRepository.cacheWeatherData(weatherBundle)
            
            PlaceSyncResult.Success(place.id, weatherBundle)
            
        } catch (e: Exception) {
            PlaceSyncResult.Failure("Failed to sync ${place.name}: ${e.message}")
        }
    }
    
    /**
     * Gets a Flow that emits sync status updates for all places.
     * This can be used by the UI to show sync progress.
     */
    fun syncStatusFlow(): Flow<SyncStatus> {
        return combine(
            networkConnectivityManager.isConnectedFlow,
            weatherDataRepository.savedPlacesFlow()
        ) { isConnected, places ->
            when {
                !isConnected -> SyncStatus.Offline
                places.isEmpty() -> SyncStatus.NoPlaces
                else -> SyncStatus.Ready
            }
        }.distinctUntilChanged()
    }
    
    /**
     * Checks if a place needs to be synced based on cache age.
     */
    private suspend fun shouldSyncPlace(place: Place): Boolean {
        val cacheTimestamp = weatherDataRepository.getCacheTimestamp(place.id)
        if (cacheTimestamp == null) {
            return true // No cache, need to sync
        }
        
        val age = System.currentTimeMillis() - cacheTimestamp
        return age >= SYNC_INTERVAL_MS
    }
    
    /**
     * Fetches weather data with retry logic for resilience.
     */
    private suspend fun fetchWithRetry(place: Place): com.example.wisp.domain.model.WeatherBundle {
        var lastException: Exception? = null
        
        repeat(MAX_RETRY_ATTEMPTS) { attempt ->
            try {
                return weatherDataRepository.weatherFor(place, forceRefresh = true)
            } catch (e: Exception) {
                lastException = e
                if (attempt < MAX_RETRY_ATTEMPTS - 1) {
                    // Wait before retry (exponential backoff)
                    kotlinx.coroutines.delay(1000L * (attempt + 1))
                }
            }
        }
        
        throw WeatherApiException("Failed to fetch weather data after $MAX_RETRY_ATTEMPTS attempts", lastException)
    }
    
    /**
     * Gets the cache timestamp for a place.
     */
    private suspend fun getCacheTimestamp(placeId: String): Long? {
        return weatherDataRepository.getCacheTimestamp(placeId)
    }
}

/**
 * Result of syncing all places.
 */
sealed class SyncResult {
    object NoNetwork : SyncResult()
    object NoPlaces : SyncResult()
    object NoDataToSync : SyncResult()
    data class Success(val syncedCount: Int) : SyncResult()
    data class PartialSuccess(val successCount: Int, val failureCount: Int) : SyncResult()
    data class Failure(val message: String) : SyncResult()
}

/**
 * Result of syncing a single place.
 */
sealed class PlaceSyncResult {
    data class Success(val placeId: String, val weatherBundle: com.example.wisp.domain.model.WeatherBundle) : PlaceSyncResult()
    data class Failure(val message: String) : PlaceSyncResult()
    data class Skipped(val reason: String) : PlaceSyncResult()
}

/**
 * Current sync status for the app.
 */
enum class SyncStatus {
    Offline,
    NoPlaces,
    Ready
}
