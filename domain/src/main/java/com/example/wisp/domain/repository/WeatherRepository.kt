package com.example.wisp.domain.repository

import com.example.wisp.domain.model.Place
import com.example.wisp.domain.model.WeatherBundle

/**
 * Repository interface for weather data management.
 * Handles caching, place management, and weather data retrieval.
 */
interface WeatherRepository {
    
    /**
     * Gets weather data for a specific place.
     * Returns cached data if available and not stale, otherwise fetches fresh data.
     * 
     * @param place The place to get weather for
     * @param forceRefresh If true, bypasses cache and fetches fresh data
     * @return Complete weather bundle for the place
     * @throws WeatherException if the request fails
     * @throws TooManyPlacesException if the place limit is exceeded
     */
    suspend fun weatherFor(place: Place, forceRefresh: Boolean = false): WeatherBundle
    
    /**
     * Gets all saved places, ordered with primary place first.
     * 
     * @return List of saved places
     */
    suspend fun savedPlaces(): List<Place>
    
    /**
     * Adds a new place to the saved places list.
     * If this is the first place, it becomes the primary place.
     * 
     * @param place The place to add
     * @throws TooManyPlacesException if adding would exceed the maximum limit (10)
     */
    suspend fun addPlace(place: Place)
    
    /**
     * Removes a place from the saved places list.
     * Also removes any cached weather data for this place.
     * 
     * @param id The ID of the place to remove
     */
    suspend fun removePlace(id: String)
    
    /**
     * Sets a place as the primary place.
     * Only one place can be primary at a time.
     * 
     * @param id The ID of the place to set as primary
     */
    suspend fun setPrimary(id: String)
}
