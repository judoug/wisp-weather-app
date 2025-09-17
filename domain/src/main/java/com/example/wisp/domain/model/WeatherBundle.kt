package com.example.wisp.domain.model

import kotlinx.serialization.Serializable

/**
 * Complete weather data bundle for a specific place.
 * Contains current conditions, hourly forecast, and daily forecast.
 * 
 * @param now Current weather conditions
 * @param hourly List of hourly weather forecasts (typically 24 hours)
 * @param daily List of daily weather forecasts (typically 7-10 days)
 * @param place The location this weather data is for
 */
@Serializable
data class WeatherBundle(
    val now: WeatherNow,
    val hourly: List<WeatherHourly>,
    val daily: List<WeatherDaily>,
    val place: Place
)
