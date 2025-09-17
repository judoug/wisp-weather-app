package com.example.wisp.data.db.mapper

import com.example.wisp.data.db.entity.WeatherNowEntity
import com.example.wisp.data.db.entity.WeatherHourlyEntity
import com.example.wisp.data.db.entity.WeatherDailyEntity
import com.example.wisp.domain.model.WeatherNow
import com.example.wisp.domain.model.WeatherHourly
import com.example.wisp.domain.model.WeatherDaily
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for WeatherMapper.
 */
class WeatherMapperTest {
    
    @Test
    fun `toDomain converts WeatherNowEntity to domain model`() {
        val entity = WeatherNowEntity(
            placeId = "test-place",
            tempC = 20.0,
            tempF = 68.0,
            condition = "Clear",
            icon = "01d",
            humidity = 65,
            windKph = 10.5,
            feelsLikeC = 22.0,
            dt = 1234567890L,
            cachedAt = 1234567890L
        )
        
        val domain = WeatherMapper.toDomain(entity)
        
        assertEquals(20.0, domain.tempC, 0.0001)
        assertEquals(68.0, domain.tempF, 0.0001)
        assertEquals("Clear", domain.condition)
        assertEquals("01d", domain.icon)
        assertEquals(65, domain.humidity)
        assertEquals(10.5, domain.windKph, 0.0001)
        assertEquals(22.0, domain.feelsLikeC, 0.0001)
        assertEquals(1234567890L, domain.dt)
    }
    
    @Test
    fun `toEntity converts WeatherNow domain model to entity`() {
        val domain = WeatherNow(
            tempC = 20.0,
            tempF = 68.0,
            condition = "Clear",
            icon = "01d",
            humidity = 65,
            windKph = 10.5,
            feelsLikeC = 22.0,
            dt = 1234567890L
        )
        
        val entity = WeatherMapper.toEntity(domain, "test-place")
        
        assertEquals("test-place", entity.placeId)
        assertEquals(20.0, entity.tempC, 0.0001)
        assertEquals(68.0, entity.tempF, 0.0001)
        assertEquals("Clear", entity.condition)
        assertEquals("01d", entity.icon)
        assertEquals(65, entity.humidity)
        assertEquals(10.5, entity.windKph, 0.0001)
        assertEquals(22.0, entity.feelsLikeC, 0.0001)
        assertEquals(1234567890L, entity.dt)
        assertTrue(entity.cachedAt > 0)
    }
    
    @Test
    fun `toDomain converts WeatherHourlyEntity to domain model`() {
        val entity = WeatherHourlyEntity(
            placeId = "test-place",
            dt = 1234567890L,
            tempC = 20.0,
            icon = "01d",
            precipMm = 0.0,
            cachedAt = 1234567890L
        )
        
        val domain = WeatherMapper.toDomain(entity)
        
        assertEquals(1234567890L, domain.dt)
        assertEquals(20.0, domain.tempC, 0.0001)
        assertEquals("01d", domain.icon)
        assertEquals(0.0, domain.precipMm, 0.0001)
    }
    
    @Test
    fun `toDomain converts WeatherDailyEntity to domain model`() {
        val entity = WeatherDailyEntity(
            placeId = "test-place",
            dt = 1234567890L,
            minC = 15.0,
            maxC = 25.0,
            icon = "01d",
            cachedAt = 1234567890L
        )
        
        val domain = WeatherMapper.toDomain(entity)
        
        assertEquals(1234567890L, domain.dt)
        assertEquals(15.0, domain.minC, 0.0001)
        assertEquals(25.0, domain.maxC, 0.0001)
        assertEquals("01d", domain.icon)
    }
    
    @Test
    fun `toDomainHourlyList converts list of hourly entities`() {
        val entities = listOf(
            WeatherHourlyEntity(placeId = "test", dt = 1L, tempC = 20.0, icon = "01d", precipMm = 0.0),
            WeatherHourlyEntity(placeId = "test", dt = 2L, tempC = 21.0, icon = "02d", precipMm = 0.0)
        )
        
        val domains = WeatherMapper.toDomainHourlyList(entities)
        
        assertEquals(2, domains.size)
        assertEquals(1L, domains[0].dt)
        assertEquals(20.0, domains[0].tempC, 0.0001)
        assertEquals(2L, domains[1].dt)
        assertEquals(21.0, domains[1].tempC, 0.0001)
    }
    
    @Test
    fun `toDomainDailyList converts list of daily entities`() {
        val entities = listOf(
            WeatherDailyEntity(placeId = "test", dt = 1L, minC = 15.0, maxC = 25.0, icon = "01d"),
            WeatherDailyEntity(placeId = "test", dt = 2L, minC = 16.0, maxC = 26.0, icon = "02d")
        )
        
        val domains = WeatherMapper.toDomainDailyList(entities)
        
        assertEquals(2, domains.size)
        assertEquals(1L, domains[0].dt)
        assertEquals(15.0, domains[0].minC, 0.0001)
        assertEquals(25.0, domains[0].maxC, 0.0001)
        assertEquals(2L, domains[1].dt)
        assertEquals(16.0, domains[1].minC, 0.0001)
        assertEquals(26.0, domains[1].maxC, 0.0001)
    }
}
