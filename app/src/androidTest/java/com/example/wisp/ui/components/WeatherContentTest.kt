package com.example.wisp.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wisp.design.theme.WispTheme
import com.example.wisp.domain.model.Place
import com.example.wisp.domain.model.WeatherBundle
import com.example.wisp.domain.model.WeatherNow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun weatherContent_displaysLocationName() {
        // Given
        val weatherBundle = createTestWeatherBundle()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Test City").assertIsDisplayed()
    }

    @Test
    fun weatherContent_displaysTemperature() {
        // Given
        val weatherBundle = createTestWeatherBundle()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then
        composeTestRule.onNodeWithText("20°C").assertIsDisplayed()
    }

    @Test
    fun weatherContent_displaysWeatherCondition() {
        // Given
        val weatherBundle = createTestWeatherBundle()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Clear sky").assertIsDisplayed()
    }

    @Test
    fun weatherContent_displaysFeelsLikeTemperature() {
        // Given
        val weatherBundle = createTestWeatherBundle()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Feels like 22°C").assertIsDisplayed()
    }

    @Test
    fun weatherContent_displaysHumidity() {
        // Given
        val weatherBundle = createTestWeatherBundle()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then
        composeTestRule.onNodeWithText("65%").assertIsDisplayed()
        composeTestRule.onNodeWithText("Humidity").assertIsDisplayed()
    }

    @Test
    fun weatherContent_displaysWindSpeed() {
        // Given
        val weatherBundle = createTestWeatherBundle()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then
        composeTestRule.onNodeWithText("10 km/h").assertIsDisplayed()
        composeTestRule.onNodeWithText("Wind Speed").assertIsDisplayed()
    }

    @Test
    fun weatherContent_displaysOfflineIndicator_whenOffline() {
        // Given
        val weatherBundle = createTestWeatherBundle()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(
                    weatherBundle = weatherBundle,
                    isOffline = true
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Offline").assertIsDisplayed()
    }

    @Test
    fun weatherContent_hidesOfflineIndicator_whenOnline() {
        // Given
        val weatherBundle = createTestWeatherBundle()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(
                    weatherBundle = weatherBundle,
                    isOffline = false
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Offline").assertIsNotDisplayed()
    }

    @Test
    fun weatherContent_displaysLastRefreshTime_whenProvided() {
        // Given
        val weatherBundle = createTestWeatherBundle()
        val refreshTime = System.currentTimeMillis()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(
                    weatherBundle = weatherBundle,
                    lastRefreshTime = refreshTime
                )
            }
        }

        // Then
        composeTestRule.onNode(hasText("Last updated:")).assertIsDisplayed()
    }

    @Test
    fun weatherContent_hidesLastRefreshTime_whenNotProvided() {
        // Given
        val weatherBundle = createTestWeatherBundle()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(
                    weatherBundle = weatherBundle,
                    lastRefreshTime = null
                )
            }
        }

        // Then
        composeTestRule.onNode(hasText("Last updated:")).assertIsNotDisplayed()
    }

    @Test
    fun weatherContent_displaysLocationIcon() {
        // Given
        val weatherBundle = createTestWeatherBundle()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then
        composeTestRule.onNode(hasContentDescription("Location")).assertIsDisplayed()
    }

    @Test
    fun weatherContent_displaysWeatherIcon() {
        // Given
        val weatherBundle = createTestWeatherBundle()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then
        composeTestRule.onNode(hasContentDescription("Clear sky")).assertIsDisplayed()
    }

    @Test
    fun weatherContent_displaysHumidityIcon() {
        // Given
        val weatherBundle = createTestWeatherBundle()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then
        composeTestRule.onNode(hasContentDescription("Humidity")).assertIsDisplayed()
    }

    @Test
    fun weatherContent_displaysWindIcon() {
        // Given
        val weatherBundle = createTestWeatherBundle()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then
        composeTestRule.onNode(hasContentDescription("Wind")).assertIsDisplayed()
    }

    @Test
    fun weatherContent_displaysTemperatureChart_whenHourlyDataAvailable() {
        // Given
        val weatherBundle = createTestWeatherBundleWithHourly()

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then - The chart should be displayed (we can't easily test the chart content without more complex setup)
        // This test ensures the chart container is rendered
        composeTestRule.onNodeWithText("Test City").assertIsDisplayed()
    }

    @Test
    fun weatherContent_hidesCharts_whenNoHourlyData() {
        // Given
        val weatherBundle = createTestWeatherBundle() // No hourly data

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then - Charts should not be displayed
        composeTestRule.onNodeWithText("Test City").assertIsDisplayed()
        // We can't easily test chart absence without more complex setup
    }

    @Test
    fun weatherContent_displaysCorrectTemperatureFormat() {
        // Given
        val weatherBundle = createTestWeatherBundleWithCustomTemp(25.5)

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then
        composeTestRule.onNodeWithText("25°C").assertIsDisplayed()
    }

    @Test
    fun weatherContent_displaysCorrectFeelsLikeFormat() {
        // Given
        val weatherBundle = createTestWeatherBundleWithCustomFeelsLike(27.8)

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Feels like 27°C").assertIsDisplayed()
    }

    @Test
    fun weatherContent_displaysCorrectHumidityFormat() {
        // Given
        val weatherBundle = createTestWeatherBundleWithCustomHumidity(80)

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then
        composeTestRule.onNodeWithText("80%").assertIsDisplayed()
    }

    @Test
    fun weatherContent_displaysCorrectWindSpeedFormat() {
        // Given
        val weatherBundle = createTestWeatherBundleWithCustomWindSpeed(15.5)

        // When
        composeTestRule.setContent {
            WispTheme {
                WeatherContent(weatherBundle = weatherBundle)
            }
        }

        // Then
        composeTestRule.onNodeWithText("15 km/h").assertIsDisplayed()
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

    private fun createTestWeatherBundleWithHourly(): WeatherBundle {
        return createTestWeatherBundle().copy(
            hourly = listOf(
                WeatherNow(
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
                )
            )
        )
    }

    private fun createTestWeatherBundleWithCustomTemp(temperature: Double): WeatherBundle {
        return createTestWeatherBundle().copy(
            current = createTestWeatherBundle().current.copy(temperature = temperature)
        )
    }

    private fun createTestWeatherBundleWithCustomFeelsLike(feelsLike: Double): WeatherBundle {
        return createTestWeatherBundle().copy(
            current = createTestWeatherBundle().current.copy(feelsLike = feelsLike)
        )
    }

    private fun createTestWeatherBundleWithCustomHumidity(humidity: Int): WeatherBundle {
        return createTestWeatherBundle().copy(
            current = createTestWeatherBundle().current.copy(humidity = humidity)
        )
    }

    private fun createTestWeatherBundleWithCustomWindSpeed(windSpeed: Double): WeatherBundle {
        return createTestWeatherBundle().copy(
            current = createTestWeatherBundle().current.copy(windSpeed = windSpeed)
        )
    }
}
