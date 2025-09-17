package com.example.wisp.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents hourly weather forecast data.
 * 
 * @param dt Unix timestamp for this hour
 * @param tempC Temperature in Celsius for this hour
 * @param icon Weather icon identifier
 * @param precipMm Precipitation amount in millimeters
 */
@Serializable
data class WeatherHourly(
    val dt: Long,
    val tempC: Double,
    val icon: String,
    val precipMm: Double
) {
    /**
     * Computed temperature in Fahrenheit
     */
    val tempF: Double
        get() = (tempC * 9.0 / 5.0) + 32.0
}
