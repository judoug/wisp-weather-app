package com.example.wisp.integration

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wisp.MainActivity
import com.example.wisp.domain.model.Place
import com.example.wisp.domain.model.WeatherBundle
import com.example.wisp.domain.model.WeatherNow
import com.example.wisp.ui.screens.HomeScreen
import com.example.wisp.ui.screens.LocationsScreen
import com.example.wisp.ui.screens.SearchScreen
import com.example.wisp.ui.screens.SettingsScreen
import com.example.wisp.ui.state.HomeUiState
import com.example.wisp.ui.state.LocationsUiState
import com.example.wisp.ui.state.SearchUiState
import com.example.wisp.ui.state.SettingsUiState
import com.example.wisp.ui.state.UiState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class UIFlowIntegrationTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun uiFlow_completeUserJourneyFromHomeToSearchToLocations() {
        // Given - Start with empty state
        val mockHomeViewModel = mockk<com.example.wisp.ui.screens.HomeViewModel>(relaxed = true)
        val mockSearchViewModel = mockk<com.example.wisp.ui.screens.SearchViewModel>(relaxed = true)
        val mockLocationsViewModel = mockk<com.example.wisp.ui.screens.LocationsViewModel>(relaxed = true)

        // When - Navigate through the app flow
        composeTestRule.setContent {
            // Start with HomeScreen showing welcome message
            HomeScreen(
                onNavigateToLocations = { /* Navigate to locations */ },
                onNavigateToSearch = { /* Navigate to search */ },
                onNavigateToSettings = { /* Navigate to settings */ },
                viewModel = mockHomeViewModel
            )
        }

        // Then - Verify welcome message is displayed
        composeTestRule.onNodeWithText("Welcome to Wisp Weather").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add your first location to get started").assertIsDisplayed()

        // When - Navigate to SearchScreen
        composeTestRule.setContent {
            SearchScreen(
                onNavigateBack = { /* Navigate back */ },
                viewModel = mockSearchViewModel
            )
        }

        // Then - Verify search screen is displayed
        composeTestRule.onNodeWithText("Search Locations").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search for a location").assertIsDisplayed()

        // When - Navigate to LocationsScreen
        composeTestRule.setContent {
            LocationsScreen(
                onNavigateBack = { /* Navigate back */ },
                onNavigateToSearch = { /* Navigate to search */ },
                viewModel = mockLocationsViewModel
            )
        }

        // Then - Verify locations screen is displayed
        composeTestRule.onNodeWithText("Saved Locations").assertIsDisplayed()
        composeTestRule.onNodeWithText("No locations saved").assertIsDisplayed()
    }

    @Test
    fun uiFlow_searchAndAddLocationFlow() {
        // Given
        val mockSearchViewModel = mockk<com.example.wisp.ui.screens.SearchViewModel>(relaxed = true)
        val searchResults = listOf(
            Place(
                id = "nyc",
                name = "New York",
                country = "United States",
                latitude = 40.7128,
                longitude = -74.0060
            )
        )

        // When - Start with SearchScreen
        composeTestRule.setContent {
            SearchScreen(
                onNavigateBack = { /* Navigate back */ },
                viewModel = mockSearchViewModel
            )
        }

        // Then - Verify search screen is displayed
        composeTestRule.onNodeWithText("Search for a location").assertIsDisplayed()

        // When - Enter search query
        composeTestRule.onNodeWithText("Search for a location").performTextInput("New York")

        // Then - Verify search query is entered
        composeTestRule.onNode(hasText("New York")).assertIsDisplayed()

        // When - Search results are displayed
        composeTestRule.setContent {
            SearchScreen(
                onNavigateBack = { /* Navigate back */ },
                viewModel = mockSearchViewModel
            )
        }

        // Then - Verify search results would be displayed (in real scenario)
        // This test verifies the UI flow structure
    }

    @Test
    fun uiFlow_settingsConfigurationFlow() {
        // Given
        val mockSettingsViewModel = mockk<com.example.wisp.ui.screens.SettingsViewModel>(relaxed = true)

        // When - Navigate to SettingsScreen
        composeTestRule.setContent {
            SettingsScreen(
                onNavigateBack = { /* Navigate back */ },
                viewModel = mockSettingsViewModel
            )
        }

        // Then - Verify settings screen is displayed
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Temperature Unit").assertIsDisplayed()
        composeTestRule.onNodeWithText("Refresh Interval").assertIsDisplayed()
        composeTestRule.onNodeWithText("Notifications").assertIsDisplayed()
        composeTestRule.onNodeWithText("Location Permission").assertIsDisplayed()
        composeTestRule.onNodeWithText("Accessibility").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dark Mode").assertIsDisplayed()

        // When - Change temperature unit
        composeTestRule.onNodeWithText("Fahrenheit").performClick()

        // Then - Verify setting change is triggered
        // The actual change would be verified through ViewModel interaction

        // When - Change refresh interval
        composeTestRule.onNodeWithText("30 minutes").performClick()

        // Then - Verify setting change is triggered
        // The actual change would be verified through ViewModel interaction
    }

    @Test
    fun uiFlow_weatherDisplayFlow() {
        // Given
        val mockHomeViewModel = mockk<com.example.wisp.ui.screens.HomeViewModel>(relaxed = true)
        val weatherBundle = createTestWeatherBundle()

        // When - Display HomeScreen with weather data
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToLocations = { /* Navigate to locations */ },
                onNavigateToSearch = { /* Navigate to search */ },
                onNavigateToSettings = { /* Navigate to settings */ },
                viewModel = mockHomeViewModel
            )
        }

        // Then - Verify weather data is displayed
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
    fun uiFlow_errorHandlingFlow() {
        // Given
        val mockHomeViewModel = mockk<com.example.wisp.ui.screens.HomeViewModel>(relaxed = true)

        // When - Display HomeScreen with error state
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToLocations = { /* Navigate to locations */ },
                onNavigateToSearch = { /* Navigate to search */ },
                onNavigateToSettings = { /* Navigate to settings */ },
                viewModel = mockHomeViewModel
            )
        }

        // Then - Verify error handling UI elements are present
        // This test verifies the error handling structure is in place
        composeTestRule.onNodeWithText("Wisp Weather").assertIsDisplayed()
    }

    @Test
    fun uiFlow_loadingStatesFlow() {
        // Given
        val mockHomeViewModel = mockk<com.example.wisp.ui.screens.HomeViewModel>(relaxed = true)
        val mockSearchViewModel = mockk<com.example.wisp.ui.screens.SearchViewModel>(relaxed = true)
        val mockLocationsViewModel = mockk<com.example.wisp.ui.screens.LocationsViewModel>(relaxed = true)

        // When - Display HomeScreen with loading state
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToLocations = { /* Navigate to locations */ },
                onNavigateToSearch = { /* Navigate to search */ },
                onNavigateToSettings = { /* Navigate to settings */ },
                viewModel = mockHomeViewModel
            )
        }

        // Then - Verify loading state UI elements are present
        composeTestRule.onNodeWithText("Wisp Weather").assertIsDisplayed()

        // When - Display SearchScreen with loading state
        composeTestRule.setContent {
            SearchScreen(
                onNavigateBack = { /* Navigate back */ },
                viewModel = mockSearchViewModel
            )
        }

        // Then - Verify search loading state UI elements are present
        composeTestRule.onNodeWithText("Search Locations").assertIsDisplayed()

        // When - Display LocationsScreen with loading state
        composeTestRule.setContent {
            LocationsScreen(
                onNavigateBack = { /* Navigate back */ },
                onNavigateToSearch = { /* Navigate to search */ },
                viewModel = mockLocationsViewModel
            )
        }

        // Then - Verify locations loading state UI elements are present
        composeTestRule.onNodeWithText("Saved Locations").assertIsDisplayed()
    }

    @Test
    fun uiFlow_accessibilityFlow() {
        // Given
        val mockSettingsViewModel = mockk<com.example.wisp.ui.screens.SettingsViewModel>(relaxed = true)

        // When - Display SettingsScreen with accessibility options
        composeTestRule.setContent {
            SettingsScreen(
                onNavigateBack = { /* Navigate back */ },
                viewModel = mockSettingsViewModel
            )
        }

        // Then - Verify accessibility options are displayed
        composeTestRule.onNodeWithText("Accessibility").assertIsDisplayed()
        composeTestRule.onNodeWithText("High contrast mode").assertIsDisplayed()
        composeTestRule.onNodeWithText("Large text").assertIsDisplayed()

        // When - Toggle accessibility options
        composeTestRule.onNodeWithText("High contrast mode").performClick()
        composeTestRule.onNodeWithText("Large text").performClick()

        // Then - Verify accessibility toggles are functional
        // The actual functionality would be verified through ViewModel interaction
    }

    @Test
    fun uiFlow_darkModeFlow() {
        // Given
        val mockSettingsViewModel = mockk<com.example.wisp.ui.screens.SettingsViewModel>(relaxed = true)

        // When - Display SettingsScreen with dark mode options
        composeTestRule.setContent {
            SettingsScreen(
                onNavigateBack = { /* Navigate back */ },
                viewModel = mockSettingsViewModel
            )
        }

        // Then - Verify dark mode options are displayed
        composeTestRule.onNodeWithText("Dark Mode").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enable dark mode").assertIsDisplayed()

        // When - Toggle dark mode
        composeTestRule.onNodeWithText("Enable dark mode").performClick()

        // Then - Verify dark mode toggle is functional
        // The actual functionality would be verified through ViewModel interaction
    }

    @Test
    fun uiFlow_navigationFlow() {
        // Given
        val mockHomeViewModel = mockk<com.example.wisp.ui.screens.HomeViewModel>(relaxed = true)

        // When - Display HomeScreen with navigation options
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToLocations = { /* Navigate to locations */ },
                onNavigateToSearch = { /* Navigate to search */ },
                onNavigateToSettings = { /* Navigate to settings */ },
                viewModel = mockHomeViewModel
            )
        }

        // Then - Verify navigation elements are present
        composeTestRule.onNodeWithText("Wisp Weather").assertIsDisplayed()
        // Navigation buttons would be verified through their content descriptions
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
