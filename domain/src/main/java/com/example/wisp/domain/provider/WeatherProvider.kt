package com.example.wisp.domain.provider

import com.example.wisp.domain.model.Place
import com.example.wisp.domain.model.WeatherBundle

/**
 * Provider interface for weather data from external APIs.
 * Handles fetching weather data and searching for places.
 */
interface WeatherProvider {
    
    /**
     * Fetches complete weather data for a location specified by coordinates.
     * 
     * @param lat Latitude coordinate
     * @param lon Longitude coordinate
     * @return Complete weather bundle with current, hourly, and daily forecasts
     * @throws WeatherException if the request fails
     */
    suspend fun fetchByLatLon(lat: Double, lon: Double): WeatherBundle
    
    /**
     * Searches for places by name or partial name.
     * 
     * @param query Search query (e.g., "New York", "London", "Paris")
     * @param limit Maximum number of results to return (default: 8)
     * @return List of matching places, ordered by relevance
     * @throws WeatherException if the request fails
     */
    suspend fun searchPlaces(query: String, limit: Int = 8): List<Place>
}
