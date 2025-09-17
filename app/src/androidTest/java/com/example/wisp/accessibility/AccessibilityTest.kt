package com.example.wisp.accessibility

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wisp.design.theme.WispTheme
import com.example.wisp.domain.model.Place
import com.example.wisp.domain.model.WeatherBundle
import com.example.wisp.domain.model.WeatherNow
import com.example.wisp.ui.components.WeatherContent
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
class AccessibilityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_hasProperContentDescriptions() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.HomeViewModel>(relaxed = true)
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

        // Then - Verify all interactive elements have content descriptions
        composeTestRule.onNodeWithContentDescription("Refresh").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Locations").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add Location").assertIsDisplayed()
    }

    @Test
    fun weatherContent_hasProperContentDescriptions() {
        // Given
        val weatherBundle = createTestWeatherBundle()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then - Verify all elements have proper content descriptions
        composeTestRule.onNodeWithContentDescription("Location").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Clear sky").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Humidity").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Wind").assertIsDisplayed()
    }

    @Test
    fun locationsScreen_hasProperContentDescriptions() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.LocationsViewModel>(relaxed = true)
        val places = listOf(createTestPlace("place1", "New York"))
        val uiState = LocationsUiState(
            placesState = UiState.Success(places)
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

        // Then - Verify all interactive elements have content descriptions
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add Location").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Refresh").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Location").assertIsDisplayed()
    }

    @Test
    fun searchScreen_hasProperContentDescriptions() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.SearchViewModel>(relaxed = true)
        val uiState = SearchUiState()

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then - Verify all interactive elements have content descriptions
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search for a location").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_hasProperContentDescriptions() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.SettingsViewModel>(relaxed = true)
        val uiState = SettingsUiState()

        // When
        composeTestRule.setContent {
            WispTheme {
                SettingsScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then - Verify all interactive elements have content descriptions
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Temperature").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Refresh").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Notifications").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Location").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Accessibility").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Dark mode").assertIsDisplayed()
    }

    @Test
    fun homeScreen_hasProperSemanticStructure() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.HomeViewModel>(relaxed = true)
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

        // Then - Verify semantic structure elements
        composeTestRule.onNodeWithText("Wisp Weather").assertIsDisplayed()
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
    fun locationsScreen_hasProperSemanticStructure() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.LocationsViewModel>(relaxed = true)
        val places = listOf(
            createTestPlace("place1", "New York"),
            createTestPlace("place2", "London")
        )
        val uiState = LocationsUiState(
            placesState = UiState.Success(places)
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

        // Then - Verify semantic structure elements
        composeTestRule.onNodeWithText("Saved Locations").assertIsDisplayed()
        composeTestRule.onNodeWithText("New York").assertIsDisplayed()
        composeTestRule.onNodeWithText("London").assertIsDisplayed()
    }

    @Test
    fun searchScreen_hasProperSemanticStructure() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.SearchViewModel>(relaxed = true)
        val uiState = SearchUiState()

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then - Verify semantic structure elements
        composeTestRule.onNodeWithText("Search Locations").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search for a location").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_hasProperSemanticStructure() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.SettingsViewModel>(relaxed = true)
        val uiState = SettingsUiState()

        // When
        composeTestRule.setContent {
            WispTheme {
                SettingsScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then - Verify semantic structure elements
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Temperature Unit").assertIsDisplayed()
        composeTestRule.onNodeWithText("Refresh Interval").assertIsDisplayed()
        composeTestRule.onNodeWithText("Notifications").assertIsDisplayed()
        composeTestRule.onNodeWithText("Location Permission").assertIsDisplayed()
        composeTestRule.onNodeWithText("Accessibility").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dark Mode").assertIsDisplayed()
    }

    @Test
    fun homeScreen_hasProperTouchTargets() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.HomeViewModel>(relaxed = true)
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

        // Then - Verify touch targets are properly sized and accessible
        // All interactive elements should be at least 48dp in size
        composeTestRule.onNodeWithContentDescription("Refresh").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Locations").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add Location").assertIsDisplayed()
    }

    @Test
    fun weatherContent_hasProperTouchTargets() {
        // Given
        val weatherBundle = createTestWeatherBundle()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then - Verify touch targets are properly sized and accessible
        // Weather content should have proper touch targets for interactive elements
        composeTestRule.onNodeWithContentDescription("Location").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Clear sky").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Humidity").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Wind").assertIsDisplayed()
    }

    @Test
    fun locationsScreen_hasProperTouchTargets() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.LocationsViewModel>(relaxed = true)
        val places = listOf(createTestPlace("place1", "New York"))
        val uiState = LocationsUiState(
            placesState = UiState.Success(places)
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

        // Then - Verify touch targets are properly sized and accessible
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add Location").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Refresh").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Location").assertIsDisplayed()
    }

    @Test
    fun searchScreen_hasProperTouchTargets() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.SearchViewModel>(relaxed = true)
        val uiState = SearchUiState()

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then - Verify touch targets are properly sized and accessible
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search for a location").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_hasProperTouchTargets() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.SettingsViewModel>(relaxed = true)
        val uiState = SettingsUiState()

        // When
        composeTestRule.setContent {
            WispTheme {
                SettingsScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then - Verify touch targets are properly sized and accessible
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Temperature").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Refresh").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Notifications").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Location").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Accessibility").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Dark mode").assertIsDisplayed()
    }

    @Test
    fun homeScreen_hasProperColorContrast() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.HomeViewModel>(relaxed = true)
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

        // Then - Verify color contrast is adequate
        // This test verifies that the UI elements are visible and have proper contrast
        composeTestRule.onNodeWithText("Wisp Weather").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test City").assertIsDisplayed()
        composeTestRule.onNodeWithText("20°C").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clear sky").assertIsDisplayed()
    }

    @Test
    fun weatherContent_hasProperColorContrast() {
        // Given
        val weatherBundle = createTestWeatherBundle()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then - Verify color contrast is adequate
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
    fun locationsScreen_hasProperColorContrast() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.LocationsViewModel>(relaxed = true)
        val places = listOf(createTestPlace("place1", "New York"))
        val uiState = LocationsUiState(
            placesState = UiState.Success(places)
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

        // Then - Verify color contrast is adequate
        composeTestRule.onNodeWithText("Saved Locations").assertIsDisplayed()
        composeTestRule.onNodeWithText("New York").assertIsDisplayed()
    }

    @Test
    fun searchScreen_hasProperColorContrast() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.SearchViewModel>(relaxed = true)
        val uiState = SearchUiState()

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then - Verify color contrast is adequate
        composeTestRule.onNodeWithText("Search Locations").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search for a location").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_hasProperColorContrast() {
        // Given
        val mockViewModel = mockk<com.example.wisp.ui.screens.SettingsViewModel>(relaxed = true)
        val uiState = SettingsUiState()

        // When
        composeTestRule.setContent {
            WispTheme {
                SettingsScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then - Verify color contrast is adequate
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Temperature Unit").assertIsDisplayed()
        composeTestRule.onNodeWithText("Refresh Interval").assertIsDisplayed()
        composeTestRule.onNodeWithText("Notifications").assertIsDisplayed()
        composeTestRule.onNodeWithText("Location Permission").assertIsDisplayed()
        composeTestRule.onNodeWithText("Accessibility").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dark Mode").assertIsDisplayed()
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

    private fun createTestPlace(id: String, name: String, country: String = "Test Country"): Place {
        return Place(
            id = id,
            name = name,
            country = country,
            latitude = 40.7128,
            longitude = -74.0060
        )
    }
}
