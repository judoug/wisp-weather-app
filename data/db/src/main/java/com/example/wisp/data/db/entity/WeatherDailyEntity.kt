package com.example.wisp.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for storing daily weather forecast data.
 * Maps to the domain WeatherDaily model.
 */
@Entity(
    tableName = "weather_daily",
    foreignKeys = [
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["placeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["placeId", "dt"])]
)
data class WeatherDailyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val placeId: String,
    val dt: Long,
    val minC: Double,
    val maxC: Double,
    val icon: String,
    val cachedAt: Long = System.currentTimeMillis()
)
