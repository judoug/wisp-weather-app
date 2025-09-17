package com.example.wisp.data.weather.service

import com.example.wisp.data.weather.dto.CurrentWeatherDto
import com.example.wisp.data.weather.dto.ForecastDto
import com.example.wisp.data.weather.dto.PlaceSearchDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for OpenWeather API endpoints.
 * Provides methods to fetch current weather, forecasts, and search for places.
 */
interface OpenWeatherService {
    
    /**
     * Fetches current weather data for a location specified by coordinates.
     * 
     * @param lat Latitude coordinate
     * @param lon Longitude coordinate
     * @param units Temperature units (metric for Celsius, imperial for Fahrenheit)
     * @param lang Language for weather descriptions
     * @return Current weather data
     */
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en"
    ): CurrentWeatherDto
    
    /**
     * Fetches 5-day weather forecast for a location specified by coordinates.
     * Returns 3-hour intervals for the next 5 days.
     * 
     * @param lat Latitude coordinate
     * @param lon Longitude coordinate
     * @param units Temperature units (metric for Celsius, imperial for Fahrenheit)
     * @param lang Language for weather descriptions
     * @return 5-day forecast data
     */
    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en"
    ): ForecastDto
    
    /**
     * Searches for places by name or partial name.
     * 
     * @param q Search query (e.g., "New York", "London", "Paris")
     * @param limit Maximum number of results to return (1-5)
     * @return List of matching places
     */
    @GET("geo/1.0/direct")
    suspend fun searchPlaces(
        @Query("q") q: String,
        @Query("limit") limit: Int = 5
    ): List<PlaceSearchDto>
}
