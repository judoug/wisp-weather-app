package com.example.wisp.domain.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherHourlyTest {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    @Test
    fun `serialization and deserialization works correctly`() {
        val weatherHourly = WeatherHourly(
            dt = 1640995200L,
            tempC = 18.5,
            icon = "02d",
            precipMm = 0.5
        )
        
        val jsonString = json.encodeToString(WeatherHourly.serializer(), weatherHourly)
        val deserialized = json.decodeFromString(WeatherHourly.serializer(), jsonString)
        
        assertEquals(weatherHourly, deserialized)
    }
    
    @Test
    fun `tempF is computed correctly`() {
        val weatherHourly = WeatherHourly(
            dt = 1640995200L,
            tempC = 20.0,
            icon = "01d",
            precipMm = 0.0
        )
        
        assertEquals(68.0, weatherHourly.tempF, 0.1)
    }
    
    @Test
    fun `deserialization from JSON works`() {
        val jsonString = """
            {
                "dt": 1640995200,
                "tempC": 12.5,
                "icon": "10d",
                "precipMm": 2.3
            }
        """.trimIndent()
        
        val weatherHourly = json.decodeFromString(WeatherHourly.serializer(), jsonString)
        
        assertEquals(1640995200L, weatherHourly.dt)
        assertEquals(12.5, weatherHourly.tempC, 0.1)
        assertEquals("10d", weatherHourly.icon)
        assertEquals(2.3, weatherHourly.precipMm, 0.1)
    }
}
