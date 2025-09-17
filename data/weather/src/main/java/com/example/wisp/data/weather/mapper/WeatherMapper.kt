package com.example.wisp.data.weather.mapper

import com.example.wisp.data.weather.dto.CurrentWeatherDto
import com.example.wisp.data.weather.dto.ForecastDto
import com.example.wisp.data.weather.dto.ForecastItemDto
import com.example.wisp.data.weather.dto.PlaceSearchDto
import com.example.wisp.domain.model.Place
import com.example.wisp.domain.model.WeatherBundle
import com.example.wisp.domain.model.WeatherDaily
import com.example.wisp.domain.model.WeatherHourly
import com.example.wisp.domain.model.WeatherNow
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Maps OpenWeather API DTOs to domain models.
 * Handles the conversion from external API format to internal domain representation.
 */
@Singleton
class WeatherMapper @Inject constructor() {
    
    /**
     * Maps current weather DTO to domain WeatherNow model.
     */
    fun mapToWeatherNow(dto: CurrentWeatherDto): WeatherNow {
        val weather = dto.weather.firstOrNull()
        return WeatherNow(
            tempC = dto.main.temperature,
            tempF = celsiusToFahrenheit(dto.main.temperature),
            condition = weather?.description?.replaceFirstChar { it.uppercaseChar() } ?: "Unknown",
            icon = weather?.icon ?: "01d",
            humidity = dto.main.humidity,
            windKph = dto.wind.speed * 3.6, // Convert m/s to km/h
            feelsLikeC = dto.main.feelsLike,
            dt = dto.timestamp
        )
    }
    
    /**
     * Maps forecast DTOs to hourly weather models.
     * Filters to get the next 24 hours of data.
     */
    fun mapToHourlyWeather(forecastItems: List<ForecastItemDto>): List<WeatherHourly> {
        return forecastItems
            .take(8) // Take first 8 items (24 hours = 8 * 3-hour intervals)
            .map { item ->
                val weather = item.weather.firstOrNull()
                WeatherHourly(
                    dt = item.timestamp,
                    tempC = item.main.temperature,
                    icon = weather?.icon ?: "01d",
                    precipMm = (item.rain?.threeHours ?: 0.0) + (item.snow?.threeHours ?: 0.0)
                )
            }
    }
    
    /**
     * Maps forecast DTOs to daily weather models.
     * Groups 3-hour intervals by day and calculates min/max temperatures.
     */
    fun mapToDailyWeather(forecastItems: List<ForecastItemDto>): List<WeatherDaily> {
        val dailyGroups = forecastItems.groupBy { item ->
            // Group by day (ignore time)
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = item.timestamp * 1000
            calendar.get(Calendar.DAY_OF_YEAR)
        }
        
        return dailyGroups.values.map { dayItems ->
            val temperatures = dayItems.map { it.main.temperature }
            val minTemp = temperatures.minOrNull() ?: 0.0
            val maxTemp = temperatures.maxOrNull() ?: 0.0
            
            // Use the weather condition from the middle of the day
            val middleItem = dayItems[dayItems.size / 2]
            val weather = middleItem.weather.firstOrNull()
            
            WeatherDaily(
                dt = dayItems.first().timestamp,
                minC = minTemp,
                maxC = maxTemp,
                icon = weather?.icon ?: "01d"
            )
        }.take(7) // Take first 7 days
    }
    
    /**
     * Maps place search DTO to domain Place model.
     */
    fun mapToPlace(dto: PlaceSearchDto): Place {
        val displayName = buildString {
            append(dto.name)
            dto.state?.let { append(", $it") }
            append(", ${dto.country}")
        }
        
        return Place(
            id = "${dto.latitude}_${dto.longitude}",
            name = displayName,
            lat = dto.latitude,
            lon = dto.longitude
        )
    }
    
    /**
     * Maps current weather and forecast DTOs to complete WeatherBundle.
     */
    fun mapToWeatherBundle(
        currentWeather: CurrentWeatherDto,
        forecast: ForecastDto
    ): WeatherBundle {
        val place = Place(
            id = "${currentWeather.coordinates.latitude}_${currentWeather.coordinates.longitude}",
            name = currentWeather.cityName,
            lat = currentWeather.coordinates.latitude,
            lon = currentWeather.coordinates.longitude
        )
        
        return WeatherBundle(
            now = mapToWeatherNow(currentWeather),
            hourly = mapToHourlyWeather(forecast.forecastList),
            daily = mapToDailyWeather(forecast.forecastList),
            place = place
        )
    }
    
    /**
     * Converts Celsius to Fahrenheit.
     */
    private fun celsiusToFahrenheit(celsius: Double): Double {
        return (celsius * 9.0 / 5.0) + 32.0
    }
}


