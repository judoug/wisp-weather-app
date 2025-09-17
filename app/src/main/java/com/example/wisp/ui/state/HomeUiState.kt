package com.example.wisp.ui.state

import com.example.wisp.domain.model.WeatherBundle

/**
 * UI state for the Home screen.
 * Represents the current state of weather data loading and display.
 */
data class HomeUiState(
    val weatherState: UiState<WeatherBundle> = UiState.Loading,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false,
    val lastRefreshTime: Long? = null
) {
    /**
     * Whether the screen is currently showing any loading state.
     */
    val isLoading: Boolean
        get() = weatherState.isLoading() || isRefreshing
    
    /**
     * Whether the screen has successfully loaded weather data.
     */
    val hasWeatherData: Boolean
        get() = weatherState.isSuccess()
    
    /**
     * Whether the screen is showing an error state.
     */
    val hasError: Boolean
        get() = weatherState.isError()
    
    /**
     * Gets the weather data if available, or null otherwise.
     */
    val weatherData: WeatherBundle?
        get() = weatherState.getDataOrNull()
    
    /**
     * Gets the error message if there's an error, or null otherwise.
     */
    val errorMessage: String?
        get() = weatherState.getErrorMessageOrNull()
}
