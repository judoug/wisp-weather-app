package com.example.wisp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wisp.data.weather.repository.WeatherDataRepository
import com.example.wisp.domain.exception.WeatherException
import com.example.wisp.domain.model.Place
import com.example.wisp.domain.model.WeatherBundle
import com.example.wisp.ui.state.HomeUiState
import com.example.wisp.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherDataRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    init {
        loadPrimaryPlaceWeather()
        observeConnectivity()
    }
    
    /**
     * Loads weather data for the primary place.
     * This is called when the ViewModel is first created.
     */
    private fun loadPrimaryPlaceWeather() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(weatherState = UiState.Loading)
            
            try {
                // Get the primary place
                val places = weatherRepository.savedPlaces()
                val primaryPlace = places.find { it.id == "primary" } ?: places.firstOrNull()
                
                if (primaryPlace == null) {
                    _uiState.value = _uiState.value.copy(
                        weatherState = UiState.Error(
                            message = "No locations saved. Please add a location to see weather data.",
                            retryAction = null
                        )
                    )
                    return@launch
                }
                
                // Load weather data for the primary place
                val weatherBundle = weatherRepository.weatherFor(primaryPlace, forceRefresh = false)
                _uiState.value = _uiState.value.copy(
                    weatherState = UiState.Success(weatherBundle),
                    lastRefreshTime = System.currentTimeMillis()
                )
                
            } catch (e: WeatherException) {
                _uiState.value = _uiState.value.copy(
                    weatherState = UiState.Error(
                        message = getErrorMessage(e),
                        throwable = e,
                        retryAction = { loadPrimaryPlaceWeather() }
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    weatherState = UiState.Error(
                        message = "An unexpected error occurred. Please try again.",
                        throwable = e,
                        retryAction = { loadPrimaryPlaceWeather() }
                    )
                )
            }
        }
    }
    
    /**
     * Refreshes the weather data for the primary place.
     * This is called when the user pulls to refresh or taps the refresh button.
     */
    fun refreshWeather() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            
            try {
                val places = weatherRepository.savedPlaces()
                val primaryPlace = places.find { it.id == "primary" } ?: places.firstOrNull()
                
                if (primaryPlace == null) {
                    _uiState.value = _uiState.value.copy(
                        weatherState = UiState.Error(
                            message = "No locations saved. Please add a location to see weather data.",
                            retryAction = null
                        ),
                        isRefreshing = false
                    )
                    return@launch
                }
                
                // Force refresh the weather data
                val weatherBundle = weatherRepository.weatherFor(primaryPlace, forceRefresh = true)
                _uiState.value = _uiState.value.copy(
                    weatherState = UiState.Success(weatherBundle),
                    isRefreshing = false,
                    lastRefreshTime = System.currentTimeMillis()
                )
                
            } catch (e: WeatherException) {
                _uiState.value = _uiState.value.copy(
                    weatherState = UiState.Error(
                        message = getErrorMessage(e),
                        throwable = e,
                        retryAction = { refreshWeather() }
                    ),
                    isRefreshing = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    weatherState = UiState.Error(
                        message = "Failed to refresh weather data. Please try again.",
                        throwable = e,
                        retryAction = { refreshWeather() }
                    ),
                    isRefreshing = false
                )
            } finally {
                _isRefreshing.value = false
            }
        }
    }
    
    /**
     * Observes network connectivity changes and updates the offline state.
     */
    private fun observeConnectivity() {
        viewModelScope.launch {
            weatherRepository.connectivityFlow().collect { isConnected ->
                _uiState.value = _uiState.value.copy(isOffline = !isConnected)
            }
        }
    }
    
    /**
     * Retries loading weather data after an error.
     */
    fun retryLoadWeather() {
        loadPrimaryPlaceWeather()
    }
    
    /**
     * Converts weather exceptions to user-friendly error messages.
     */
    private fun getErrorMessage(exception: WeatherException): String {
        return when (exception) {
            is com.example.wisp.domain.exception.WeatherApiException -> {
                if (exception.message?.contains("No internet connection") == true) {
                    "No internet connection. Showing cached data if available."
                } else {
                    "Unable to fetch weather data. Please check your connection and try again."
                }
            }
            is com.example.wisp.domain.exception.LocationUnavailableException -> {
                "Location services are unavailable. Please check your location settings."
            }
            is com.example.wisp.domain.exception.PlaceNotFoundException -> {
                "Location not found. Please try a different location."
            }
            else -> {
                "Unable to load weather data. Please try again."
            }
        }
    }
}
