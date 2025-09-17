package com.example.wisp.data.db.dao

import androidx.room.*
import com.example.wisp.data.db.entity.WeatherNowEntity
import com.example.wisp.data.db.entity.WeatherHourlyEntity
import com.example.wisp.data.db.entity.WeatherDailyEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for weather data operations.
 */
@Dao
interface WeatherDao {
    
    // WeatherNow operations
    
    @Query("SELECT * FROM weather_now WHERE placeId = :placeId")
    suspend fun getWeatherNow(placeId: String): WeatherNowEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherNow(weather: WeatherNowEntity)
    
    @Query("DELETE FROM weather_now WHERE placeId = :placeId")
    suspend fun deleteWeatherNow(placeId: String)
    
    // WeatherHourly operations
    
    @Query("SELECT * FROM weather_hourly WHERE placeId = :placeId ORDER BY dt ASC")
    suspend fun getWeatherHourly(placeId: String): List<WeatherHourlyEntity>
    
    @Query("SELECT * FROM weather_hourly WHERE placeId = :placeId ORDER BY dt ASC")
    fun getWeatherHourlyFlow(placeId: String): Flow<List<WeatherHourlyEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherHourly(weather: List<WeatherHourlyEntity>)
    
    @Query("DELETE FROM weather_hourly WHERE placeId = :placeId")
    suspend fun deleteWeatherHourly(placeId: String)
    
    // WeatherDaily operations
    
    @Query("SELECT * FROM weather_daily WHERE placeId = :placeId ORDER BY dt ASC")
    suspend fun getWeatherDaily(placeId: String): List<WeatherDailyEntity>
    
    @Query("SELECT * FROM weather_daily WHERE placeId = :placeId ORDER BY dt ASC")
    fun getWeatherDailyFlow(placeId: String): Flow<List<WeatherDailyEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherDaily(weather: List<WeatherDailyEntity>)
    
    @Query("DELETE FROM weather_daily WHERE placeId = :placeId")
    suspend fun deleteWeatherDaily(placeId: String)
    
    // Combined operations
    
    /**
     * Deletes all weather data for a specific place.
     */
    @Transaction
    suspend fun deleteAllWeatherForPlace(placeId: String) {
        deleteWeatherNow(placeId)
        deleteWeatherHourly(placeId)
        deleteWeatherDaily(placeId)
    }
    
    /**
     * Gets the cache timestamp for weather data of a place.
     */
    @Query("SELECT cachedAt FROM weather_now WHERE placeId = :placeId")
    suspend fun getWeatherCacheTimestamp(placeId: String): Long?
    
    /**
     * Checks if weather data exists for a place.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM weather_now WHERE placeId = :placeId)")
    suspend fun weatherExists(placeId: String): Boolean
}
