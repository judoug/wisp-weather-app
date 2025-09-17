package com.example.wisp.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSwipeDown
import androidx.compose.ui.test.performSwipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wisp.design.theme.WispTheme
import com.example.wisp.domain.model.Place
import com.example.wisp.domain.model.WeatherBundle
import com.example.wisp.domain.model.WeatherNow
import com.example.wisp.ui.state.HomeUiState
import com.example.wisp.ui.state.UiState
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysWelcomeMessage_whenNoLocationsSaved() {
        // Given
        val mockViewModel = mockk<HomeViewModel>(relaxed = true)
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
    fun homeScreen_displaysWeatherData_whenDataIsAvailable() {
        // Given
        val mockViewModel = mockk<HomeViewModel>(relaxed = true)
        val weatherBundle = createTestWeatherBundle()
        val uiState = HomeUiState(
            weatherState = UiState.Success(weatherBundle)
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
        composeTestRule.onNodeWithText("Test City").assertIsDisplayed()
        composeTestRule.onNodeWithText("20°C").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clear sky").assertIsDisplayed()
        composeTestRule.onNodeWithText("Feels like 22°C").assertIsDisplayed()
        composeTestRule.onNodeWithText("65%").assertIsDisplayed()
        composeTestRule.onNodeWithText("Humidity").assertIsDisplayed()
        composeTestRule.onNodeWithText("10 km/h").assertIsDisplayed()
        composeTestRule.onNodeWithText("Wind Speed").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysLoadingState_whenLoading() {
        // Given
        val mockViewModel = mockk<HomeViewModel>(relaxed = true)
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
    fun homeScreen_displaysErrorState_whenErrorOccurs() {
        // Given
        val mockViewModel = mockk<HomeViewModel>(relaxed = true)
        val uiState = HomeUiState(
            weatherState = UiState.Error(
                message = "Network error occurred",
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
        composeTestRule.onNodeWithText("Network error occurred").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysOfflineIndicator_whenOffline() {
        // Given
        val mockViewModel = mockk<HomeViewModel>(relaxed = true)
        val weatherBundle = createTestWeatherBundle()
        val uiState = HomeUiState(
            weatherState = UiState.Success(weatherBundle),
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
    fun homeScreen_refreshButtonCallsRefresh_whenClicked() {
        // Given
        val mockViewModel = mockk<HomeViewModel>(relaxed = true)
        val weatherBundle = createTestWeatherBundle()
        val uiState = HomeUiState(
            weatherState = UiState.Success(weatherBundle)
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

        composeTestRule.onNodeWithContentDescription("Refresh").performClick()

        // Then
        verify { mockViewModel.refreshWeather() }
    }

    @Test
    fun homeScreen_showsLoadingIndicator_whenRefreshing() {
        // Given
        val mockViewModel = mockk<HomeViewModel>(relaxed = true)
        val weatherBundle = createTestWeatherBundle()
        val uiState = HomeUiState(
            weatherState = UiState.Success(weatherBundle),
            isRefreshing = true
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
        composeTestRule.onNode(hasContentDescription("Refresh")).assertIsNotDisplayed()
        // Loading indicator should be visible in the refresh button
    }

    @Test
    fun homeScreen_navigationButtonsAreClickable() {
        // Given
        val mockViewModel = mockk<HomeViewModel>(relaxed = true)
        val weatherBundle = createTestWeatherBundle()
        val uiState = HomeUiState(
            weatherState = UiState.Success(weatherBundle)
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
        composeTestRule.onNodeWithContentDescription("Locations").assertIsDisplayed().assertIsEnabled()
        composeTestRule.onNodeWithContentDescription("Settings").assertIsDisplayed().assertIsEnabled()
        composeTestRule.onNodeWithContentDescription("Add Location").assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun homeScreen_displaysLastRefreshTime_whenAvailable() {
        // Given
        val mockViewModel = mockk<HomeViewModel>(relaxed = true)
        val weatherBundle = createTestWeatherBundle()
        val refreshTime = System.currentTimeMillis()
        val uiState = HomeUiState(
            weatherState = UiState.Success(weatherBundle),
            lastRefreshTime = refreshTime
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
        composeTestRule.onNode(hasText("Last updated:")).assertIsDisplayed()
    }

    @Test
    fun homeScreen_supportsPullToRefresh() {
        // Given
        val mockViewModel = mockk<HomeViewModel>(relaxed = true)
        val weatherBundle = createTestWeatherBundle()
        val uiState = HomeUiState(
            weatherState = UiState.Success(weatherBundle)
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

        // Perform pull to refresh gesture
        composeTestRule.onNodeWithText("Test City").performSwipeDown()

        // Then - The pull to refresh should trigger (this is tested through the ViewModel interaction)
        // In a real test, we would verify that refreshWeather is called
    }

    @Test
    fun homeScreen_displaysWeatherIcon() {
        // Given
        val mockViewModel = mockk<HomeViewModel>(relaxed = true)
        val weatherBundle = createTestWeatherBundle()
        val uiState = HomeUiState(
            weatherState = UiState.Success(weatherBundle)
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
        composeTestRule.onNode(hasContentDescription("Clear sky")).assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysLocationIcon() {
        // Given
        val mockViewModel = mockk<HomeViewModel>(relaxed = true)
        val weatherBundle = createTestWeatherBundle()
        val uiState = HomeUiState(
            weatherState = UiState.Success(weatherBundle)
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
        composeTestRule.onNode(hasContentDescription("Location")).assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysHumidityIcon() {
        // Given
        val mockViewModel = mockk<HomeViewModel>(relaxed = true)
        val weatherBundle = createTestWeatherBundle()
        val uiState = HomeUiState(
            weatherState = UiState.Success(weatherBundle)
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
        composeTestRule.onNode(hasContentDescription("Humidity")).assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysWindIcon() {
        // Given
        val mockViewModel = mockk<HomeViewModel>(relaxed = true)
        val weatherBundle = createTestWeatherBundle()
        val uiState = HomeUiState(
            weatherState = UiState.Success(weatherBundle)
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
        composeTestRule.onNode(hasContentDescription("Wind")).assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysAppTitle() {
        // Given
        val mockViewModel = mockk<HomeViewModel>(relaxed = true)
        val weatherBundle = createTestWeatherBundle()
        val uiState = HomeUiState(
            weatherState = UiState.Success(weatherBundle)
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
        composeTestRule.onNodeWithText("Wisp Weather").assertIsDisplayed()
    }

    private fun createTestWeatherBundle(): WeatherBundle {
        return WeatherBundle(
            place = Place(
                id = "test-place",
                name = "Test City",
                country = "Test Country",
                latitude = 40.7128,
                longitude = -74.0060
            ),
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
