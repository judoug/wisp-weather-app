package com.example.wisp.domain.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherDailyTest {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    @Test
    fun `serialization and deserialization works correctly`() {
        val weatherDaily = WeatherDaily(
            dt = 1640995200L,
            minC = 8.0,
            maxC = 22.0,
            icon = "01d"
        )
        
        val jsonString = json.encodeToString(WeatherDaily.serializer(), weatherDaily)
        val deserialized = json.decodeFromString(WeatherDaily.serializer(), jsonString)
        
        assertEquals(weatherDaily, deserialized)
    }
    
    @Test
    fun `temperature conversions are computed correctly`() {
        val weatherDaily = WeatherDaily(
            dt = 1640995200L,
            minC = 0.0,
            maxC = 20.0,
            icon = "01d"
        )
        
        assertEquals(32.0, weatherDaily.minF, 0.1)
        assertEquals(68.0, weatherDaily.maxF, 0.1)
    }
    
    @Test
    fun `deserialization from JSON works`() {
        val jsonString = """
            {
                "dt": 1640995200,
                "minC": 5.5,
                "maxC": 18.5,
                "icon": "04d"
            }
        """.trimIndent()
        
        val weatherDaily = json.decodeFromString(WeatherDaily.serializer(), jsonString)
        
        assertEquals(1640995200L, weatherDaily.dt)
        assertEquals(5.5, weatherDaily.minC, 0.1)
        assertEquals(18.5, weatherDaily.maxC, 0.1)
        assertEquals("04d", weatherDaily.icon)
    }
}
