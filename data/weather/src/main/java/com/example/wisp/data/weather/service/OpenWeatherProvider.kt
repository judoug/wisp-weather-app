package com.example.wisp.data.weather.service

import com.example.wisp.data.weather.mapper.WeatherMapper
import com.example.wisp.domain.exception.WeatherApiException
import com.example.wisp.domain.model.Place
import com.example.wisp.domain.model.WeatherBundle
import com.example.wisp.domain.provider.WeatherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of WeatherProvider that uses OpenWeather API.
 * Handles network requests, error handling, and data mapping.
 */
@Singleton
class OpenWeatherProvider @Inject constructor(
    private val weatherService: OpenWeatherService,
    private val weatherMapper: WeatherMapper
) : WeatherProvider {
    
    override suspend fun fetchByLatLon(lat: Double, lon: Double): WeatherBundle {
        return withContext(Dispatchers.IO) {
            try {
                // Fetch current weather and forecast in parallel
                val currentWeatherDeferred = async { weatherService.getCurrentWeather(lat, lon) }
                val forecastDeferred = async { weatherService.getForecast(lat, lon) }
                
                val currentWeather = currentWeatherDeferred.await()
                val forecast = forecastDeferred.await()
                
                weatherMapper.mapToWeatherBundle(currentWeather, forecast)
                
            } catch (e: HttpException) {
                throw when (e.code()) {
                    401 -> WeatherApiException("Invalid API key", e)
                    404 -> WeatherApiException("Location not found", e)
                    429 -> WeatherApiException("API rate limit exceeded", e)
                    500, 502, 503, 504 -> WeatherApiException("Weather service temporarily unavailable", e)
                    else -> WeatherApiException("Failed to fetch weather data: ${e.message}", e)
                }
            } catch (e: SocketTimeoutException) {
                throw WeatherApiException("Request timed out. Please check your internet connection.", e)
            } catch (e: UnknownHostException) {
                throw WeatherApiException("No internet connection available", e)
            } catch (e: IOException) {
                throw WeatherApiException("Network error: ${e.message}", e)
            } catch (e: Exception) {
                throw WeatherApiException("Unexpected error: ${e.message}", e)
            }
        }
    }
    
    override suspend fun searchPlaces(query: String, limit: Int): List<Place> {
        return withContext(Dispatchers.IO) {
            try {
                val searchResults = weatherService.searchPlaces(query, limit)
                searchResults.map { weatherMapper.mapToPlace(it) }
                
            } catch (e: HttpException) {
                throw when (e.code()) {
                    401 -> WeatherApiException("Invalid API key", e)
                    404 -> WeatherApiException("No places found for query: $query", e)
                    429 -> WeatherApiException("API rate limit exceeded", e)
                    500, 502, 503, 504 -> WeatherApiException("Search service temporarily unavailable", e)
                    else -> WeatherApiException("Failed to search places: ${e.message}", e)
                }
            } catch (e: SocketTimeoutException) {
                throw WeatherApiException("Search request timed out. Please check your internet connection.", e)
            } catch (e: UnknownHostException) {
                throw WeatherApiException("No internet connection available", e)
            } catch (e: IOException) {
                throw WeatherApiException("Network error during search: ${e.message}", e)
            } catch (e: Exception) {
                throw WeatherApiException("Unexpected error during search: ${e.message}", e)
            }
        }
    }
}
