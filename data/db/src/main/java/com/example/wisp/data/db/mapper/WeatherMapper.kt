package com.example.wisp.data.db.mapper

import com.example.wisp.data.db.entity.WeatherNowEntity
import com.example.wisp.data.db.entity.WeatherHourlyEntity
import com.example.wisp.data.db.entity.WeatherDailyEntity
import com.example.wisp.domain.model.WeatherNow
import com.example.wisp.domain.model.WeatherHourly
import com.example.wisp.domain.model.WeatherDaily

/**
 * Maps between weather entities (database) and weather domain models.
 */
object WeatherMapper {
    
    /**
     * Converts a WeatherNowEntity to a domain WeatherNow model.
     */
    fun toDomain(entity: WeatherNowEntity): WeatherNow {
        return WeatherNow(
            tempC = entity.tempC,
            tempF = entity.tempF,
            condition = entity.condition,
            icon = entity.icon,
            humidity = entity.humidity,
            windKph = entity.windKph,
            feelsLikeC = entity.feelsLikeC,
            dt = entity.dt
        )
    }
    
    /**
     * Converts a domain WeatherNow model to a WeatherNowEntity.
     */
    fun toEntity(weather: WeatherNow, placeId: String): WeatherNowEntity {
        return WeatherNowEntity(
            placeId = placeId,
            tempC = weather.tempC,
            tempF = weather.tempF,
            condition = weather.condition,
            icon = weather.icon,
            humidity = weather.humidity,
            windKph = weather.windKph,
            feelsLikeC = weather.feelsLikeC,
            dt = weather.dt,
            cachedAt = System.currentTimeMillis()
        )
    }
    
    /**
     * Converts a WeatherHourlyEntity to a domain WeatherHourly model.
     */
    fun toDomain(entity: WeatherHourlyEntity): WeatherHourly {
        return WeatherHourly(
            dt = entity.dt,
            tempC = entity.tempC,
            icon = entity.icon,
            precipMm = entity.precipMm
        )
    }
    
    /**
     * Converts a domain WeatherHourly model to a WeatherHourlyEntity.
     */
    fun toEntity(weather: WeatherHourly, placeId: String): WeatherHourlyEntity {
        return WeatherHourlyEntity(
            placeId = placeId,
            dt = weather.dt,
            tempC = weather.tempC,
            icon = weather.icon,
            precipMm = weather.precipMm,
            cachedAt = System.currentTimeMillis()
        )
    }
    
    /**
     * Converts a WeatherDailyEntity to a domain WeatherDaily model.
     */
    fun toDomain(entity: WeatherDailyEntity): WeatherDaily {
        return WeatherDaily(
            dt = entity.dt,
            minC = entity.minC,
            maxC = entity.maxC,
            icon = entity.icon
        )
    }
    
    /**
     * Converts a domain WeatherDaily model to a WeatherDailyEntity.
     */
    fun toEntity(weather: WeatherDaily, placeId: String): WeatherDailyEntity {
        return WeatherDailyEntity(
            placeId = placeId,
            dt = weather.dt,
            minC = weather.minC,
            maxC = weather.maxC,
            icon = weather.icon,
            cachedAt = System.currentTimeMillis()
        )
    }
    
    /**
     * Converts lists of entities to domain models.
     */
    fun toDomainHourlyList(entities: List<WeatherHourlyEntity>): List<WeatherHourly> {
        return entities.map { toDomain(it) }
    }
    
    fun toDomainDailyList(entities: List<WeatherDailyEntity>): List<WeatherDaily> {
        return entities.map { toDomain(it) }
    }
    
    /**
     * Converts lists of domain models to entities.
     */
    fun toEntityHourlyList(weather: List<WeatherHourly>, placeId: String): List<WeatherHourlyEntity> {
        return weather.map { toEntity(it, placeId) }
    }
    
    fun toEntityDailyList(weather: List<WeatherDaily>, placeId: String): List<WeatherDailyEntity> {
        return weather.map { toEntity(it, placeId) }
    }
}
