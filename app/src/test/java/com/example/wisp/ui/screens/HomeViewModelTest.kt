package com.example.wisp.ui.screens

import app.cash.turbine.test
import com.example.wisp.data.weather.repository.WeatherDataRepository
import com.example.wisp.domain.exception.LocationUnavailableException
import com.example.wisp.domain.exception.PlaceNotFoundException
import com.example.wisp.domain.exception.WeatherApiException
import com.example.wisp.domain.exception.WeatherException
import com.example.wisp.domain.model.Place
import com.example.wisp.domain.model.WeatherBundle
import com.example.wisp.domain.model.WeatherNow
import com.example.wisp.ui.state.HomeUiState
import com.example.wisp.ui.state.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var mockRepository: WeatherDataRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        viewModel = HomeViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be loading`() = runTest {
        // Given
        val places = listOf(createTestPlace())
        val weatherBundle = createTestWeatherBundle()
        
        coEvery { mockRepository.savedPlaces() } returns places
        coEvery { mockRepository.weatherFor(any(), any()) } returns weatherBundle
        every { mockRepository.connectivityFlow() } returns flowOf(true)

        // When
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.weatherState is UiState.Success)
            assertEquals(weatherBundle, (state.weatherState as UiState.Success).data)
            assertFalse(state.isOffline)
        }
    }

    @Test
    fun `should handle no saved places error`() = runTest {
        // Given
        coEvery { mockRepository.savedPlaces() } returns emptyList()
        every { mockRepository.connectivityFlow() } returns flowOf(true)

        // When
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.weatherState is UiState.Error)
            val errorState = state.weatherState as UiState.Error
            assertEquals("No locations saved. Please add a location to see weather data.", errorState.message)
            assertEquals(null, errorState.retryAction)
        }
    }

    @Test
    fun `should handle weather API exception`() = runTest {
        // Given
        val places = listOf(createTestPlace())
        val exception = WeatherApiException("API Error")
        
        coEvery { mockRepository.savedPlaces() } returns places
        coEvery { mockRepository.weatherFor(any(), any()) } throws exception
        every { mockRepository.connectivityFlow() } returns flowOf(true)

        // When
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.weatherState is UiState.Error)
            val errorState = state.weatherState as UiState.Error
            assertEquals("Unable to fetch weather data. Please check your connection and try again.", errorState.message)
            assertTrue(errorState.retryAction != null)
        }
    }

    @Test
    fun `should handle location unavailable exception`() = runTest {
        // Given
        val places = listOf(createTestPlace())
        val exception = LocationUnavailableException("Location unavailable")
        
        coEvery { mockRepository.savedPlaces() } returns places
        coEvery { mockRepository.weatherFor(any(), any()) } throws exception
        every { mockRepository.connectivityFlow() } returns flowOf(true)

        // When
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.weatherState is UiState.Error)
            val errorState = state.weatherState as UiState.Error
            assertEquals("Location services are unavailable. Please check your location settings.", errorState.message)
        }
    }

    @Test
    fun `should handle place not found exception`() = runTest {
        // Given
        val places = listOf(createTestPlace())
        val exception = PlaceNotFoundException("Place not found")
        
        coEvery { mockRepository.savedPlaces() } returns places
        coEvery { mockRepository.weatherFor(any(), any()) } throws exception
        every { mockRepository.connectivityFlow() } returns flowOf(true)

        // When
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.weatherState is UiState.Error)
            val errorState = state.weatherState as UiState.Error
            assertEquals("Location not found. Please try a different location.", errorState.message)
        }
    }

    @Test
    fun `should handle generic exception`() = runTest {
        // Given
        val places = listOf(createTestPlace())
        val exception = RuntimeException("Generic error")
        
        coEvery { mockRepository.savedPlaces() } returns places
        coEvery { mockRepository.weatherFor(any(), any()) } throws exception
        every { mockRepository.connectivityFlow() } returns flowOf(true)

        // When
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.weatherState is UiState.Error)
            val errorState = state.weatherState as UiState.Error
            assertEquals("An unexpected error occurred. Please try again.", errorState.message)
        }
    }

    @Test
    fun `refreshWeather should force refresh and update state`() = runTest {
        // Given
        val places = listOf(createTestPlace())
        val weatherBundle = createTestWeatherBundle()
        
        coEvery { mockRepository.savedPlaces() } returns places
        coEvery { mockRepository.weatherFor(any(), forceRefresh = true) } returns weatherBundle
        every { mockRepository.connectivityFlow() } returns flowOf(true)

        // When
        viewModel.refreshWeather()
        advanceUntilIdle()

        // Then
        coVerify { mockRepository.weatherFor(any(), forceRefresh = true) }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.weatherState is UiState.Success)
            assertFalse(state.isRefreshing)
        }
        
        viewModel.isRefreshing.test {
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `refreshWeather should handle error and reset refreshing state`() = runTest {
        // Given
        val places = listOf(createTestPlace())
        val exception = WeatherApiException("API Error")
        
        coEvery { mockRepository.savedPlaces() } returns places
        coEvery { mockRepository.weatherFor(any(), forceRefresh = true) } throws exception
        every { mockRepository.connectivityFlow() } returns flowOf(true)

        // When
        viewModel.refreshWeather()
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.weatherState is UiState.Error)
            assertFalse(state.isRefreshing)
        }
        
        viewModel.isRefreshing.test {
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `should observe connectivity and update offline state`() = runTest {
        // Given
        val places = listOf(createTestPlace())
        val weatherBundle = createTestWeatherBundle()
        
        coEvery { mockRepository.savedPlaces() } returns places
        coEvery { mockRepository.weatherFor(any(), any()) } returns weatherBundle
        every { mockRepository.connectivityFlow() } returns flowOf(false)

        // When
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isOffline)
        }
    }

    @Test
    fun `retryLoadWeather should reload weather data`() = runTest {
        // Given
        val places = listOf(createTestPlace())
        val weatherBundle = createTestWeatherBundle()
        
        coEvery { mockRepository.savedPlaces() } returns places
        coEvery { mockRepository.weatherFor(any(), any()) } returns weatherBundle
        every { mockRepository.connectivityFlow() } returns flowOf(true)

        // When
        viewModel.retryLoadWeather()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 2) { mockRepository.weatherFor(any(), any()) }
    }

    @Test
    fun `should handle no internet connection error message`() = runTest {
        // Given
        val places = listOf(createTestPlace())
        val exception = WeatherApiException("No internet connection")
        
        coEvery { mockRepository.savedPlaces() } returns places
        coEvery { mockRepository.weatherFor(any(), any()) } throws exception
        every { mockRepository.connectivityFlow() } returns flowOf(true)

        // When
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.weatherState is UiState.Error)
            val errorState = state.weatherState as UiState.Error
            assertEquals("No internet connection. Showing cached data if available.", errorState.message)
        }
    }

    @Test
    fun `should update lastRefreshTime on successful load`() = runTest {
        // Given
        val places = listOf(createTestPlace())
        val weatherBundle = createTestWeatherBundle()
        
        coEvery { mockRepository.savedPlaces() } returns places
        coEvery { mockRepository.weatherFor(any(), any()) } returns weatherBundle
        every { mockRepository.connectivityFlow() } returns flowOf(true)

        // When
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.lastRefreshTime > 0)
        }
    }

    private fun createTestPlace(): Place {
        return Place(
            id = "test-place",
            name = "Test City",
            country = "Test Country",
            latitude = 40.7128,
            longitude = -74.0060
        )
    }

    private fun createTestWeatherBundle(): WeatherBundle {
        return WeatherBundle(
            place = createTestPlace(),
            current = WeatherNow(
                temperature = 20.0,
                feelsLike = 22.0,
                humidity = 65,
                pressure = 1013,
                visibility = 10000,
                uvIndex = 5,
                windSpeed = 10.0,
                windDirection = 180,
                description = "Clear sky",
                icon = "01d"
            ),
            hourly = emptyList(),
            daily = emptyList()
        )
    }
}
