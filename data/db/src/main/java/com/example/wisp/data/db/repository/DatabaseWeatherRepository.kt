package com.example.wisp.data.db.repository

import com.example.wisp.data.db.WispDatabase
import com.example.wisp.data.db.dao.PlaceDao
import com.example.wisp.data.db.dao.WeatherDao
import com.example.wisp.data.db.mapper.PlaceMapper
import com.example.wisp.data.db.mapper.WeatherMapper
import com.example.wisp.domain.model.Place
import com.example.wisp.domain.model.WeatherBundle
import com.example.wisp.domain.repository.WeatherRepository
import com.example.wisp.domain.exception.TooManyPlacesException
import com.example.wisp.domain.exception.WeatherApiException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Database-backed implementation of WeatherRepository.
 * Handles caching of weather data and place management.
 */
@Singleton
class DatabaseWeatherRepository @Inject constructor(
    private val database: WispDatabase
) : WeatherRepository {
    
    private val placeDao: PlaceDao = database.placeDao()
    private val weatherDao: WeatherDao = database.weatherDao()
    
    companion object {
        private const val MAX_PLACES = 10
        private const val CACHE_TTL_MS = 15 * 60 * 1000L // 15 minutes
    }
    
    override suspend fun weatherFor(place: Place, forceRefresh: Boolean): WeatherBundle {
        // Check if we have cached data and if it's still valid
        if (!forceRefresh) {
            val cachedTimestamp = weatherDao.getWeatherCacheTimestamp(place.id)
            if (cachedTimestamp != null && isCacheValid(cachedTimestamp)) {
                val cachedWeather = getCachedWeather(place.id)
                if (cachedWeather != null) {
                    return cachedWeather
                }
            }
        }
        
        // If no valid cache, we need to fetch fresh data
        // This would typically call the weather API provider
        // For now, we'll throw an exception indicating we need fresh data
        throw WeatherApiException("No cached weather data available and forceRefresh is false")
    }
    
    override suspend fun savedPlaces(): List<Place> {
        // Get current places from Flow
        val entities = placeDao.getAllPlaces().first()
        return PlaceMapper.toDomainList(entities)
    }
    
    override suspend fun addPlace(place: Place) {
        val currentCount = placeDao.getPlaceCount()
        if (currentCount >= MAX_PLACES) {
            throw TooManyPlacesException("Cannot add more than $MAX_PLACES places")
        }
        
        // If this is the first place, make it primary
        val isPrimary = currentCount == 0
        
        val entity = PlaceMapper.toEntity(place, isPrimary)
        placeDao.insertPlace(entity)
    }
    
    override suspend fun removePlace(id: String) {
        // Remove the place
        placeDao.deletePlace(id)
        
        // Remove all cached weather data for this place
        weatherDao.deleteAllWeatherForPlace(id)
        
        // If this was the primary place, set another place as primary
        val remainingPlaces = placeDao.getAllPlaces().first()
        if (remainingPlaces.isNotEmpty() && !remainingPlaces.any { it.isPrimary }) {
            // Set the first remaining place as primary
            placeDao.setPrimaryPlace(remainingPlaces.first().id)
        }
    }
    
    override suspend fun setPrimary(id: String) {
        if (!placeDao.placeExists(id)) {
            throw IllegalArgumentException("Place with id $id does not exist")
        }
        placeDao.setPrimaryPlace(id)
    }
    
    /**
     * Caches weather data for a place.
     * This method is called by the weather provider after fetching fresh data.
     */
    suspend fun cacheWeatherData(weatherBundle: WeatherBundle) {
        val placeId = weatherBundle.place.id
        
        // Cache current weather
        val nowEntity = WeatherMapper.toEntity(weatherBundle.now, placeId)
        weatherDao.insertWeatherNow(nowEntity)
        
        // Cache hourly forecast
        val hourlyEntities = WeatherMapper.toEntityHourlyList(weatherBundle.hourly, placeId)
        weatherDao.insertWeatherHourly(hourlyEntities)
        
        // Cache daily forecast
        val dailyEntities = WeatherMapper.toEntityDailyList(weatherBundle.daily, placeId)
        weatherDao.insertWeatherDaily(dailyEntities)
    }
    
    /**
     * Gets cached weather data for a place.
     */
    private suspend fun getCachedWeather(placeId: String): WeatherBundle? {
        val nowEntity = weatherDao.getWeatherNow(placeId) ?: return null
        val hourlyEntities = weatherDao.getWeatherHourly(placeId)
        val dailyEntities = weatherDao.getWeatherDaily(placeId)
        val placeEntity = placeDao.getPlaceById(placeId) ?: return null
        
        return WeatherBundle(
            now = WeatherMapper.toDomain(nowEntity),
            hourly = WeatherMapper.toDomainHourlyList(hourlyEntities),
            daily = WeatherMapper.toDomainDailyList(dailyEntities),
            place = PlaceMapper.toDomain(placeEntity)
        )
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
        return placeDao.getAllPlaces().map { entities ->
            PlaceMapper.toDomainList(entities)
        }
    }
    
    /**
     * Gets weather data for a place as a Flow for reactive UI updates.
     */
    fun weatherForFlow(placeId: String): Flow<WeatherBundle?> {
        return kotlinx.coroutines.flow.combine(
            kotlinx.coroutines.flow.flow { emit(weatherDao.getWeatherNow(placeId)) },
            weatherDao.getWeatherHourlyFlow(placeId),
            weatherDao.getWeatherDailyFlow(placeId),
            kotlinx.coroutines.flow.flow { emit(placeDao.getPlaceById(placeId)) }
        ) { nowEntity, hourlyEntities, dailyEntities, placeEntity ->
            if (nowEntity != null && placeEntity != null) {
                WeatherBundle(
                    now = WeatherMapper.toDomain(nowEntity),
                    hourly = WeatherMapper.toDomainHourlyList(hourlyEntities),
                    daily = WeatherMapper.toDomainDailyList(dailyEntities),
                    place = PlaceMapper.toDomain(placeEntity)
                )
            } else {
                null
            }
        }
    }
}