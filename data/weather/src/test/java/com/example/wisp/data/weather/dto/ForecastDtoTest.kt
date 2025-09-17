package com.example.wisp.data.weather.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ForecastDtoTest {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    @Test
    fun `deserialize forecast response from JSON`() {
        val jsonString = javaClass.classLoader
            ?.getResourceAsStream("forecast_response.json")
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: throw AssertionError("Could not load test JSON file")
        
        val dto = json.decodeFromString<ForecastDto>(jsonString)
        
        // Verify response metadata
        assertEquals("200", dto.responseCode)
        assertEquals(0, dto.message)
        assertEquals(40, dto.count)
        
        // Verify forecast list
        assertEquals(3, dto.forecastList.size)
        
        // Verify first forecast item
        val firstItem = dto.forecastList[0]
        assertEquals(1695123456L, firstItem.timestamp)
        assertEquals(22.5, firstItem.main.temperature, 0.001)
        assertEquals(24.1, firstItem.main.feelsLike, 0.001)
        assertEquals(0.0, firstItem.precipitationProbability, 0.001)
        assertEquals("2023-09-18 12:00:00", firstItem.dateTimeText)
        
        // Verify weather condition
        assertEquals(1, firstItem.weather.size)
        val weather = firstItem.weather[0]
        assertEquals(800, weather.id)
        assertEquals("Clear", weather.main)
        assertEquals("clear sky", weather.description)
        assertEquals("01d", weather.icon)
        
        // Verify second forecast item with rain
        val secondItem = dto.forecastList[1]
        assertEquals(0.1, secondItem.precipitationProbability, 0.001)
        assertNotNull(secondItem.rain)
        assertEquals(0.5, secondItem.rain!!.threeHours!!, 0.001)
        
        // Verify third forecast item with rain
        val thirdItem = dto.forecastList[2]
        assertEquals(0.8, thirdItem.precipitationProbability, 0.001)
        assertNotNull(thirdItem.rain)
        assertEquals(2.1, thirdItem.rain!!.threeHours!!, 0.001)
        
        // Verify city information
        assertEquals("New York", dto.city.name)
        assertEquals(-74.006, dto.city.coordinates.longitude, 0.001)
        assertEquals(40.7143, dto.city.coordinates.latitude, 0.001)
        assertEquals("US", dto.city.country)
        assertEquals(5128581, dto.city.id)
    }
}
