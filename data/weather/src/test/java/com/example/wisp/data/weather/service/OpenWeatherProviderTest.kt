package com.example.wisp.data.weather.service

import com.example.wisp.data.weather.dto.CurrentWeatherDto
import com.example.wisp.data.weather.dto.ForecastDto
import com.example.wisp.data.weather.dto.PlaceSearchDto
import com.example.wisp.data.weather.mapper.WeatherMapper
import com.example.wisp.domain.exception.WeatherApiException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class OpenWeatherProviderTest {
    
    private lateinit var weatherService: OpenWeatherService
    private lateinit var weatherMapper: WeatherMapper
    private lateinit var openWeatherProvider: OpenWeatherProvider
    
    @Before
    fun setUp() {
        weatherService = mockk()
        weatherMapper = mockk()
        openWeatherProvider = OpenWeatherProvider(weatherService, weatherMapper)
    }
    
    @Test
    fun `fetchByLatLon returns weather bundle successfully`() = runTest {
        // Given
        val lat = 40.7143
        val lon = -74.006
        val currentWeatherDto = mockk<CurrentWeatherDto>()
        val forecastDto = mockk<ForecastDto>()
        val expectedWeatherBundle = mockk<com.example.wisp.domain.model.WeatherBundle>()
        
        coEvery { weatherService.getCurrentWeather(lat, lon) } returns currentWeatherDto
        coEvery { weatherService.getForecast(lat, lon) } returns forecastDto
        coEvery { weatherMapper.mapToWeatherBundle(currentWeatherDto, forecastDto) } returns expectedWeatherBundle
        
        // When
        val result = openWeatherProvider.fetchByLatLon(lat, lon)
        
        // Then
        assertEquals(expectedWeatherBundle, result)
    }
    
    @Test
    fun `fetchByLatLon throws WeatherException on 401 error`() = runTest {
        // Given
        val lat = 40.7143
        val lon = -74.006
        val response = Response.error<Any>(401, mockk())
        val httpException = HttpException(response)
        coEvery { weatherService.getCurrentWeather(lat, lon) } throws httpException
        
        // When & Then
        try {
            openWeatherProvider.fetchByLatLon(lat, lon)
            assert(false) { "Expected WeatherApiException to be thrown" }
        } catch (e: WeatherApiException) {
            assertEquals("Invalid API key", e.message)
        }
    }
    
    @Test
    fun `fetchByLatLon throws WeatherException on 404 error`() = runTest {
        // Given
        val lat = 40.7143
        val lon = -74.006
        val response = Response.error<Any>(404, mockk())
        val httpException = HttpException(response)
        coEvery { weatherService.getCurrentWeather(lat, lon) } throws httpException
        
        // When & Then
        try {
            openWeatherProvider.fetchByLatLon(lat, lon)
            assert(false) { "Expected WeatherApiException to be thrown" }
        } catch (e: WeatherApiException) {
            assertEquals("Location not found", e.message)
        }
    }
    
    @Test
    fun `fetchByLatLon throws WeatherException on timeout`() = runTest {
        // Given
        val lat = 40.7143
        val lon = -74.006
        coEvery { weatherService.getCurrentWeather(lat, lon) } throws SocketTimeoutException()
        
        // When & Then
        try {
            openWeatherProvider.fetchByLatLon(lat, lon)
            assert(false) { "Expected WeatherApiException to be thrown" }
        } catch (e: WeatherApiException) {
            assertEquals("Request timed out. Please check your internet connection.", e.message)
        }
    }
    
    @Test
    fun `fetchByLatLon throws WeatherException on no internet`() = runTest {
        // Given
        val lat = 40.7143
        val lon = -74.006
        coEvery { weatherService.getCurrentWeather(lat, lon) } throws UnknownHostException()
        
        // When & Then
        try {
            openWeatherProvider.fetchByLatLon(lat, lon)
            assert(false) { "Expected WeatherApiException to be thrown" }
        } catch (e: WeatherApiException) {
            assertEquals("No internet connection available", e.message)
        }
    }
    
    @Test
    fun `searchPlaces returns list of places successfully`() = runTest {
        // Given
        val query = "New York"
        val limit = 5
        val placeSearchDtos = listOf(mockk<PlaceSearchDto>())
        val expectedPlaces = listOf(mockk<com.example.wisp.domain.model.Place>())
        
        coEvery { weatherService.searchPlaces(query, limit) } returns placeSearchDtos
        coEvery { weatherMapper.mapToPlace(placeSearchDtos[0]) } returns expectedPlaces[0]
        
        // When
        val result = openWeatherProvider.searchPlaces(query, limit)
        
        // Then
        assertEquals(expectedPlaces, result)
    }
    
    @Test
    fun `searchPlaces throws WeatherException on 404 error`() = runTest {
        // Given
        val query = "InvalidCity"
        val response = Response.error<Any>(404, mockk())
        val httpException = HttpException(response)
        coEvery { weatherService.searchPlaces(query, 5) } throws httpException
        
        // When & Then
        try {
            openWeatherProvider.searchPlaces(query)
            assert(false) { "Expected WeatherApiException to be thrown" }
        } catch (e: WeatherApiException) {
            assertEquals("No places found for query: $query", e.message)
        }
    }
}
