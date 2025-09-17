package com.example.wisp.data.db.mapper

import com.example.wisp.data.db.entity.PlaceEntity
import com.example.wisp.domain.model.Place
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for PlaceMapper.
 */
class PlaceMapperTest {
    
    @Test
    fun `toDomain converts entity to domain model`() {
        val entity = PlaceEntity(
            id = "test-id",
            name = "Test City",
            lat = 40.7128,
            lon = -74.0060,
            isPrimary = true,
            createdAt = 1234567890L
        )
        
        val domain = PlaceMapper.toDomain(entity)
        
        assertEquals("test-id", domain.id)
        assertEquals("Test City", domain.name)
        assertEquals(40.7128, domain.lat, 0.0001)
        assertEquals(-74.0060, domain.lon, 0.0001)
    }
    
    @Test
    fun `toEntity converts domain model to entity`() {
        val domain = Place(
            id = "test-id",
            name = "Test City",
            lat = 40.7128,
            lon = -74.0060
        )
        
        val entity = PlaceMapper.toEntity(domain, isPrimary = true)
        
        assertEquals("test-id", entity.id)
        assertEquals("Test City", entity.name)
        assertEquals(40.7128, entity.lat, 0.0001)
        assertEquals(-74.0060, entity.lon, 0.0001)
        assertTrue(entity.isPrimary)
        assertTrue(entity.createdAt > 0)
    }
    
    @Test
    fun `toDomainList converts list of entities`() {
        val entities = listOf(
            PlaceEntity("id1", "City 1", 40.0, -74.0),
            PlaceEntity("id2", "City 2", 41.0, -75.0)
        )
        
        val domains = PlaceMapper.toDomainList(entities)
        
        assertEquals(2, domains.size)
        assertEquals("id1", domains[0].id)
        assertEquals("City 1", domains[0].name)
        assertEquals("id2", domains[1].id)
        assertEquals("City 2", domains[1].name)
    }
}
