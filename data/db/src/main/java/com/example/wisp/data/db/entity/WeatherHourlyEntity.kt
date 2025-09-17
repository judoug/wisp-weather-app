package com.example.wisp.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for storing hourly weather forecast data.
 * Maps to the domain WeatherHourly model.
 */
@Entity(
    tableName = "weather_hourly",
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
data class WeatherHourlyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val placeId: String,
    val dt: Long,
    val tempC: Double,
    val icon: String,
    val precipMm: Double,
    val cachedAt: Long = System.currentTimeMillis()
)
