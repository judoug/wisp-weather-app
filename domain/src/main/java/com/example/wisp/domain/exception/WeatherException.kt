package com.example.wisp.domain.exception

/**
 * Base exception for weather-related errors.
 */
sealed class WeatherException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Thrown when a weather API request fails.
 */
class WeatherApiException(message: String, cause: Throwable? = null) : WeatherException(message, cause)

/**
 * Thrown when the maximum number of saved places is exceeded.
 */
class TooManyPlacesException(message: String = "Maximum number of places (10) exceeded") : WeatherException(message)

/**
 * Thrown when a place is not found.
 */
class PlaceNotFoundException(message: String = "Place not found") : WeatherException(message)

/**
 * Thrown when location services are unavailable or permission is denied.
 */
class LocationUnavailableException(message: String = "Location services unavailable") : WeatherException(message)
