package com.example.wisp.data.weather.repository

import com.example.wisp.data.db.repository.DatabaseWeatherRepository
import com.example.wisp.data.weather.network.NetworkConnectivityManager
import com.example.wisp.domain.exception.WeatherApiException
import com.example.wisp.domain.model.Place
import com.example.wisp.domain.model.WeatherBundle
import com.example.wisp.domain.model.WeatherNow
import com.example.wisp.domain.provider.WeatherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WeatherDataRepositoryTest {
    
    private lateinit var weatherProvider: WeatherProvider
    private lateinit var databaseRepository: DatabaseWeatherRepository
    private lateinit var networkConnectivityManager: NetworkConnectivityManager
    private lateinit var weatherDataRepository: WeatherDataRepository
    
    private val testPlace = Place(
        id = "test-place-1",
        name = "Test City",
        country = "Test Country",
        latitude = 40.7128,
        longitude = -74.0060
    )
    
    private val testWeatherBundle = WeatherBundle(
        now = WeatherNow(
            temperature = 20.0,
            feelsLike = 22.0,
            humidity = 65,
            pressure = 1013,
            visibility = 10000,
            uvIndex = 5,
            windSpeed = 3.5,
            windDirection = 180,
            description = "Partly cloudy",
            icon = "02d"
        ),
        hourly = emptyList(),
        daily = emptyList(),
        place = testPlace
    )
    
    @BeforeEach
    fun setup() {
        weatherProvider = mockk()
        databaseRepository = mockk()
        networkConnectivityManager = mockk()
        weatherDataRepository = WeatherDataRepository(
            weatherProvider,
            databaseRepository,
            networkConnectivityManager
        )
    }
    
    @Test
    fun `weatherFor should return cached data when valid and not force refresh`() = runTest {
        // Given
        every { networkConnectivityManager.isNetworkConnected() } returns true
        coEvery { databaseRepository.getCacheTimestamp(testPlace.id) } returns System.currentTimeMillis() - 5 * 60 * 1000L // 5 minutes ago
        coEvery { databaseRepository.getCachedWeather(testPlace.id) } returns testWeatherBundle
        
        // When
        val result = weatherDataRepository.weatherFor(testPlace, forceRefresh = false)
        
        // Then
        assertEquals(testWeatherBundle, result)
        coVerify(exactly = 0) { weatherProvider.fetchByLatLon(any(), any()) }
    }
    
    @Test
    fun `weatherFor should fetch fresh data when cache is stale`() = runTest {
        // Given
        every { networkConnectivityManager.isNetworkConnected() } returns true
        coEvery { databaseRepository.getCacheTimestamp(testPlace.id) } returns System.currentTimeMillis() - 20 * 60 * 1000L // 20 minutes ago (stale)
        coEvery { weatherProvider.fetchByLatLon(testPlace.latitude!!, testPlace.longitude!!) } returns testWeatherBundle
        coEvery { databaseRepository.cacheWeatherData(testWeatherBundle) } returns Unit
        
        // When
        val result = weatherDataRepository.weatherFor(testPlace, forceRefresh = false)
        
        // Then
        assertEquals(testWeatherBundle, result)
        coVerify { weatherProvider.fetchByLatLon(testPlace.latitude!!, testPlace.longitude!!) }
        coVerify { databaseRepository.cacheWeatherData(testWeatherBundle) }
    }
    
    @Test
    fun `weatherFor should fetch fresh data when force refresh is true`() = runTest {
        // Given
        every { networkConnectivityManager.isNetworkConnected() } returns true
        coEvery { weatherProvider.fetchByLatLon(testPlace.latitude!!, testPlace.longitude!!) } returns testWeatherBundle
        coEvery { databaseRepository.cacheWeatherData(testWeatherBundle) } returns Unit
        
        // When
        val result = weatherDataRepository.weatherFor(testPlace, forceRefresh = true)
        
        // Then
        assertEquals(testWeatherBundle, result)
        coVerify { weatherProvider.fetchByLatLon(testPlace.latitude!!, testPlace.longitude!!) }
        coVerify { databaseRepository.cacheWeatherData(testWeatherBundle) }
    }
    
    @Test
    fun `weatherFor should return stale cached data when offline`() = runTest {
        // Given
        every { networkConnectivityManager.isNetworkConnected() } returns false
        coEvery { databaseRepository.getCacheTimestamp(testPlace.id) } returns System.currentTimeMillis() - 20 * 60 * 1000L // 20 minutes ago (stale)
        coEvery { databaseRepository.getCachedWeather(testPlace.id) } returns testWeatherBundle
        
        // When
        val result = weatherDataRepository.weatherFor(testPlace, forceRefresh = false)
        
        // Then
        assertEquals(testWeatherBundle, result)
        coVerify(exactly = 0) { weatherProvider.fetchByLatLon(any(), any()) }
    }
    
    @Test
    fun `weatherFor should throw exception when offline and no cached data`() = runTest {
        // Given
        every { networkConnectivityManager.isNetworkConnected() } returns false
        coEvery { databaseRepository.getCacheTimestamp(testPlace.id) } returns null
        
        // When & Then
        assertThrows<WeatherApiException> {
            weatherDataRepository.weatherFor(testPlace, forceRefresh = false)
        }
    }
    
    @Test
    fun `weatherFor should fallback to cached data when API fails`() = runTest {
        // Given
        every { networkConnectivityManager.isNetworkConnected() } returns true
        coEvery { databaseRepository.getCacheTimestamp(testPlace.id) } returns System.currentTimeMillis() - 20 * 60 * 1000L // 20 minutes ago (stale)
        coEvery { weatherProvider.fetchByLatLon(testPlace.latitude!!, testPlace.longitude!!) } throws WeatherApiException("API Error")
        coEvery { databaseRepository.getCachedWeather(testPlace.id) } returns testWeatherBundle
        
        // When
        val result = weatherDataRepository.weatherFor(testPlace, forceRefresh = false)
        
        // Then
        assertEquals(testWeatherBundle, result)
    }
    
    @Test
    fun `weatherFor should throw exception when API fails and no cached data`() = runTest {
        // Given
        every { networkConnectivityManager.isNetworkConnected() } returns true
        coEvery { databaseRepository.getCacheTimestamp(testPlace.id) } returns null
        coEvery { weatherProvider.fetchByLatLon(testPlace.latitude!!, testPlace.longitude!!) } throws WeatherApiException("API Error")
        
        // When & Then
        assertThrows<WeatherApiException> {
            weatherDataRepository.weatherFor(testPlace, forceRefresh = false)
        }
    }
    
    @Test
    fun `savedPlaces should delegate to database repository`() = runTest {
        // Given
        val expectedPlaces = listOf(testPlace)
        coEvery { databaseRepository.savedPlaces() } returns expectedPlaces
        
        // When
        val result = weatherDataRepository.savedPlaces()
        
        // Then
        assertEquals(expectedPlaces, result)
    }
    
    @Test
    fun `addPlace should delegate to database repository`() = runTest {
        // Given
        coEvery { databaseRepository.addPlace(testPlace) } returns Unit
        
        // When
        weatherDataRepository.addPlace(testPlace)
        
        // Then
        coVerify { databaseRepository.addPlace(testPlace) }
    }
    
    @Test
    fun `removePlace should delegate to database repository`() = runTest {
        // Given
        coEvery { databaseRepository.removePlace(testPlace.id) } returns Unit
        
        // When
        weatherDataRepository.removePlace(testPlace.id)
        
        // Then
        coVerify { databaseRepository.removePlace(testPlace.id) }
    }
    
    @Test
    fun `setPrimary should delegate to database repository`() = runTest {
        // Given
        coEvery { databaseRepository.setPrimary(testPlace.id) } returns Unit
        
        // When
        weatherDataRepository.setPrimary(testPlace.id)
        
        // Then
        coVerify { databaseRepository.setPrimary(testPlace.id) }
    }
    
    @Test
    fun `isOffline should return true when network is not connected`() {
        // Given
        every { networkConnectivityManager.isNetworkConnected() } returns false
        
        // When
        val result = weatherDataRepository.isOffline()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `isOffline should return false when network is connected`() {
        // Given
        every { networkConnectivityManager.isNetworkConnected() } returns true
        
        // When
        val result = weatherDataRepository.isOffline()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `connectivityFlow should delegate to network connectivity manager`() {
        // Given
        val expectedFlow = flowOf(true, false, true)
        every { networkConnectivityManager.isConnectedFlow } returns expectedFlow
        
        // When
        val result = weatherDataRepository.connectivityFlow()
        
        // Then
        assertEquals(expectedFlow, result)
    }
    
    @Test
    fun `hasCachedData should return true when cached data exists`() = runTest {
        // Given
        coEvery { databaseRepository.savedPlaces() } returns listOf(testPlace)
        coEvery { databaseRepository.getCacheTimestamp(testPlace.id) } returns System.currentTimeMillis()
        coEvery { databaseRepository.getCachedWeather(testPlace.id) } returns testWeatherBundle
        
        // When
        val result = weatherDataRepository.hasCachedData()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `hasCachedData should return false when no places exist`() = runTest {
        // Given
        coEvery { databaseRepository.savedPlaces() } returns emptyList()
        
        // When
        val result = weatherDataRepository.hasCachedData()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `hasCachedData should return false when no cached data exists`() = runTest {
        // Given
        coEvery { databaseRepository.savedPlaces() } returns listOf(testPlace)
        coEvery { databaseRepository.getCacheTimestamp(testPlace.id) } returns null
        
        // When
        val result = weatherDataRepository.hasCachedData()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `getCacheTimestamp should delegate to database repository`() = runTest {
        // Given
        val expectedTimestamp = System.currentTimeMillis()
        coEvery { databaseRepository.getCacheTimestamp(testPlace.id) } returns expectedTimestamp
        
        // When
        val result = weatherDataRepository.getCacheTimestamp(testPlace.id)
        
        // Then
        assertEquals(expectedTimestamp, result)
    }
}
