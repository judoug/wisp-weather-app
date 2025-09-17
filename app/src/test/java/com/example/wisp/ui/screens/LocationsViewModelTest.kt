package com.example.wisp.ui.screens

import app.cash.turbine.test
import com.example.wisp.data.weather.repository.WeatherDataRepository
import com.example.wisp.domain.model.Place
import com.example.wisp.ui.state.LocationsUiState
import com.example.wisp.ui.state.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
class LocationsViewModelTest {

    private lateinit var viewModel: LocationsViewModel
    private lateinit var mockRepository: WeatherDataRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        viewModel = LocationsViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load saved places`() = runTest {
        // Given
        val places = listOf(createTestPlace("place1"), createTestPlace("place2"))
        coEvery { mockRepository.savedPlaces() } returns places

        // When
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.placesState is UiState.Success)
            assertEquals(places, (state.placesState as UiState.Success).data)
            assertFalse(state.isRefreshing)
            assertFalse(state.isDeleting)
            assertNull(state.deletingPlaceId)
            assertNull(state.selectedPlaceId)
        }
    }

    @Test
    fun `should handle error when loading places fails`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        coEvery { mockRepository.savedPlaces() } throws exception

        // When
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.placesState is UiState.Error)
            val errorState = state.placesState as UiState.Error
            assertEquals("Failed to load saved locations. Please try again.", errorState.message)
            assertTrue(errorState.retryAction != null)
        }
    }

    @Test
    fun `refreshPlaces should reload places and update state`() = runTest {
        // Given
        val places = listOf(createTestPlace("place1"), createTestPlace("place2"))
        coEvery { mockRepository.savedPlaces() } returns places

        // When
        viewModel.refreshPlaces()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 2) { mockRepository.savedPlaces() }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.placesState is UiState.Success)
            assertEquals(places, (state.placesState as UiState.Success).data)
            assertFalse(state.isRefreshing)
        }
    }

    @Test
    fun `refreshPlaces should handle error and reset refreshing state`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        coEvery { mockRepository.savedPlaces() } throws exception

        // When
        viewModel.refreshPlaces()
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.placesState is UiState.Error)
            assertFalse(state.isRefreshing)
        }
    }

    @Test
    fun `removePlace should delete place and reload list`() = runTest {
        // Given
        val placeId = "place1"
        val remainingPlaces = listOf(createTestPlace("place2"))
        
        coEvery { mockRepository.removePlace(placeId) } returns Unit
        coEvery { mockRepository.savedPlaces() } returns remainingPlaces

        // When
        viewModel.removePlace(placeId)
        advanceUntilIdle()

        // Then
        coVerify { mockRepository.removePlace(placeId) }
        coVerify { mockRepository.savedPlaces() }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.placesState is UiState.Success)
            assertEquals(remainingPlaces, (state.placesState as UiState.Success).data)
            assertFalse(state.isDeleting)
            assertNull(state.deletingPlaceId)
        }
    }

    @Test
    fun `removePlace should handle error and reset deleting state`() = runTest {
        // Given
        val placeId = "place1"
        val exception = RuntimeException("Delete failed")
        
        coEvery { mockRepository.removePlace(placeId) } throws exception

        // When
        viewModel.removePlace(placeId)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.placesState is UiState.Error)
            assertFalse(state.isDeleting)
            assertNull(state.deletingPlaceId)
        }
    }

    @Test
    fun `setPrimaryPlace should set primary and reload list`() = runTest {
        // Given
        val placeId = "place1"
        val places = listOf(createTestPlace("place1"), createTestPlace("place2"))
        
        coEvery { mockRepository.setPrimary(placeId) } returns Unit
        coEvery { mockRepository.savedPlaces() } returns places

        // When
        viewModel.setPrimaryPlace(placeId)
        advanceUntilIdle()

        // Then
        coVerify { mockRepository.setPrimary(placeId) }
        coVerify { mockRepository.savedPlaces() }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.placesState is UiState.Success)
            assertEquals(places, (state.placesState as UiState.Success).data)
        }
    }

    @Test
    fun `setPrimaryPlace should handle error`() = runTest {
        // Given
        val placeId = "place1"
        val exception = RuntimeException("Set primary failed")
        
        coEvery { mockRepository.setPrimary(placeId) } throws exception

        // When
        viewModel.setPrimaryPlace(placeId)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.placesState is UiState.Error)
            val errorState = state.placesState as UiState.Error
            assertEquals("Failed to set primary location. Please try again.", errorState.message)
        }
    }

    @Test
    fun `selectPlace should update selected place ID`() = runTest {
        // Given
        val placeId = "place1"
        val places = listOf(createTestPlace("place1"))
        coEvery { mockRepository.savedPlaces() } returns places

        // When
        viewModel.selectPlace(placeId)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(placeId, state.selectedPlaceId)
        }
    }

    @Test
    fun `clearSelection should clear selected place ID`() = runTest {
        // Given
        val places = listOf(createTestPlace("place1"))
        coEvery { mockRepository.savedPlaces() } returns places

        // When
        viewModel.selectPlace("place1")
        viewModel.clearSelection()
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.selectedPlaceId)
        }
    }

    @Test
    fun `retryLoadPlaces should reload places`() = runTest {
        // Given
        val places = listOf(createTestPlace("place1"))
        coEvery { mockRepository.savedPlaces() } returns places

        // When
        viewModel.retryLoadPlaces()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 2) { mockRepository.savedPlaces() }
    }

    @Test
    fun `isMaxPlacesReached should return true when 10 places`() = runTest {
        // Given
        val places = (1..10).map { createTestPlace("place$it") }
        coEvery { mockRepository.savedPlaces() } returns places

        // When
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.isMaxPlacesReached())
    }

    @Test
    fun `isMaxPlacesReached should return false when less than 10 places`() = runTest {
        // Given
        val places = listOf(createTestPlace("place1"), createTestPlace("place2"))
        coEvery { mockRepository.savedPlaces() } returns places

        // When
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.isMaxPlacesReached())
    }

    @Test
    fun `getPlacesCount should return correct count`() = runTest {
        // Given
        val places = listOf(createTestPlace("place1"), createTestPlace("place2"), createTestPlace("place3"))
        coEvery { mockRepository.savedPlaces() } returns places

        // When
        advanceUntilIdle()

        // Then
        assertEquals(3, viewModel.getPlacesCount())
    }

    @Test
    fun `getPrimaryPlace should return primary place`() = runTest {
        // Given
        val primaryPlace = createTestPlace("primary")
        val places = listOf(primaryPlace, createTestPlace("place2"))
        coEvery { mockRepository.savedPlaces() } returns places

        // When
        advanceUntilIdle()

        // Then
        assertEquals(primaryPlace, viewModel.getPrimaryPlace())
    }

    @Test
    fun `getPrimaryPlace should return null when no primary place`() = runTest {
        // Given
        val places = listOf(createTestPlace("place1"), createTestPlace("place2"))
        coEvery { mockRepository.savedPlaces() } returns places

        // When
        advanceUntilIdle()

        // Then
        assertNull(viewModel.getPrimaryPlace())
    }

    @Test
    fun `should set deleting state during place removal`() = runTest {
        // Given
        val placeId = "place1"
        val places = listOf(createTestPlace("place1"))
        
        coEvery { mockRepository.removePlace(placeId) } returns Unit
        coEvery { mockRepository.savedPlaces() } returns emptyList()

        // When
        viewModel.removePlace(placeId)

        // Then - Check intermediate state
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isDeleting)
            assertEquals(placeId, state.deletingPlaceId)
        }
        
        advanceUntilIdle()
        
        // Then - Check final state
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isDeleting)
            assertNull(state.deletingPlaceId)
        }
    }

    private fun createTestPlace(id: String): Place {
        return Place(
            id = id,
            name = "Test City $id",
            lat = 40.7128,
            lon = -74.0060
        )
    }
}
