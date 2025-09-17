package com.example.wisp.data.weather.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for OpenWeather 5-Day Forecast API response.
 * Maps to the JSON structure returned by the /forecast endpoint.
 */
@Serializable
data class ForecastDto(
    @SerialName("cod")
    val responseCode: String,
    
    @SerialName("message")
    val message: Int,
    
    @SerialName("cnt")
    val count: Int,
    
    @SerialName("list")
    val forecastList: List<ForecastItemDto>,
    
    @SerialName("city")
    val city: CityDto
)

@Serializable
data class ForecastItemDto(
    @SerialName("dt")
    val timestamp: Long,
    
    @SerialName("main")
    val main: MainWeatherDto,
    
    @SerialName("weather")
    val weather: List<WeatherConditionDto>,
    
    @SerialName("clouds")
    val clouds: CloudsDto,
    
    @SerialName("wind")
    val wind: WindDto,
    
    @SerialName("visibility")
    val visibility: Int,
    
    @SerialName("pop")
    val precipitationProbability: Double,
    
    @SerialName("rain")
    val rain: PrecipitationDto? = null,
    
    @SerialName("snow")
    val snow: PrecipitationDto? = null,
    
    @SerialName("sys")
    val system: ForecastSystemDto,
    
    @SerialName("dt_txt")
    val dateTimeText: String
)

@Serializable
data class PrecipitationDto(
    @SerialName("3h")
    val threeHours: Double? = null
)

@Serializable
data class ForecastSystemDto(
    @SerialName("pod")
    val partOfDay: String
)

@Serializable
data class CityDto(
    @SerialName("id")
    val id: Int,
    
    @SerialName("name")
    val name: String,
    
    @SerialName("coord")
    val coordinates: CoordinatesDto,
    
    @SerialName("country")
    val country: String,
    
    @SerialName("population")
    val population: Int,
    
    @SerialName("timezone")
    val timezone: Int,
    
    @SerialName("sunrise")
    val sunrise: Long,
    
    @SerialName("sunset")
    val sunset: Long
)
