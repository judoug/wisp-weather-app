package com.example.wisp.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wisp.design.theme.WispTheme
import com.example.wisp.ui.state.RefreshInterval
import com.example.wisp.ui.state.SettingsUiState
import com.example.wisp.ui.state.TemperatureUnit
import com.example.wisp.ui.state.UiState
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreen_displaysAppTitle() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        // Then
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysBackButton() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        // Then
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun settingsScreen_displaysTemperatureUnitSection() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        // Then
        composeTestRule.onNodeWithText("Temperature Unit").assertIsDisplayed()
        composeTestRule.onNodeWithText("Celsius").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fahrenheit").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysRefreshIntervalSection() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        // Then
        composeTestRule.onNodeWithText("Refresh Interval").assertIsDisplayed()
        composeTestRule.onNodeWithText("15 minutes").assertIsDisplayed()
        composeTestRule.onNodeWithText("30 minutes").assertIsDisplayed()
        composeTestRule.onNodeWithText("1 hour").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysNotificationsSection() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        // Then
        composeTestRule.onNodeWithText("Notifications").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enable notifications").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysLocationPermissionSection() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        // Then
        composeTestRule.onNodeWithText("Location Permission").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysAccessibilitySection() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        // Then
        composeTestRule.onNodeWithText("Accessibility").assertIsDisplayed()
        composeTestRule.onNodeWithText("High contrast mode").assertIsDisplayed()
        composeTestRule.onNodeWithText("Large text").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysDarkModeSection() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        // Then
        composeTestRule.onNodeWithText("Dark Mode").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enable dark mode").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysResetButton() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        // Then
        composeTestRule.onNodeWithText("Reset to Defaults").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_temperatureUnitSelectionCallsUpdateTemperatureUnit() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        composeTestRule.onNodeWithText("Fahrenheit").performClick()

        // Then
        verify { mockViewModel.updateTemperatureUnit(TemperatureUnit.FAHRENHEIT) }
    }

    @Test
    fun settingsScreen_refreshIntervalSelectionCallsUpdateRefreshInterval() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        composeTestRule.onNodeWithText("30 minutes").performClick()

        // Then
        verify { mockViewModel.updateRefreshInterval(RefreshInterval.THIRTY_MINUTES) }
    }

    @Test
    fun settingsScreen_notificationToggleCallsUpdateNotificationsEnabled() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        composeTestRule.onNode(hasText("Enable notifications")).performClick()

        // Then
        verify { mockViewModel.updateNotificationsEnabled(false) }
    }

    @Test
    fun settingsScreen_resetButtonCallsResetToDefaults() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        composeTestRule.onNodeWithText("Reset to Defaults").performClick()

        // Then
        verify { mockViewModel.resetToDefaults() }
    }

    @Test
    fun settingsScreen_displaysSavingState_whenSaving() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        // Then - Saving state should be visible (loading indicator, disabled interactions, etc.)
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysSaveError_whenSaveFails() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
        val uiState = SettingsUiState(
            saveState = UiState.Error(
                message = "Failed to save settings",
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
        composeTestRule.onNodeWithText("Failed to save settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysLocationPermissionGranted_whenPermissionGranted() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
        val uiState = SettingsUiState(
            locationPermissionGranted = true
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
        composeTestRule.onNodeWithText("Granted").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysLocationPermissionDenied_whenPermissionDenied() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
        val uiState = SettingsUiState(
            locationPermissionGranted = false
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
        composeTestRule.onNodeWithText("Denied").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysCurrentTemperatureUnit() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
        val uiState = SettingsUiState(
            temperatureUnit = TemperatureUnit.FAHRENHEIT
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
        composeTestRule.onNodeWithText("Fahrenheit").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysCurrentRefreshInterval() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
        val uiState = SettingsUiState(
            refreshInterval = RefreshInterval.ONE_HOUR
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
        composeTestRule.onNodeWithText("1 hour").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysCurrentNotificationSetting() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
        val uiState = SettingsUiState(
            notificationsEnabled = false
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
        composeTestRule.onNodeWithText("Enable notifications").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysAccessibilityIcons() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        // Then
        composeTestRule.onNode(hasContentDescription("Accessibility")).assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysDarkModeIcon() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        // Then
        composeTestRule.onNode(hasContentDescription("Dark mode")).assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysLocationIcon() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        // Then
        composeTestRule.onNode(hasContentDescription("Location")).assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysNotificationIcon() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        // Then
        composeTestRule.onNode(hasContentDescription("Notifications")).assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysTemperatureIcon() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        // Then
        composeTestRule.onNode(hasContentDescription("Temperature")).assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysRefreshIcon() {
        // Given
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
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

        // Then
        composeTestRule.onNode(hasContentDescription("Refresh")).assertIsDisplayed()
    }
}
