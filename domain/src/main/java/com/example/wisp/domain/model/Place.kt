package com.example.wisp.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a geographical location/place.
 * 
 * @param id Unique identifier for this place
 * @param name Human-readable name of the place (e.g., "New York", "London")
 * @param lat Latitude coordinate
 * @param lon Longitude coordinate
 */
@Serializable
data class Place(
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double
)
