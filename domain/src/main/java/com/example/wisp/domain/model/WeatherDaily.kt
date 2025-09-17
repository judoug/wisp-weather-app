package com.example.wisp.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents daily weather forecast data.
 * 
 * @param dt Unix timestamp for this day (start of day)
 * @param minC Minimum temperature in Celsius for this day
 * @param maxC Maximum temperature in Celsius for this day
 * @param icon Weather icon identifier (typically for the day's primary condition)
 */
@Serializable
data class WeatherDaily(
    val dt: Long,
    val minC: Double,
    val maxC: Double,
    val icon: String
) {
    /**
     * Computed minimum temperature in Fahrenheit
     */
    val minF: Double
        get() = (minC * 9.0 / 5.0) + 32.0
    
    /**
     * Computed maximum temperature in Fahrenheit
     */
    val maxF: Double
        get() = (maxC * 9.0 / 5.0) + 32.0
}
