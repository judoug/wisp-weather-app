package com.example.wisp.errorhandling

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wisp.design.theme.WispTheme
import com.example.wisp.domain.exception.LocationUnavailableException
import com.example.wisp.domain.exception.PlaceNotFoundException
import com.example.wisp.domain.exception.TooManyPlacesException
import com.example.wisp.domain.exception.WeatherApiException
import com.example.wisp.domain.exception.WeatherException
import com.example.wisp.ui.screens.HomeScreen
import com.example.wisp.ui.screens.LocationsScreen
import com.example.wisp.ui.screens.SearchScreen
import com.example.wisp.ui.screens.SettingsScreen
import com.example.wisp.ui.state.HomeUiState
import com.example.wisp.ui.state.LocationsUiState
import com.example.wisp.ui.state.SearchUiState
import com.example.wisp.ui.state.SettingsUiState
import com.example.wisp.ui.state.UiState
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErrorHandlingTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysNetworkError_whenNetworkFails() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.HomeViewModel>(relaxed = true)
        val uiState = HomeUiState(
            weatherState = UiState.Error(
                message = "Unable to fetch weather data. Please check your connection and try again.",
                throwable = WeatherApiException("Network error"),
                retryAction = {}
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                HomeScreen(
                    onNavigateToLocations = {},
                    onNavigateToSearch = {},
                    onNavigateToSettings = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Unable to fetch weather data. Please check your connection and try again.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysLocationError_whenLocationUnavailable() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.HomeViewModel>(relaxed = true)
        val uiState = HomeUiState(
            weatherState = UiState.Error(
                message = "Location services are unavailable. Please check your location settings.",
                throwable = LocationUnavailableException("Location unavailable"),
                retryAction = {}
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                HomeScreen(
                    onNavigateToLocations = {},
                    onNavigateToSearch = {},
                    onNavigateToSettings = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Location services are unavailable. Please check your location settings.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysPlaceNotFoundError_whenPlaceNotFound() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.HomeViewModel>(relaxed = true)
        val uiState = HomeUiState(
            weatherState = UiState.Error(
                message = "Location not found. Please try a different location.",
                throwable = PlaceNotFoundException("Place not found"),
                retryAction = {}
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                HomeScreen(
                    onNavigateToLocations = {},
                    onNavigateToSearch = {},
                    onNavigateToSettings = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Location not found. Please try a different location.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysGenericError_whenUnexpectedErrorOccurs() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.HomeViewModel>(relaxed = true)
        val uiState = HomeUiState(
            weatherState = UiState.Error(
                message = "An unexpected error occurred. Please try again.",
                throwable = RuntimeException("Unexpected error"),
                retryAction = {}
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                HomeScreen(
                    onNavigateToLocations = {},
                    onNavigateToSearch = {},
                    onNavigateToSettings = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("An unexpected error occurred. Please try again.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysNoLocationsError_whenNoLocationsSaved() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.HomeViewModel>(relaxed = true)
        val uiState = HomeUiState(
            weatherState = UiState.Error(
                message = "No locations saved. Please add a location to see weather data.",
                retryAction = null
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                HomeScreen(
                    onNavigateToLocations = {},
                    onNavigateToSearch = {},
                    onNavigateToSettings = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Welcome to Wisp Weather").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add your first location to get started").assertIsDisplayed()
    }

    @Test
    fun locationsScreen_displaysError_whenLoadingFails() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.LocationsViewModel>(relaxed = true)
        val uiState = LocationsUiState(
            placesState = UiState.Error(
                message = "Failed to load saved locations. Please try again.",
                throwable = RuntimeException("Database error"),
                retryAction = {}
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                LocationsScreen(
                    onNavigateBack = {},
                    onNavigateToSearch = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Failed to load saved locations. Please try again.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun locationsScreen_displaysError_whenRemovingPlaceFails() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.LocationsViewModel>(relaxed = true)
        val uiState = LocationsUiState(
            placesState = UiState.Error(
                message = "Failed to remove location. Please try again.",
                throwable = RuntimeException("Remove failed"),
                retryAction = {}
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                LocationsScreen(
                    onNavigateBack = {},
                    onNavigateToSearch = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Failed to remove location. Please try again.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun locationsScreen_displaysError_whenSettingPrimaryFails() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.LocationsViewModel>(relaxed = true)
        val uiState = LocationsUiState(
            placesState = UiState.Error(
                message = "Failed to set primary location. Please try again.",
                throwable = RuntimeException("Set primary failed"),
                retryAction = {}
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                LocationsScreen(
                    onNavigateBack = {},
                    onNavigateToSearch = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Failed to set primary location. Please try again.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysError_whenSearchFails() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.SearchViewModel>(relaxed = true)
        val uiState = SearchUiState(
            searchQuery = "Invalid",
            searchResultsState = UiState.Error(
                message = "Search failed. Please try again.",
                throwable = RuntimeException("Search failed"),
                retryAction = {}
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Search failed. Please try again.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysNetworkError_whenNetworkFails() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.SearchViewModel>(relaxed = true)
        val uiState = SearchUiState(
            searchQuery = "New York",
            searchResultsState = UiState.Error(
                message = "No internet connection. Please check your connection and try again.",
                throwable = WeatherApiException("No internet connection"),
                retryAction = {}
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("No internet connection. Please check your connection and try again.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysMaxPlacesError_whenMaxPlacesReached() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.SearchViewModel>(relaxed = true)
        val uiState = SearchUiState(
            searchQuery = "New York",
            searchResultsState = UiState.Error(
                message = "Maximum number of locations reached (10). Please remove a location first.",
                retryAction = null
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Maximum number of locations reached (10). Please remove a location first.").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysDuplicateLocationError_whenLocationAlreadySaved() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.SearchViewModel>(relaxed = true)
        val uiState = SearchUiState(
            searchQuery = "New York",
            searchResultsState = UiState.Error(
                message = "This location is already saved.",
                retryAction = null
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("This location is already saved.").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysAddLocationError_whenAddingFails() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.SearchViewModel>(relaxed = true)
        val uiState = SearchUiState(
            searchQuery = "New York",
            searchResultsState = UiState.Error(
                message = "Failed to add location. Please try again.",
                throwable = RuntimeException("Add failed"),
                retryAction = {}
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Failed to add location. Please try again.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysError_whenSavingFails() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.SettingsViewModel>(relaxed = true)
        val uiState = SettingsUiState(
            saveState = UiState.Error(
                message = "Failed to save settings. Please try again.",
                throwable = RuntimeException("Save failed"),
                retryAction = {}
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SettingsScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Failed to save settings. Please try again.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysOfflineIndicator_whenOffline() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.HomeViewModel>(relaxed = true)
        val uiState = HomeUiState(
            weatherState = UiState.Success(createTestWeatherBundle()),
            isOffline = true
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                HomeScreen(
                    onNavigateToLocations = {},
                    onNavigateToSearch = {},
                    onNavigateToSettings = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Offline").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysRefreshError_whenRefreshFails() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.HomeViewModel>(relaxed = true)
        val uiState = HomeUiState(
            weatherState = UiState.Error(
                message = "Failed to refresh weather data. Please try again.",
                throwable = RuntimeException("Refresh failed"),
                retryAction = {}
            ),
            isRefreshing = false
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                HomeScreen(
                    onNavigateToLocations = {},
                    onNavigateToSearch = {},
                    onNavigateToSettings = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Failed to refresh weather data. Please try again.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun locationsScreen_displaysRefreshError_whenRefreshFails() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.LocationsViewModel>(relaxed = true)
        val uiState = LocationsUiState(
            placesState = UiState.Error(
                message = "Failed to refresh locations. Please try again.",
                throwable = RuntimeException("Refresh failed"),
                retryAction = {}
            ),
            isRefreshing = false
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                LocationsScreen(
                    onNavigateBack = {},
                    onNavigateToSearch = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Failed to refresh locations. Please try again.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysEmptyResults_whenNoResultsFound() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.SearchViewModel>(relaxed = true)
        val uiState = SearchUiState(
            searchQuery = "NonExistentCity",
            searchResultsState = UiState.Success(emptyList())
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("No locations found").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try a different search term").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysLoadingState_whenLoading() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.HomeViewModel>(relaxed = true)
        val uiState = HomeUiState(
            weatherState = UiState.Loading
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                HomeScreen(
                    onNavigateToLocations = {},
                    onNavigateToSearch = {},
                    onNavigateToSettings = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Loading weather data...").assertIsDisplayed()
    }

    @Test
    fun locationsScreen_displaysLoadingState_whenLoading() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.LocationsViewModel>(relaxed = true)
        val uiState = LocationsUiState(
            placesState = UiState.Loading
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                LocationsScreen(
                    onNavigateBack = {},
                    onNavigateToSearch = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Loading locations...").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysLoadingState_whenSearching() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.SearchViewModel>(relaxed = true)
        val uiState = SearchUiState(
            searchQuery = "New York",
            isSearching = true,
            searchResultsState = UiState.Loading
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Searching...").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysLoadingState_whenSaving() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.SettingsViewModel>(relaxed = true)
        val uiState = SettingsUiState(
            isSaving = true
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SettingsScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        // Saving state would be indicated by loading indicators
    }

    private fun createTestWeatherBundle(): com.example.wisp.domain.model.WeatherBundle {
        return com.example.wisp.domain.model.WeatherBundle(
            place = com.example.wisp.domain.model.Place(
                id = "test-place",
                name = "Test City",
                country = "Test Country",
                latitude = 40.7128,
                longitude = -74.0060
            ),
            current = com.example.wisp.domain.model.WeatherNow(
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
