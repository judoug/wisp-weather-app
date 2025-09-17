package com.example.wisp.domain.provider

/**
 * Provider interface for time-related operations.
 * Useful for testing TTL (Time To Live) logic and cache expiration.
 */
interface TimeProvider {
    
    /**
     * Gets the current time as Unix epoch seconds.
     * 
     * @return Current time in seconds since Unix epoch
     */
    fun nowEpochSeconds(): Long
}
