package com.example.wisp.domain.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherBundleTest {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    @Test
    fun `serialization and deserialization works correctly`() {
        val place = Place(
            id = "test_city",
            name = "Test City",
            lat = 40.7128,
            lon = -74.0060
        )
        
        val weatherNow = WeatherNow(
            tempC = 22.0,
            tempF = 71.6,
            condition = "Clear",
            icon = "01d",
            humidity = 60,
            windKph = 10.0,
            feelsLikeC = 23.0,
            dt = 1640995200L
        )
        
        val weatherHourly = listOf(
            WeatherHourly(
                dt = 1640995200L,
                tempC = 20.0,
                icon = "01d",
                precipMm = 0.0
            )
        )
        
        val weatherDaily = listOf(
            WeatherDaily(
                dt = 1640995200L,
                minC = 15.0,
                maxC = 25.0,
                icon = "01d"
            )
        )
        
        val weatherBundle = WeatherBundle(
            now = weatherNow,
            hourly = weatherHourly,
            daily = weatherDaily,
            place = place
        )
        
        val jsonString = json.encodeToString(WeatherBundle.serializer(), weatherBundle)
        val deserialized = json.decodeFromString(WeatherBundle.serializer(), jsonString)
        
        assertEquals(weatherBundle, deserialized)
    }
    
    @Test
    fun `deserialization from JSON works`() {
        val jsonString = """
            {
                "now": {
                    "tempC": 18.5,
                    "tempF": 65.3,
                    "condition": "Cloudy",
                    "icon": "04d",
                    "humidity": 70,
                    "windKph": 12.0,
                    "feelsLikeC": 17.8,
                    "dt": 1640995200
                },
                "hourly": [
                    {
                        "dt": 1640995200,
                        "tempC": 18.0,
                        "icon": "04d",
                        "precipMm": 0.5
                    }
                ],
                "daily": [
                    {
                        "dt": 1640995200,
                        "minC": 12.0,
                        "maxC": 20.0,
                        "icon": "04d"
                    }
                ],
                "place": {
                    "id": "test_city",
                    "name": "Test City",
                    "lat": 40.7128,
                    "lon": -74.0060
                }
            }
        """.trimIndent()
        
        val weatherBundle = json.decodeFromString(WeatherBundle.serializer(), jsonString)
        
        assertEquals("test_city", weatherBundle.place.id)
        assertEquals("Test City", weatherBundle.place.name)
        assertEquals(18.5, weatherBundle.now.tempC, 0.1)
        assertEquals("Cloudy", weatherBundle.now.condition)
        assertEquals(1, weatherBundle.hourly.size)
        assertEquals(1, weatherBundle.daily.size)
    }
}
