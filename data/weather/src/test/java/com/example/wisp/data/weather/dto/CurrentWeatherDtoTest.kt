package com.example.wisp.data.weather.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CurrentWeatherDtoTest {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    @Test
    fun `deserialize current weather response from JSON`() {
        val jsonString = javaClass.classLoader
            ?.getResourceAsStream("current_weather_response.json")
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: throw AssertionError("Could not load test JSON file")
        
        val dto = json.decodeFromString<CurrentWeatherDto>(jsonString)
        
        // Verify coordinates
        assertEquals(-74.006, dto.coordinates.longitude, 0.001)
        assertEquals(40.7143, dto.coordinates.latitude, 0.001)
        
        // Verify weather conditions
        assertEquals(1, dto.weather.size)
        val weather = dto.weather[0]
        assertEquals(800, weather.id)
        assertEquals("Clear", weather.main)
        assertEquals("clear sky", weather.description)
        assertEquals("01d", weather.icon)
        
        // Verify main weather data
        assertEquals(22.5, dto.main.temperature, 0.001)
        assertEquals(24.1, dto.main.feelsLike, 0.001)
        assertEquals(20.0, dto.main.tempMin, 0.001)
        assertEquals(25.0, dto.main.tempMax, 0.001)
        assertEquals(1013, dto.main.pressure)
        assertEquals(65, dto.main.humidity)
        
        // Verify wind
        assertEquals(3.2, dto.wind.speed, 0.001)
        assertEquals(180, dto.wind.direction)
        
        // Verify clouds
        assertEquals(0, dto.clouds.coverage)
        
        // Verify timestamp and city info
        assertEquals(1695123456L, dto.timestamp)
        assertEquals("New York", dto.cityName)
        assertEquals(5128581, dto.cityId)
        assertEquals(200, dto.responseCode)
    }
}
