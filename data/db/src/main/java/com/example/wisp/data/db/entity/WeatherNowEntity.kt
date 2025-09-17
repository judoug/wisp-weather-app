package com.example.wisp.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for storing current weather data.
 * Maps to the domain WeatherNow model.
 */
@Entity(
    tableName = "weather_now",
    foreignKeys = [
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["placeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["placeId"])]
)
data class WeatherNowEntity(
    @PrimaryKey
    val placeId: String,
    val tempC: Double,
    val tempF: Double,
    val condition: String,
    val icon: String,
    val humidity: Int,
    val windKph: Double,
    val feelsLikeC: Double,
    val dt: Long,
    val cachedAt: Long = System.currentTimeMillis()
)
