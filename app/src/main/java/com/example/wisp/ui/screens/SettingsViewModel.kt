package com.example.wisp.ui.screens

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wisp.ui.state.RefreshInterval
import com.example.wisp.ui.state.SettingsUiState
import com.example.wisp.ui.state.TemperatureUnit
import com.example.wisp.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    // SharedPreferences keys
    private val prefs = context.getSharedPreferences("wisp_settings", Context.MODE_PRIVATE)
    
    init {
        loadSettings()
        checkLocationPermission()
    }
    
    /**
     * Loads settings from SharedPreferences.
     */
    private fun loadSettings() {
        val temperatureUnit = TemperatureUnit.valueOf(
            prefs.getString("temperature_unit", TemperatureUnit.CELSIUS.name) ?: TemperatureUnit.CELSIUS.name
        )
        val refreshInterval = RefreshInterval.valueOf(
            prefs.getString("refresh_interval", RefreshInterval.FIFTEEN_MINUTES.name) ?: RefreshInterval.FIFTEEN_MINUTES.name
        )
        val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
        
        _uiState.value = _uiState.value.copy(
            temperatureUnit = temperatureUnit,
            refreshInterval = refreshInterval,
            notificationsEnabled = notificationsEnabled
        )
    }
    
    /**
     * Saves settings to SharedPreferences.
     */
    private fun saveSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            
            try {
                prefs.edit()
                    .putString("temperature_unit", _uiState.value.temperatureUnit.name)
                    .putString("refresh_interval", _uiState.value.refreshInterval.name)
                    .putBoolean("notifications_enabled", _uiState.value.notificationsEnabled)
                    .apply()
                
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveState = UiState.Success(Unit)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveState = UiState.Error(
                        message = "Failed to save settings. Please try again.",
                        throwable = e,
                        retryAction = { saveSettings() }
                    )
                )
            }
        }
    }
    
    /**
     * Updates the temperature unit setting.
     * @param unit The new temperature unit
     */
    fun updateTemperatureUnit(unit: TemperatureUnit) {
        _uiState.value = _uiState.value.copy(temperatureUnit = unit)
        saveSettings()
    }
    
    /**
     * Updates the refresh interval setting.
     * @param interval The new refresh interval
     */
    fun updateRefreshInterval(interval: RefreshInterval) {
        _uiState.value = _uiState.value.copy(refreshInterval = interval)
        saveSettings()
    }
    
    /**
     * Updates the notifications setting.
     * @param enabled Whether notifications are enabled
     */
    fun updateNotificationsEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
        saveSettings()
    }
    
    /**
     * Checks if location permission is granted.
     */
    private fun checkLocationPermission() {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        _uiState.value = _uiState.value.copy(
            locationPermissionGranted = hasFineLocation || hasCoarseLocation
        )
    }
    
    /**
     * Refreshes the location permission status.
     * This should be called when returning from the permission request.
     */
    fun refreshLocationPermission() {
        checkLocationPermission()
    }
    
    /**
     * Resets all settings to their default values.
     */
    fun resetToDefaults() {
        _uiState.value = _uiState.value.copy(
            temperatureUnit = TemperatureUnit.CELSIUS,
            refreshInterval = RefreshInterval.FIFTEEN_MINUTES,
            notificationsEnabled = true
        )
        saveSettings()
    }
    
    /**
     * Clears any save error state.
     */
    fun clearSaveError() {
        _uiState.value = _uiState.value.copy(saveState = UiState.Success(Unit))
    }
    
    /**
     * Gets the current temperature unit.
     * @return The current temperature unit
     */
    fun getTemperatureUnit(): TemperatureUnit {
        return _uiState.value.temperatureUnit
    }
    
    /**
     * Gets the current refresh interval.
     * @return The current refresh interval
     */
    fun getRefreshInterval(): RefreshInterval {
        return _uiState.value.refreshInterval
    }
    
    /**
     * Gets whether notifications are enabled.
     * @return true if notifications are enabled
     */
    fun areNotificationsEnabled(): Boolean {
        return _uiState.value.notificationsEnabled
    }
    
    /**
     * Gets whether location permission is granted.
     * @return true if location permission is granted
     */
    fun isLocationPermissionGranted(): Boolean {
        return _uiState.value.locationPermissionGranted
    }
}
