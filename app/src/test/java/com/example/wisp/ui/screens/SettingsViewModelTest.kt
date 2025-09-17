package com.example.wisp.ui.screens

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import app.cash.turbine.test
import com.example.wisp.ui.state.RefreshInterval
import com.example.wisp.ui.state.SettingsUiState
import com.example.wisp.ui.state.TemperatureUnit
import com.example.wisp.ui.state.UiState
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var mockContext: Context
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock Context and SharedPreferences
        mockContext = mockk()
        mockSharedPreferences = mockk()
        mockEditor = mockk()
        
        every { mockContext.getSharedPreferences("wisp_settings", Context.MODE_PRIVATE) } returns mockSharedPreferences
        every { mockSharedPreferences.edit() } returns mockEditor
        every { mockEditor.putString(any(), any()) } returns mockEditor
        every { mockEditor.putBoolean(any(), any()) } returns mockEditor
        every { mockEditor.apply() } returns Unit
        
        // Mock ContextCompat
        mockkStatic(ContextCompat::class)
        
        viewModel = SettingsViewModel(mockContext)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load default settings`() = runTest {
        // Given
        every { mockSharedPreferences.getString("temperature_unit", TemperatureUnit.CELSIUS.name) } returns TemperatureUnit.CELSIUS.name
        every { mockSharedPreferences.getString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) } returns RefreshInterval.FIFTEEN_MINUTES.name
        every { mockSharedPreferences.getBoolean("notifications_enabled", true) } returns true
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_GRANTED
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) } returns PackageManager.PERMISSION_GRANTED

        // When
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(TemperatureUnit.CELSIUS, state.temperatureUnit)
            assertEquals(RefreshInterval.FIFTEEN_MINUTES, state.refreshInterval)
            assertTrue(state.notificationsEnabled)
            assertTrue(state.locationPermissionGranted)
            assertFalse(state.isSaving)
            assertTrue(state.saveState is UiState.Success)
        }
    }

    @Test
    fun `should load custom settings from preferences`() = runTest {
        // Given
        every { mockSharedPreferences.getString("temperature_unit", TemperatureUnit.CELSIUS.name) } returns TemperatureUnit.FAHRENHEIT.name
        every { mockSharedPreferences.getString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) } returns RefreshInterval.THIRTY_MINUTES.name
        every { mockSharedPreferences.getBoolean("notifications_enabled", true) } returns false
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_DENIED
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) } returns PackageManager.PERMISSION_DENIED

        // When
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(TemperatureUnit.FAHRENHEIT, state.temperatureUnit)
            assertEquals(RefreshInterval.THIRTY_MINUTES, state.refreshInterval)
            assertFalse(state.notificationsEnabled)
            assertFalse(state.locationPermissionGranted)
        }
    }

    @Test
    fun `updateTemperatureUnit should update unit and save`() = runTest {
        // Given
        every { mockSharedPreferences.getString("temperature_unit", TemperatureUnit.CELSIUS.name) } returns TemperatureUnit.CELSIUS.name
        every { mockSharedPreferences.getString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) } returns RefreshInterval.FIFTEEN_MINUTES.name
        every { mockSharedPreferences.getBoolean("notifications_enabled", true) } returns true
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_GRANTED
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) } returns PackageManager.PERMISSION_GRANTED

        // When
        viewModel.updateTemperatureUnit(TemperatureUnit.FAHRENHEIT)
        advanceUntilIdle()

        // Then
        verify { mockEditor.putString("temperature_unit", TemperatureUnit.FAHRENHEIT.name) }
        verify { mockEditor.apply() }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(TemperatureUnit.FAHRENHEIT, state.temperatureUnit)
        }
    }

    @Test
    fun `updateRefreshInterval should update interval and save`() = runTest {
        // Given
        every { mockSharedPreferences.getString("temperature_unit", TemperatureUnit.CELSIUS.name) } returns TemperatureUnit.CELSIUS.name
        every { mockSharedPreferences.getString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) } returns RefreshInterval.FIFTEEN_MINUTES.name
        every { mockSharedPreferences.getBoolean("notifications_enabled", true) } returns true
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_GRANTED
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) } returns PackageManager.PERMISSION_GRANTED

        // When
        viewModel.updateRefreshInterval(RefreshInterval.ONE_HOUR)
        advanceUntilIdle()

        // Then
        verify { mockEditor.putString("refresh_interval", RefreshInterval.ONE_HOUR.name) }
        verify { mockEditor.apply() }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(RefreshInterval.ONE_HOUR, state.refreshInterval)
        }
    }

    @Test
    fun `updateNotificationsEnabled should update setting and save`() = runTest {
        // Given
        every { mockSharedPreferences.getString("temperature_unit", TemperatureUnit.CELSIUS.name) } returns TemperatureUnit.CELSIUS.name
        every { mockSharedPreferences.getString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) } returns RefreshInterval.FIFTEEN_MINUTES.name
        every { mockSharedPreferences.getBoolean("notifications_enabled", true) } returns true
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_GRANTED
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) } returns PackageManager.PERMISSION_GRANTED

        // When
        viewModel.updateNotificationsEnabled(false)
        advanceUntilIdle()

        // Then
        verify { mockEditor.putBoolean("notifications_enabled", false) }
        verify { mockEditor.apply() }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.notificationsEnabled)
        }
    }

    @Test
    fun `should handle location permission granted`() = runTest {
        // Given
        every { mockSharedPreferences.getString("temperature_unit", TemperatureUnit.CELSIUS.name) } returns TemperatureUnit.CELSIUS.name
        every { mockSharedPreferences.getString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) } returns RefreshInterval.FIFTEEN_MINUTES.name
        every { mockSharedPreferences.getBoolean("notifications_enabled", true) } returns true
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_GRANTED
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) } returns PackageManager.PERMISSION_DENIED

        // When
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.locationPermissionGranted)
        }
    }

    @Test
    fun `should handle location permission denied`() = runTest {
        // Given
        every { mockSharedPreferences.getString("temperature_unit", TemperatureUnit.CELSIUS.name) } returns TemperatureUnit.CELSIUS.name
        every { mockSharedPreferences.getString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) } returns RefreshInterval.FIFTEEN_MINUTES.name
        every { mockSharedPreferences.getBoolean("notifications_enabled", true) } returns true
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_DENIED
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) } returns PackageManager.PERMISSION_DENIED

        // When
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.locationPermissionGranted)
        }
    }

    @Test
    fun `refreshLocationPermission should check permission again`() = runTest {
        // Given
        every { mockSharedPreferences.getString("temperature_unit", TemperatureUnit.CELSIUS.name) } returns TemperatureUnit.CELSIUS.name
        every { mockSharedPreferences.getString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) } returns RefreshInterval.FIFTEEN_MINUTES.name
        every { mockSharedPreferences.getBoolean("notifications_enabled", true) } returns true
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_DENIED
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) } returns PackageManager.PERMISSION_DENIED

        // When
        advanceUntilIdle()
        viewModel.refreshLocationPermission()
        advanceUntilIdle()

        // Then
        verify(exactly = 2) { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_FINE_LOCATION) }
        verify(exactly = 2) { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) }
    }

    @Test
    fun `resetToDefaults should reset all settings`() = runTest {
        // Given
        every { mockSharedPreferences.getString("temperature_unit", TemperatureUnit.CELSIUS.name) } returns TemperatureUnit.FAHRENHEIT.name
        every { mockSharedPreferences.getString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) } returns RefreshInterval.ONE_HOUR.name
        every { mockSharedPreferences.getBoolean("notifications_enabled", true) } returns false
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_GRANTED
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) } returns PackageManager.PERMISSION_GRANTED

        // When
        advanceUntilIdle()
        viewModel.resetToDefaults()
        advanceUntilIdle()

        // Then
        verify { mockEditor.putString("temperature_unit", TemperatureUnit.CELSIUS.name) }
        verify { mockEditor.putString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) }
        verify { mockEditor.putBoolean("notifications_enabled", true) }
        verify { mockEditor.apply() }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(TemperatureUnit.CELSIUS, state.temperatureUnit)
            assertEquals(RefreshInterval.FIFTEEN_MINUTES, state.refreshInterval)
            assertTrue(state.notificationsEnabled)
        }
    }

    @Test
    fun `clearSaveError should clear error state`() = runTest {
        // Given
        every { mockSharedPreferences.getString("temperature_unit", TemperatureUnit.CELSIUS.name) } returns TemperatureUnit.CELSIUS.name
        every { mockSharedPreferences.getString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) } returns RefreshInterval.FIFTEEN_MINUTES.name
        every { mockSharedPreferences.getBoolean("notifications_enabled", true) } returns true
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_GRANTED
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) } returns PackageManager.PERMISSION_GRANTED

        // When
        advanceUntilIdle()
        viewModel.clearSaveError()
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.saveState is UiState.Success)
        }
    }

    @Test
    fun `getTemperatureUnit should return current unit`() = runTest {
        // Given
        every { mockSharedPreferences.getString("temperature_unit", TemperatureUnit.CELSIUS.name) } returns TemperatureUnit.FAHRENHEIT.name
        every { mockSharedPreferences.getString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) } returns RefreshInterval.FIFTEEN_MINUTES.name
        every { mockSharedPreferences.getBoolean("notifications_enabled", true) } returns true
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_GRANTED
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) } returns PackageManager.PERMISSION_GRANTED

        // When
        advanceUntilIdle()

        // Then
        assertEquals(TemperatureUnit.FAHRENHEIT, viewModel.getTemperatureUnit())
    }

    @Test
    fun `getRefreshInterval should return current interval`() = runTest {
        // Given
        every { mockSharedPreferences.getString("temperature_unit", TemperatureUnit.CELSIUS.name) } returns TemperatureUnit.CELSIUS.name
        every { mockSharedPreferences.getString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) } returns RefreshInterval.THIRTY_MINUTES.name
        every { mockSharedPreferences.getBoolean("notifications_enabled", true) } returns true
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_GRANTED
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) } returns PackageManager.PERMISSION_GRANTED

        // When
        advanceUntilIdle()

        // Then
        assertEquals(RefreshInterval.THIRTY_MINUTES, viewModel.getRefreshInterval())
    }

    @Test
    fun `areNotificationsEnabled should return current setting`() = runTest {
        // Given
        every { mockSharedPreferences.getString("temperature_unit", TemperatureUnit.CELSIUS.name) } returns TemperatureUnit.CELSIUS.name
        every { mockSharedPreferences.getString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) } returns RefreshInterval.FIFTEEN_MINUTES.name
        every { mockSharedPreferences.getBoolean("notifications_enabled", true) } returns false
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_GRANTED
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) } returns PackageManager.PERMISSION_GRANTED

        // When
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.areNotificationsEnabled())
    }

    @Test
    fun `isLocationPermissionGranted should return current permission status`() = runTest {
        // Given
        every { mockSharedPreferences.getString("temperature_unit", TemperatureUnit.CELSIUS.name) } returns TemperatureUnit.CELSIUS.name
        every { mockSharedPreferences.getString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) } returns RefreshInterval.FIFTEEN_MINUTES.name
        every { mockSharedPreferences.getBoolean("notifications_enabled", true) } returns true
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_GRANTED
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) } returns PackageManager.PERMISSION_GRANTED

        // When
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.isLocationPermissionGranted())
    }

    @Test
    fun `should set saving state during save operation`() = runTest {
        // Given
        every { mockSharedPreferences.getString("temperature_unit", TemperatureUnit.CELSIUS.name) } returns TemperatureUnit.CELSIUS.name
        every { mockSharedPreferences.getString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) } returns RefreshInterval.FIFTEEN_MINUTES.name
        every { mockSharedPreferences.getBoolean("notifications_enabled", true) } returns true
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_GRANTED
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) } returns PackageManager.PERMISSION_GRANTED

        // When
        viewModel.updateTemperatureUnit(TemperatureUnit.FAHRENHEIT)

        // Then - Check intermediate state
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isSaving)
        }
        
        advanceUntilIdle()
        
        // Then - Check final state
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isSaving)
            assertTrue(state.saveState is UiState.Success)
        }
    }

    @Test
    fun `should handle null preference values with defaults`() = runTest {
        // Given
        every { mockSharedPreferences.getString("temperature_unit", TemperatureUnit.CELSIUS.name) } returns null
        every { mockSharedPreferences.getString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) } returns null
        every { mockSharedPreferences.getBoolean("notifications_enabled", true) } returns true
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_GRANTED
        every { ContextCompat.checkSelfPermission(mockContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) } returns PackageManager.PERMISSION_GRANTED

        // When
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(TemperatureUnit.CELSIUS, state.temperatureUnit)
            assertEquals(RefreshInterval.FIFTEEN_MINUTES, state.refreshInterval)
        }
    }
}
