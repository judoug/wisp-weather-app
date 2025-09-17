package com.example.wisp.data.weather.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for OpenWeather Geocoding API response.
 * Maps to the JSON structure returned by the /geo/1.0/direct endpoint.
 */
@Serializable
data class PlaceSearchDto(
    @SerialName("name")
    val name: String,
    
    @SerialName("local_names")
    val localNames: Map<String, String>? = null,
    
    @SerialName("lat")
    val latitude: Double,
    
    @SerialName("lon")
    val longitude: Double,
    
    @SerialName("country")
    val country: String,
    
    @SerialName("state")
    val state: String? = null
)
