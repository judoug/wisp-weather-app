package com.example.wisp.data.db.dao

import androidx.room.*
import com.example.wisp.data.db.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for place operations.
 */
@Dao
interface PlaceDao {
    
    /**
     * Gets all saved places, ordered with primary place first.
     */
    @Query("SELECT * FROM places ORDER BY isPrimary DESC, createdAt ASC")
    fun getAllPlaces(): Flow<List<PlaceEntity>>
    
    /**
     * Gets a specific place by ID.
     */
    @Query("SELECT * FROM places WHERE id = :id")
    suspend fun getPlaceById(id: String): PlaceEntity?
    
    /**
     * Gets the primary place.
     */
    @Query("SELECT * FROM places WHERE isPrimary = 1 LIMIT 1")
    suspend fun getPrimaryPlace(): PlaceEntity?
    
    /**
     * Inserts a new place.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity)
    
    /**
     * Updates an existing place.
     */
    @Update
    suspend fun updatePlace(place: PlaceEntity)
    
    /**
     * Deletes a place by ID.
     */
    @Query("DELETE FROM places WHERE id = :id")
    suspend fun deletePlace(id: String)
    
    /**
     * Sets a place as primary and unsets all others.
     */
    @Transaction
    suspend fun setPrimaryPlace(id: String) {
        // First, unset all places as primary
        unsetAllPrimary()
        // Then set the specified place as primary
        setPlaceAsPrimary(id)
    }
    
    @Query("UPDATE places SET isPrimary = 0")
    suspend fun unsetAllPrimary()
    
    @Query("UPDATE places SET isPrimary = 1 WHERE id = :id")
    suspend fun setPlaceAsPrimary(id: String)
    
    /**
     * Gets the count of saved places.
     */
    @Query("SELECT COUNT(*) FROM places")
    suspend fun getPlaceCount(): Int
    
    /**
     * Checks if a place exists by ID.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM places WHERE id = :id)")
    suspend fun placeExists(id: String): Boolean
}
