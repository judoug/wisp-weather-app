package com.example.wisp.domain.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaceTest {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    @Test
    fun `serialization and deserialization works correctly`() {
        val place = Place(
            id = "london_gb",
            name = "London",
            lat = 51.5074,
            lon = -0.1278
        )
        
        val jsonString = json.encodeToString(Place.serializer(), place)
        val deserialized = json.decodeFromString(Place.serializer(), jsonString)
        
        assertEquals(place, deserialized)
    }
    
    @Test
    fun `deserialization from JSON works`() {
        val jsonString = """
            {
                "id": "new_york_us",
                "name": "New York",
                "lat": 40.7128,
                "lon": -74.0060
            }
        """.trimIndent()
        
        val place = json.decodeFromString(Place.serializer(), jsonString)
        
        assertEquals("new_york_us", place.id)
        assertEquals("New York", place.name)
        assertEquals(40.7128, place.lat, 0.0001)
        assertEquals(-74.0060, place.lon, 0.0001)
    }
}
