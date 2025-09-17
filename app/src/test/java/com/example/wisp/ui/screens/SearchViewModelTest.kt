package com.example.wisp.ui.screens

import app.cash.turbine.test
import com.example.wisp.data.weather.repository.WeatherDataRepository
import com.example.wisp.data.weather.service.OpenWeatherProvider
import com.example.wisp.domain.exception.TooManyPlacesException
import com.example.wisp.domain.exception.WeatherApiException
import com.example.wisp.domain.exception.WeatherException
import com.example.wisp.domain.model.Place
import com.example.wisp.ui.state.SearchUiState
import com.example.wisp.ui.state.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel
    private lateinit var mockWeatherProvider: OpenWeatherProvider
    private lateinit var mockRepository: WeatherDataRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockWeatherProvider = mockk()
        mockRepository = mockk()
        viewModel = SearchViewModel(mockWeatherProvider, mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be empty`() = runTest {
        // When
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.searchQuery)
            assertTrue(state.searchResultsState is UiState.Success)
            assertEquals(emptyList<Place>(), (state.searchResultsState as UiState.Success).data)
            assertFalse(state.isSearching)
            assertFalse(state.isAddingPlaceToSaved)
            assertNull(state.addingPlaceId)
            assertTrue(state.searchHistory.isEmpty())
        }
    }

    @Test
    fun `updateSearchQuery should update query and trigger search after debounce`() = runTest {
        // Given
        val query = "New York"
        val searchResults = listOf(createTestPlace("nyc"))
        
        coEvery { mockWeatherProvider.searchPlaces(query, 8) } returns searchResults

        // When
        viewModel.updateSearchQuery(query)
        advanceTimeBy(500) // Wait for debounce
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(query, state.searchQuery)
            assertTrue(state.searchResultsState is UiState.Success)
            assertEquals(searchResults, (state.searchResultsState as UiState.Success).data)
            assertFalse(state.isSearching)
        }
        
        coVerify { mockWeatherProvider.searchPlaces(query, 8) }
    }

    @Test
    fun `updateSearchQuery should cancel previous search`() = runTest {
        // Given
        val query1 = "New York"
        val query2 = "London"
        val results1 = listOf(createTestPlace("nyc"))
        val results2 = listOf(createTestPlace("london"))
        
        coEvery { mockWeatherProvider.searchPlaces(query1, 8) } returns results1
        coEvery { mockWeatherProvider.searchPlaces(query2, 8) } returns results2

        // When
        viewModel.updateSearchQuery(query1)
        viewModel.updateSearchQuery(query2) // This should cancel the first search
        advanceTimeBy(500)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(query2, state.searchQuery)
            assertTrue(state.searchResultsState is UiState.Success)
            assertEquals(results2, (state.searchResultsState as UiState.Success).data)
        }
    }

    @Test
    fun `updateSearchQuery with empty string should clear results`() = runTest {
        // Given
        val query = "New York"
        val searchResults = listOf(createTestPlace("nyc"))
        
        coEvery { mockWeatherProvider.searchPlaces(query, 8) } returns searchResults

        // When
        viewModel.updateSearchQuery(query)
        advanceTimeBy(500)
        advanceUntilIdle()
        
        viewModel.updateSearchQuery("")
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.searchQuery)
            assertTrue(state.searchResultsState is UiState.Success)
            assertEquals(emptyList<Place>(), (state.searchResultsState as UiState.Success).data)
        }
    }

    @Test
    fun `searchPlaces should handle API exception`() = runTest {
        // Given
        val query = "Invalid"
        val exception = WeatherApiException("API Error")
        
        coEvery { mockWeatherProvider.searchPlaces(query, 8) } throws exception

        // When
        viewModel.updateSearchQuery(query)
        advanceTimeBy(500)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.searchResultsState is UiState.Error)
            val errorState = state.searchResultsState as UiState.Error
            assertEquals("Unable to search for locations. Please check your connection and try again.", errorState.message)
            assertFalse(state.isSearching)
        }
    }

    @Test
    fun `searchPlaces should handle no internet connection error`() = runTest {
        // Given
        val query = "New York"
        val exception = WeatherApiException("No internet connection")
        
        coEvery { mockWeatherProvider.searchPlaces(query, 8) } throws exception

        // When
        viewModel.updateSearchQuery(query)
        advanceTimeBy(500)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.searchResultsState is UiState.Error)
            val errorState = state.searchResultsState as UiState.Error
            assertEquals("No internet connection. Please check your connection and try again.", errorState.message)
        }
    }

    @Test
    fun `searchPlaces should handle generic exception`() = runTest {
        // Given
        val query = "New York"
        val exception = RuntimeException("Generic error")
        
        coEvery { mockWeatherProvider.searchPlaces(query, 8) } throws exception

        // When
        viewModel.updateSearchQuery(query)
        advanceTimeBy(500)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.searchResultsState is UiState.Error)
            val errorState = state.searchResultsState as UiState.Error
            assertEquals("Search failed. Please try again.", errorState.message)
        }
    }

    @Test
    fun `addPlace should add place and clear search`() = runTest {
        // Given
        val place = createTestPlace("nyc")
        val currentPlaces = listOf(createTestPlace("existing"))
        
        coEvery { mockRepository.savedPlaces() } returns currentPlaces
        coEvery { mockRepository.addPlace(place) } returns Unit

        // When
        viewModel.addPlace(place)
        advanceUntilIdle()

        // Then
        coVerify { mockRepository.addPlace(place) }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.searchResultsState is UiState.Success)
            assertEquals(emptyList<Place>(), (state.searchResultsState as UiState.Success).data)
            assertEquals("", state.searchQuery)
            assertFalse(state.isAddingPlaceToSaved)
            assertNull(state.addingPlaceId)
        }
    }

    @Test
    fun `addPlace should handle maximum places reached`() = runTest {
        // Given
        val place = createTestPlace("nyc")
        val currentPlaces = (1..10).map { createTestPlace("place$it") }
        
        coEvery { mockRepository.savedPlaces() } returns currentPlaces

        // When
        viewModel.addPlace(place)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.searchResultsState is UiState.Error)
            val errorState = state.searchResultsState as UiState.Error
            assertEquals("Maximum number of locations reached (10). Please remove a location first.", errorState.message)
            assertFalse(state.isAddingPlaceToSaved)
            assertNull(state.addingPlaceId)
        }
    }

    @Test
    fun `addPlace should handle duplicate place`() = runTest {
        // Given
        val place = createTestPlace("nyc")
        val currentPlaces = listOf(place)
        
        coEvery { mockRepository.savedPlaces() } returns currentPlaces

        // When
        viewModel.addPlace(place)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.searchResultsState is UiState.Error)
            val errorState = state.searchResultsState as UiState.Error
            assertEquals("This location is already saved.", errorState.message)
        }
    }

    @Test
    fun `addPlace should handle TooManyPlacesException`() = runTest {
        // Given
        val place = createTestPlace("nyc")
        val currentPlaces = listOf(createTestPlace("existing"))
        
        coEvery { mockRepository.savedPlaces() } returns currentPlaces
        coEvery { mockRepository.addPlace(place) } throws TooManyPlacesException("Too many places")

        // When
        viewModel.addPlace(place)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.searchResultsState is UiState.Error)
            val errorState = state.searchResultsState as UiState.Error
            assertEquals("Maximum number of locations reached (10). Please remove a location first.", errorState.message)
        }
    }

    @Test
    fun `addPlace should handle generic exception`() = runTest {
        // Given
        val place = createTestPlace("nyc")
        val currentPlaces = listOf(createTestPlace("existing"))
        val exception = RuntimeException("Add failed")
        
        coEvery { mockRepository.savedPlaces() } returns currentPlaces
        coEvery { mockRepository.addPlace(place) } throws exception

        // When
        viewModel.addPlace(place)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.searchResultsState is UiState.Error)
            val errorState = state.searchResultsState as UiState.Error
            assertEquals("Failed to add location. Please try again.", errorState.message)
        }
    }

    @Test
    fun `clearSearch should clear query and results`() = runTest {
        // Given
        val query = "New York"
        val searchResults = listOf(createTestPlace("nyc"))
        
        coEvery { mockWeatherProvider.searchPlaces(query, 8) } returns searchResults

        // When
        viewModel.updateSearchQuery(query)
        advanceTimeBy(500)
        advanceUntilIdle()
        
        viewModel.clearSearch()
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.searchQuery)
            assertTrue(state.searchResultsState is UiState.Success)
            assertEquals(emptyList<Place>(), (state.searchResultsState as UiState.Success).data)
            assertFalse(state.isSearching)
        }
    }

    @Test
    fun `retrySearch should retry last search`() = runTest {
        // Given
        val query = "New York"
        val searchResults = listOf(createTestPlace("nyc"))
        
        coEvery { mockWeatherProvider.searchPlaces(query, 8) } returns searchResults

        // When
        viewModel.updateSearchQuery(query)
        advanceTimeBy(500)
        advanceUntilIdle()
        
        viewModel.retrySearch()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 2) { mockWeatherProvider.searchPlaces(query, 8) }
    }

    @Test
    fun `useSearchHistory should update search query`() = runTest {
        // Given
        val historyTerm = "London"
        val searchResults = listOf(createTestPlace("london"))
        
        coEvery { mockWeatherProvider.searchPlaces(historyTerm, 8) } returns searchResults

        // When
        viewModel.useSearchHistory(historyTerm)
        advanceTimeBy(500)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(historyTerm, state.searchQuery)
        }
    }

    @Test
    fun `clearSearchHistory should clear history`() = runTest {
        // Given
        val place = createTestPlace("nyc")
        val currentPlaces = listOf(createTestPlace("existing"))
        
        coEvery { mockRepository.savedPlaces() } returns currentPlaces
        coEvery { mockRepository.addPlace(place) } returns Unit

        // When
        viewModel.addPlace(place) // This adds to history
        viewModel.clearSearchHistory()
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.searchHistory.isEmpty())
        }
    }

    @Test
    fun `should add to search history when place is added`() = runTest {
        // Given
        val place = createTestPlace("nyc")
        val currentPlaces = listOf(createTestPlace("existing"))
        
        coEvery { mockRepository.savedPlaces() } returns currentPlaces
        coEvery { mockRepository.addPlace(place) } returns Unit

        // When
        viewModel.addPlace(place)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.searchHistory.contains(place.name))
        }
    }

    @Test
    fun `should limit search history to 10 items`() = runTest {
        // Given
        val currentPlaces = listOf(createTestPlace("existing"))
        
        coEvery { mockRepository.savedPlaces() } returns currentPlaces
        coEvery { mockRepository.addPlace(any()) } returns Unit

        // When - Add 12 places
        repeat(12) { index ->
            val place = createTestPlace("place$index")
            viewModel.addPlace(place)
        }
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.searchHistory.size <= 10)
        }
    }

    @Test
    fun `should set adding state during place addition`() = runTest {
        // Given
        val place = createTestPlace("nyc")
        val currentPlaces = listOf(createTestPlace("existing"))
        
        coEvery { mockRepository.savedPlaces() } returns currentPlaces
        coEvery { mockRepository.addPlace(place) } returns Unit

        // When
        viewModel.addPlace(place)

        // Then - Check intermediate state
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isAddingPlaceToSaved)
            assertEquals(place.id, state.addingPlaceId)
        }
        
        advanceUntilIdle()
        
        // Then - Check final state
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isAddingPlaceToSaved)
            assertNull(state.addingPlaceId)
        }
    }

    private fun createTestPlace(id: String): Place {
        return Place(
            id = id,
            name = "Test City $id",
            country = "Test Country",
            latitude = 40.7128,
            longitude = -74.0060
        )
    }
}
