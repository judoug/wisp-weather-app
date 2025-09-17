package com.example.wisp.data.db.mapper

import com.example.wisp.data.db.entity.PlaceEntity
import com.example.wisp.domain.model.Place

/**
 * Maps between PlaceEntity (database) and Place (domain model).
 */
object PlaceMapper {
    
    /**
     * Converts a PlaceEntity to a domain Place model.
     */
    fun toDomain(entity: PlaceEntity): Place {
        return Place(
            id = entity.id,
            name = entity.name,
            lat = entity.lat,
            lon = entity.lon
        )
    }
    
    /**
     * Converts a domain Place model to a PlaceEntity.
     */
    fun toEntity(place: Place, isPrimary: Boolean = false): PlaceEntity {
        return PlaceEntity(
            id = place.id,
            name = place.name,
            lat = place.lat,
            lon = place.lon,
            isPrimary = isPrimary,
            createdAt = System.currentTimeMillis()
        )
    }
    
    /**
     * Converts a list of PlaceEntity to a list of domain Place models.
     */
    fun toDomainList(entities: List<PlaceEntity>): List<Place> {
        return entities.map { toDomain(it) }
    }
}
