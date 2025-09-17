package com.example.wisp.ui.state

/**
 * UI state for the Settings screen.
 * Represents the current state of app settings and preferences.
 */
data class SettingsUiState(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val refreshInterval: RefreshInterval = RefreshInterval.FIFTEEN_MINUTES,
    val notificationsEnabled: Boolean = true,
    val locationPermissionGranted: Boolean = false,
    val isSaving: Boolean = false,
    val saveState: UiState<Unit> = UiState.Success(Unit)
) {
    /**
     * Whether the screen is currently saving settings.
     */
    val isSavingSettings: Boolean
        get() = isSaving || saveState.isLoading()
    
    /**
     * Whether there was an error saving settings.
     */
    val hasSaveError: Boolean
        get() = saveState.isError()
    
    /**
     * Gets the save error message if there's an error, or null otherwise.
     */
    val saveErrorMessage: String?
        get() = saveState.getErrorMessageOrNull()
}

/**
 * Available temperature units for display.
 */
enum class TemperatureUnit(val displayName: String, val symbol: String) {
    CELSIUS("Celsius", "°C"),
    FAHRENHEIT("Fahrenheit", "°F")
}

/**
 * Available refresh intervals for weather data.
 */
enum class RefreshInterval(val displayName: String, val minutes: Int) {
    FIVE_MINUTES("5 minutes", 5),
    FIFTEEN_MINUTES("15 minutes", 15),
    THIRTY_MINUTES("30 minutes", 30),
    ONE_HOUR("1 hour", 60),
    TWO_HOURS("2 hours", 120)
}
