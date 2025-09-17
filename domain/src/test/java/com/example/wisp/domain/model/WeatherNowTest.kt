package com.example.wisp.domain.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherNowTest {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    @Test
    fun `serialization and deserialization works correctly`() {
        val weatherNow = WeatherNow(
            tempC = 22.5,
            tempF = 72.5,
            condition = "Clear",
            icon = "01d",
            humidity = 65,
            windKph = 12.3,
            feelsLikeC = 24.1,
            dt = 1640995200L
        )
        
        val jsonString = json.encodeToString(WeatherNow.serializer(), weatherNow)
        val deserialized = json.decodeFromString(WeatherNow.serializer(), jsonString)
        
        assertEquals(weatherNow, deserialized)
    }
    
    @Test
    fun `feelsLikeF is computed correctly`() {
        val weatherNow = WeatherNow(
            tempC = 0.0,
            tempF = 32.0,
            condition = "Clear",
            icon = "01d",
            humidity = 50,
            windKph = 5.0,
            feelsLikeC = 2.0,
            dt = 1640995200L
        )
        
        assertEquals(35.6, weatherNow.feelsLikeF, 0.1)
    }
    
    @Test
    fun `deserialization from JSON works`() {
        val jsonString = """
            {
                "tempC": 15.0,
                "tempF": 59.0,
                "condition": "Cloudy",
                "icon": "04d",
                "humidity": 80,
                "windKph": 8.5,
                "feelsLikeC": 14.2,
                "dt": 1640995200
            }
        """.trimIndent()
        
        val weatherNow = json.decodeFromString(WeatherNow.serializer(), jsonString)
        
        assertEquals(15.0, weatherNow.tempC, 0.1)
        assertEquals(59.0, weatherNow.tempF, 0.1)
        assertEquals("Cloudy", weatherNow.condition)
        assertEquals("04d", weatherNow.icon)
        assertEquals(80, weatherNow.humidity)
        assertEquals(8.5, weatherNow.windKph, 0.1)
        assertEquals(14.2, weatherNow.feelsLikeC, 0.1)
        assertEquals(1640995200L, weatherNow.dt)
    }
}
