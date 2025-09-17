package com.example.wisp.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents current weather conditions at a specific location.
 * 
 * @param tempC Temperature in Celsius
 * @param tempF Temperature in Fahrenheit (computed from Celsius)
 * @param condition Weather condition description (e.g., "Clear", "Cloudy", "Rain")
 * @param icon Weather icon identifier
 * @param humidity Humidity percentage (0-100)
 * @param windKph Wind speed in kilometers per hour
 * @param feelsLikeC "Feels like" temperature in Celsius
 * @param dt Unix timestamp of when this data was recorded
 */
@Serializable
data class WeatherNow(
    val tempC: Double,
    val tempF: Double,
    val condition: String,
    val icon: String,
    val humidity: Int,
    val windKph: Double,
    val feelsLikeC: Double,
    val dt: Long
) {
    /**
     * Computed "feels like" temperature in Fahrenheit
     */
    val feelsLikeF: Double
        get() = (feelsLikeC * 9.0 / 5.0) + 32.0
}
