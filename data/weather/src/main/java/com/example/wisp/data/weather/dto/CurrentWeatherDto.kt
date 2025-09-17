package com.example.wisp.data.weather.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for OpenWeather Current Weather API response.
 * Maps to the JSON structure returned by the /weather endpoint.
 */
@Serializable
data class CurrentWeatherDto(
    @SerialName("coord")
    val coordinates: CoordinatesDto,
    
    @SerialName("weather")
    val weather: List<WeatherConditionDto>,
    
    @SerialName("main")
    val main: MainWeatherDto,
    
    @SerialName("wind")
    val wind: WindDto,
    
    @SerialName("clouds")
    val clouds: CloudsDto,
    
    @SerialName("dt")
    val timestamp: Long,
    
    @SerialName("sys")
    val system: SystemDto,
    
    @SerialName("timezone")
    val timezone: Int,
    
    @SerialName("id")
    val cityId: Int,
    
    @SerialName("name")
    val cityName: String,
    
    @SerialName("cod")
    val responseCode: Int
)

@Serializable
data class CoordinatesDto(
    @SerialName("lon")
    val longitude: Double,
    
    @SerialName("lat")
    val latitude: Double
)

@Serializable
data class WeatherConditionDto(
    @SerialName("id")
    val id: Int,
    
    @SerialName("main")
    val main: String,
    
    @SerialName("description")
    val description: String,
    
    @SerialName("icon")
    val icon: String
)

@Serializable
data class MainWeatherDto(
    @SerialName("temp")
    val temperature: Double,
    
    @SerialName("feels_like")
    val feelsLike: Double,
    
    @SerialName("temp_min")
    val tempMin: Double,
    
    @SerialName("temp_max")
    val tempMax: Double,
    
    @SerialName("pressure")
    val pressure: Int,
    
    @SerialName("humidity")
    val humidity: Int
)

@Serializable
data class WindDto(
    @SerialName("speed")
    val speed: Double,
    
    @SerialName("deg")
    val direction: Int
)

@Serializable
data class CloudsDto(
    @SerialName("all")
    val coverage: Int
)

@Serializable
data class SystemDto(
    @SerialName("type")
    val type: Int,
    
    @SerialName("id")
    val id: Int,
    
    @SerialName("country")
    val country: String,
    
    @SerialName("sunrise")
    val sunrise: Long,
    
    @SerialName("sunset")
    val sunset: Long
)


