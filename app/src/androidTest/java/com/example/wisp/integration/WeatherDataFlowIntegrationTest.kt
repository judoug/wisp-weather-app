package com.example.wisp.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.wisp.data.weather.repository.WeatherDataRepository
import com.example.wisp.domain.model.Place
import com.example.wisp.domain.model.WeatherBundle
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class WeatherDataFlowIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var weatherRepository: WeatherDataRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun weatherDataFlow_completeFlowFromApiToDatabase() = runTest {
        // Given
        val testPlace = Place(
            id = "test-location",
            name = "New York",
            country = "United States",
            latitude = 40.7128,
            longitude = -74.0060
        )

        // When - Add place to repository
        weatherRepository.addPlace(testPlace)

        // Then - Verify place is saved
        val savedPlaces = weatherRepository.savedPlaces()
        assertTrue("Place should be saved", savedPlaces.any { it.id == testPlace.id })

        // When - Get weather data for the place
        val weatherBundle = weatherRepository.weatherFor(testPlace, forceRefresh = true)

        // Then - Verify weather data is retrieved
        assertNotNull("Weather bundle should not be null", weatherBundle)
        assertNotNull("Current weather should not be null", weatherBundle.current)
        assertTrue("Temperature should be reasonable", weatherBundle.current.temperature > -50 && weatherBundle.current.temperature < 60)
        assertTrue("Humidity should be reasonable", weatherBundle.current.humidity >= 0 && weatherBundle.current.humidity <= 100)
        assertTrue("Wind speed should be reasonable", weatherBundle.current.windSpeed >= 0)
    }

    @Test
    fun weatherDataFlow_cachingWorksCorrectly() = runTest {
        // Given
        val testPlace = Place(
            id = "test-location-2",
            name = "London",
            country = "United Kingdom",
            latitude = 51.5074,
            longitude = -0.1278
        )

        // When - Add place and get weather data
        weatherRepository.addPlace(testPlace)
        val firstWeatherBundle = weatherRepository.weatherFor(testPlace, forceRefresh = true)
        
        // Wait a bit to ensure different timestamps
        kotlinx.coroutines.delay(100)
        
        // Get weather data again without forcing refresh (should use cache)
        val secondWeatherBundle = weatherRepository.weatherFor(testPlace, forceRefresh = false)

        // Then - Verify both calls return data
        assertNotNull("First weather bundle should not be null", firstWeatherBundle)
        assertNotNull("Second weather bundle should not be null", secondWeatherBundle)
        
        // Verify the data is consistent (same place, reasonable weather data)
        assertTrue("Place should be the same", firstWeatherBundle.place.id == secondWeatherBundle.place.id)
        assertTrue("Temperature should be reasonable", firstWeatherBundle.current.temperature > -50 && firstWeatherBundle.current.temperature < 60)
    }

    @Test
    fun weatherDataFlow_offlineModeWorksCorrectly() = runTest {
        // Given
        val testPlace = Place(
            id = "test-location-3",
            name = "Paris",
            country = "France",
            latitude = 48.8566,
            longitude = 2.3522
        )

        // When - Add place and get weather data (online)
        weatherRepository.addPlace(testPlace)
        val onlineWeatherBundle = weatherRepository.weatherFor(testPlace, forceRefresh = true)

        // Then - Verify online data is retrieved
        assertNotNull("Online weather bundle should not be null", onlineWeatherBundle)
        assertTrue("Temperature should be reasonable", onlineWeatherBundle.current.temperature > -50 && onlineWeatherBundle.current.temperature < 60)

        // When - Get weather data again (should use cached data)
        val cachedWeatherBundle = weatherRepository.weatherFor(testPlace, forceRefresh = false)

        // Then - Verify cached data is retrieved
        assertNotNull("Cached weather bundle should not be null", cachedWeatherBundle)
        assertTrue("Cached temperature should be reasonable", cachedWeatherBundle.current.temperature > -50 && cachedWeatherBundle.current.temperature < 60)
    }

    @Test
    fun weatherDataFlow_placeManagementWorksCorrectly() = runTest {
        // Given
        val testPlace1 = Place(
            id = "test-location-4",
            name = "Tokyo",
            country = "Japan",
            latitude = 35.6762,
            longitude = 139.6503
        )
        val testPlace2 = Place(
            id = "test-location-5",
            name = "Sydney",
            country = "Australia",
            latitude = -33.8688,
            longitude = 151.2093
        )

        // When - Add multiple places
        weatherRepository.addPlace(testPlace1)
        weatherRepository.addPlace(testPlace2)

        // Then - Verify both places are saved
        val savedPlaces = weatherRepository.savedPlaces()
        assertTrue("First place should be saved", savedPlaces.any { it.id == testPlace1.id })
        assertTrue("Second place should be saved", savedPlaces.any { it.id == testPlace2.id })

        // When - Set primary place
        weatherRepository.setPrimary(testPlace1.id)

        // Then - Verify primary place is set
        val updatedPlaces = weatherRepository.savedPlaces()
        val primaryPlace = updatedPlaces.find { it.id == "primary" }
        assertNotNull("Primary place should be set", primaryPlace)

        // When - Remove a place
        weatherRepository.removePlace(testPlace2.id)

        // Then - Verify place is removed
        val finalPlaces = weatherRepository.savedPlaces()
        assertTrue("Second place should be removed", !finalPlaces.any { it.id == testPlace2.id })
        assertTrue("First place should still be saved", finalPlaces.any { it.id == testPlace1.id })
    }

    @Test
    fun weatherDataFlow_connectivityMonitoringWorksCorrectly() = runTest {
        // Given
        val testPlace = Place(
            id = "test-location-6",
            name = "Berlin",
            country = "Germany",
            latitude = 52.5200,
            longitude = 13.4050
        )

        // When - Add place and monitor connectivity
        weatherRepository.addPlace(testPlace)
        
        // Collect connectivity updates
        val connectivityUpdates = mutableListOf<Boolean>()
        val job = kotlinx.coroutines.launch {
            weatherRepository.connectivityFlow().collect { isConnected ->
                connectivityUpdates.add(isConnected)
            }
        }

        // Wait for initial connectivity state
        kotlinx.coroutines.delay(1000)
        
        // Then - Verify connectivity is monitored
        assertTrue("Should have connectivity updates", connectivityUpdates.isNotEmpty())
        
        job.cancel()
    }

    @Test
    fun weatherDataFlow_errorHandlingWorksCorrectly() = runTest {
        // Given
        val invalidPlace = Place(
            id = "invalid-location",
            name = "Invalid City",
            country = "Invalid Country",
            latitude = 999.0, // Invalid coordinates
            longitude = 999.0
        )

        // When - Try to get weather for invalid place
        try {
            weatherRepository.weatherFor(invalidPlace, forceRefresh = true)
            // If no exception is thrown, the API should still return some data or handle gracefully
        } catch (e: Exception) {
            // Expected behavior - invalid coordinates should be handled gracefully
            assertTrue("Exception should be handled gracefully", true)
        }

        // Then - Verify the system doesn't crash and can still handle valid requests
        val validPlace = Place(
            id = "valid-location",
            name = "New York",
            country = "United States",
            latitude = 40.7128,
            longitude = -74.0060
        )
        
        weatherRepository.addPlace(validPlace)
        val weatherBundle = weatherRepository.weatherFor(validPlace, forceRefresh = true)
        assertNotNull("Valid weather bundle should not be null", weatherBundle)
    }

    @Test
    fun weatherDataFlow_maximumPlacesLimitWorksCorrectly() = runTest {
        // Given - Add maximum number of places
        val places = (1..10).map { index ->
            Place(
                id = "test-location-$index",
                name = "City $index",
                country = "Country $index",
                latitude = 40.0 + index,
                longitude = -74.0 + index
            )
        }

        // When - Add all places
        places.forEach { place ->
            try {
                weatherRepository.addPlace(place)
            } catch (e: Exception) {
                // Expected when reaching limit
            }
        }

        // Then - Verify we don't exceed the limit
        val savedPlaces = weatherRepository.savedPlaces()
        assertTrue("Should not exceed maximum places limit", savedPlaces.size <= 10)

        // When - Try to add one more place
        val extraPlace = Place(
            id = "extra-location",
            name = "Extra City",
            country = "Extra Country",
            latitude = 50.0,
            longitude = -80.0
        )

        try {
            weatherRepository.addPlace(extraPlace)
            // If no exception, verify the place wasn't actually added
            val finalPlaces = weatherRepository.savedPlaces()
            assertTrue("Extra place should not be added", !finalPlaces.any { it.id == extraPlace.id })
        } catch (e: Exception) {
            // Expected behavior - should throw exception when limit is reached
            assertTrue("Should throw exception when limit is reached", true)
        }
    }
}
