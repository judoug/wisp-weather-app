package com.example.wisp.data.db.entity

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for PlaceEntity.
 */
class PlaceEntityTest {
    
    @Test
    fun `place entity creation with all fields`() {
        val entity = PlaceEntity(
            id = "test-id",
            name = "Test City",
            lat = 40.7128,
            lon = -74.0060,
            isPrimary = true,
            createdAt = 1234567890L
        )
        
        assertEquals("test-id", entity.id)
        assertEquals("Test City", entity.name)
        assertEquals(40.7128, entity.lat, 0.0001)
        assertEquals(-74.0060, entity.lon, 0.0001)
        assertTrue(entity.isPrimary)
        assertEquals(1234567890L, entity.createdAt)
    }
    
    @Test
    fun `place entity creation with default values`() {
        val entity = PlaceEntity(
            id = "test-id",
            name = "Test City",
            lat = 40.7128,
            lon = -74.0060
        )
        
        assertFalse(entity.isPrimary)
        assertTrue(entity.createdAt > 0)
    }
}
