package com.example.wisp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing saved places.
 * Maps to the domain Place model.
 */
@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val isPrimary: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
