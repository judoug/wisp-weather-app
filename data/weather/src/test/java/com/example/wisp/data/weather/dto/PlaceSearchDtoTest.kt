package com.example.wisp.data.weather.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class PlaceSearchDtoTest {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    @Test
    fun `deserialize place search response from JSON`() {
        val jsonString = javaClass.classLoader
            ?.getResourceAsStream("place_search_response.json")
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: throw AssertionError("Could not load test JSON file")
        
        val dtoList = json.decodeFromString<List<PlaceSearchDto>>(jsonString)
        
        assertEquals(3, dtoList.size)
        
        // Verify first place
        val firstPlace = dtoList[0]
        assertEquals("New York", firstPlace.name)
        assertEquals(40.7128, firstPlace.latitude, 0.001)
        assertEquals(-74.0060, firstPlace.longitude, 0.001)
        assertEquals("US", firstPlace.country)
        assertEquals("New York", firstPlace.state)
        assertNotNull(firstPlace.localNames)
        assertEquals("New York", firstPlace.localNames?.get("en"))
        assertEquals("Nueva York", firstPlace.localNames?.get("es"))
        
        // Verify second place
        val secondPlace = dtoList[1]
        assertEquals("New York", secondPlace.name)
        assertEquals(40.7589, secondPlace.latitude, 0.001)
        assertEquals(-73.9851, secondPlace.longitude, 0.001)
        assertEquals("US", secondPlace.country)
        assertEquals("New York", secondPlace.state)
        
        // Verify third place
        val thirdPlace = dtoList[2]
        assertEquals("New York Mills", thirdPlace.name)
        assertEquals(46.5181, thirdPlace.latitude, 0.001)
        assertEquals(-95.3761, thirdPlace.longitude, 0.001)
        assertEquals("US", thirdPlace.country)
        assertEquals("Minnesota", thirdPlace.state)
    }
}

