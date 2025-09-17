package com.example.wisp.ui.state

import com.example.wisp.domain.model.Place

/**
 * UI state for the Search screen.
 * Represents the current state of location search functionality.
 */
data class SearchUiState(
    val searchQuery: String = "",
    val searchResultsState: UiState<List<Place>> = UiState.Success(emptyList()),
    val isSearching: Boolean = false,
    val isAddingPlaceToSaved: Boolean = false,
    val addingPlaceId: String? = null,
    val searchHistory: List<String> = emptyList()
) {
    /**
     * Whether the screen is currently showing any loading state.
     */
    val isLoading: Boolean
        get() = searchResultsState.isLoading() || isSearching
    
    /**
     * Whether the screen has successfully loaded search results.
     */
    val hasSearchResults: Boolean
        get() = searchResultsState.isSuccess()
    
    /**
     * Whether the screen is showing an error state.
     */
    val hasError: Boolean
        get() = searchResultsState.isError()
    
    /**
     * Gets the search results if available, or empty list otherwise.
     */
    val searchResults: List<Place>
        get() = searchResultsState.getDataOrNull() ?: emptyList()
    
    /**
     * Gets the error message if there's an error, or null otherwise.
     */
    val errorMessage: String?
        get() = searchResultsState.getErrorMessageOrNull()
    
    /**
     * Whether there are any search results to display.
     */
    val hasResults: Boolean
        get() = searchResults.isNotEmpty()
    
    /**
     * Whether the search query is empty.
     */
    val isQueryEmpty: Boolean
        get() = searchQuery.isBlank()
    
    /**
     * Whether a place is currently being added.
     */
    val isAddingPlace: Boolean
        get() = isAddingPlaceToSaved && addingPlaceId != null
    
    /**
     * Whether there's any search history available.
     */
    val hasSearchHistory: Boolean
        get() = searchHistory.isNotEmpty()
}
