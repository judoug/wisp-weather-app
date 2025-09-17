package com.example.wisp.ui.state

import com.example.wisp.domain.model.Place

/**
 * UI state for the Locations screen.
 * Represents the current state of saved locations management.
 */
data class LocationsUiState(
    val placesState: UiState<List<Place>> = UiState.Loading,
    val isRefreshing: Boolean = false,
    val selectedPlaceId: String? = null,
    val isDeleting: Boolean = false,
    val deletingPlaceId: String? = null
) {
    /**
     * Whether the screen is currently showing any loading state.
     */
    val isLoading: Boolean
        get() = placesState.isLoading() || isRefreshing
    
    /**
     * Whether the screen has successfully loaded places data.
     */
    val hasPlaces: Boolean
        get() = placesState.isSuccess()
    
    /**
     * Whether the screen is showing an error state.
     */
    val hasError: Boolean
        get() = placesState.isError()
    
    /**
     * Gets the places list if available, or empty list otherwise.
     */
    val places: List<Place>
        get() = placesState.getDataOrNull() ?: emptyList()
    
    /**
     * Gets the error message if there's an error, or null otherwise.
     */
    val errorMessage: String?
        get() = placesState.getErrorMessageOrNull()
    
    /**
     * Whether there are any saved places.
     */
    val isEmpty: Boolean
        get() = places.isEmpty()
    
    /**
     * Whether a place is currently being deleted.
     */
    val isDeletingPlace: Boolean
        get() = isDeleting && deletingPlaceId != null
}
