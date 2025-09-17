package com.example.wisp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wisp.data.weather.repository.WeatherDataRepository
import com.example.wisp.domain.exception.TooManyPlacesException
import com.example.wisp.domain.exception.WeatherException
import com.example.wisp.domain.model.Place
import com.example.wisp.ui.state.LocationsUiState
import com.example.wisp.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(
    private val weatherRepository: WeatherDataRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LocationsUiState())
    val uiState: StateFlow<LocationsUiState> = _uiState.asStateFlow()
    
    init {
        loadSavedPlaces()
    }
    
    /**
     * Loads all saved places from the repository.
     */
    private fun loadSavedPlaces() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(placesState = UiState.Loading)
            
            try {
                val places = weatherRepository.savedPlaces()
                _uiState.value = _uiState.value.copy(
                    placesState = UiState.Success(places)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    placesState = UiState.Error(
                        message = "Failed to load saved locations. Please try again.",
                        throwable = e,
                        retryAction = { loadSavedPlaces() }
                    )
                )
            }
        }
    }
    
    /**
     * Refreshes the list of saved places.
     */
    fun refreshPlaces() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            
            try {
                val places = weatherRepository.savedPlaces()
                _uiState.value = _uiState.value.copy(
                    placesState = UiState.Success(places),
                    isRefreshing = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    placesState = UiState.Error(
                        message = "Failed to refresh locations. Please try again.",
                        throwable = e,
                        retryAction = { refreshPlaces() }
                    ),
                    isRefreshing = false
                )
            }
        }
    }
    
    /**
     * Removes a place from the saved locations.
     * @param placeId The ID of the place to remove
     */
    fun removePlace(placeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDeleting = true,
                deletingPlaceId = placeId
            )
            
            try {
                weatherRepository.removePlace(placeId)
                
                // Reload the places list to reflect the change
                val places = weatherRepository.savedPlaces()
                _uiState.value = _uiState.value.copy(
                    placesState = UiState.Success(places),
                    isDeleting = false,
                    deletingPlaceId = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    placesState = UiState.Error(
                        message = "Failed to remove location. Please try again.",
                        throwable = e,
                        retryAction = { loadSavedPlaces() }
                    ),
                    isDeleting = false,
                    deletingPlaceId = null
                )
            }
        }
    }
    
    /**
     * Sets a place as the primary location.
     * @param placeId The ID of the place to set as primary
     */
    fun setPrimaryPlace(placeId: String) {
        viewModelScope.launch {
            try {
                weatherRepository.setPrimary(placeId)
                
                // Reload the places list to reflect the change
                val places = weatherRepository.savedPlaces()
                _uiState.value = _uiState.value.copy(
                    placesState = UiState.Success(places)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    placesState = UiState.Error(
                        message = "Failed to set primary location. Please try again.",
                        throwable = e,
                        retryAction = { loadSavedPlaces() }
                    )
                )
            }
        }
    }
    
    /**
     * Selects a place for potential actions (like setting as primary or removing).
     * @param placeId The ID of the place to select
     */
    fun selectPlace(placeId: String) {
        _uiState.value = _uiState.value.copy(selectedPlaceId = placeId)
    }
    
    /**
     * Clears the selected place.
     */
    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedPlaceId = null)
    }
    
    /**
     * Retries loading saved places after an error.
     */
    fun retryLoadPlaces() {
        loadSavedPlaces()
    }
    
    /**
     * Checks if the maximum number of places has been reached.
     * @return true if the maximum number of places has been reached
     */
    fun isMaxPlacesReached(): Boolean {
        val currentPlaces = _uiState.value.places
        return currentPlaces.size >= 10 // Maximum 10 places as per domain layer
    }
    
    /**
     * Gets the number of saved places.
     * @return The number of saved places
     */
    fun getPlacesCount(): Int {
        return _uiState.value.places.size
    }
    
    /**
     * Gets the primary place from the current list.
     * @return The primary place, or null if none is set
     */
    fun getPrimaryPlace(): Place? {
        return _uiState.value.places.find { it.id == "primary" }
    }
}
