package com.example.wisp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wisp.data.weather.repository.WeatherDataRepository
import com.example.wisp.data.weather.service.OpenWeatherProvider
import com.example.wisp.domain.exception.TooManyPlacesException
import com.example.wisp.domain.exception.WeatherException
import com.example.wisp.domain.model.Place
import com.example.wisp.ui.state.SearchUiState
import com.example.wisp.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val weatherProvider: OpenWeatherProvider,
    private val weatherRepository: WeatherDataRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    private var searchJob: Job? = null
    private val searchHistory = mutableListOf<String>()
    
    init {
        loadSearchHistory()
    }
    
    /**
     * Updates the search query and triggers a search if the query is not empty.
     * @param query The search query
     */
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        
        // Cancel any existing search
        searchJob?.cancel()
        
        if (query.isNotBlank()) {
            // Debounce the search to avoid too many API calls
            searchJob = viewModelScope.launch {
                delay(500) // Wait 500ms before searching
                searchPlaces(query)
            }
        } else {
            // Clear results if query is empty
            _uiState.value = _uiState.value.copy(
                searchResultsState = UiState.Success(emptyList())
            )
        }
    }
    
    /**
     * Searches for places using the weather provider.
     * @param query The search query
     */
    private suspend fun searchPlaces(query: String) {
        _uiState.value = _uiState.value.copy(
            isSearching = true,
            searchResultsState = UiState.Loading
        )
        
        try {
            val results = weatherProvider.searchPlaces(query, limit = 8)
            _uiState.value = _uiState.value.copy(
                searchResultsState = UiState.Success(results),
                isSearching = false
            )
        } catch (e: WeatherException) {
            _uiState.value = _uiState.value.copy(
                searchResultsState = UiState.Error(
                    message = getSearchErrorMessage(e),
                    throwable = e,
                        retryAction = { viewModelScope.launch { searchPlaces(query) } }
                ),
                isSearching = false
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                searchResultsState = UiState.Error(
                    message = "Search failed. Please try again.",
                    throwable = e,
                        retryAction = { viewModelScope.launch { searchPlaces(query) } }
                ),
                isSearching = false
            )
        }
    }
    
    /**
     * Adds a place to the saved locations.
     * @param place The place to add
     */
    fun addPlace(place: Place) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isAddingPlaceToSaved = true,
                addingPlaceId = place.id
            )
            
            try {
                // Check if we've reached the maximum number of places
                val currentPlaces = weatherRepository.savedPlaces()
                if (currentPlaces.size >= 10) {
                    _uiState.value = _uiState.value.copy(
                        searchResultsState = UiState.Error(
                            message = "Maximum number of locations reached (10). Please remove a location first.",
                            retryAction = null
                        ),
                        isAddingPlaceToSaved = false,
                        addingPlaceId = null
                    )
                    return@launch
                }
                
                // Check if the place is already saved
                if (currentPlaces.any { it.id == place.id }) {
                    _uiState.value = _uiState.value.copy(
                        searchResultsState = UiState.Error(
                            message = "This location is already saved.",
                            retryAction = null
                        ),
                        isAddingPlaceToSaved = false,
                        addingPlaceId = null
                    )
                    return@launch
                }
                
                // Add the place
                weatherRepository.addPlace(place)
                
                // Add to search history
                addToSearchHistory(place.name)
                
                // Clear the search results to show success
                _uiState.value = _uiState.value.copy(
                    searchResultsState = UiState.Success(emptyList()),
                    isAddingPlaceToSaved = false,
                    addingPlaceId = null,
                    searchQuery = ""
                )
                
            } catch (e: TooManyPlacesException) {
                _uiState.value = _uiState.value.copy(
                    searchResultsState = UiState.Error(
                        message = "Maximum number of locations reached (10). Please remove a location first.",
                        retryAction = null
                    ),
                    isAddingPlaceToSaved = false,
                    addingPlaceId = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    searchResultsState = UiState.Error(
                        message = "Failed to add location. Please try again.",
                        throwable = e,
                        retryAction = { viewModelScope.launch { addPlace(place) } }
                    ),
                    isAddingPlaceToSaved = false,
                    addingPlaceId = null
                )
            }
        }
    }
    
    /**
     * Clears the search query and results.
     */
    fun clearSearch() {
        searchJob?.cancel()
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            searchResultsState = UiState.Success(emptyList()),
            isSearching = false
        )
    }
    
    /**
     * Retries the last search operation.
     */
    fun retrySearch() {
        val currentQuery = _uiState.value.searchQuery
        if (currentQuery.isNotBlank()) {
            viewModelScope.launch {
                searchPlaces(currentQuery)
            }
        }
    }
    
    /**
     * Loads search history from preferences (placeholder implementation).
     * In a real app, this would load from SharedPreferences or a database.
     */
    private fun loadSearchHistory() {
        // For now, we'll use a simple in-memory list
        // In a real implementation, this would load from SharedPreferences
        _uiState.value = _uiState.value.copy(searchHistory = searchHistory.toList())
    }
    
    /**
     * Adds a search term to the search history.
     * @param searchTerm The search term to add
     */
    private fun addToSearchHistory(searchTerm: String) {
        // Remove if already exists to avoid duplicates
        searchHistory.remove(searchTerm)
        // Add to the beginning
        searchHistory.add(0, searchTerm)
        // Keep only the last 10 searches
        if (searchHistory.size > 10) {
            searchHistory.removeAt(searchHistory.size - 1)
        }
        
        _uiState.value = _uiState.value.copy(searchHistory = searchHistory.toList())
    }
    
    /**
     * Uses a search term from history.
     * @param searchTerm The search term to use
     */
    fun useSearchHistory(searchTerm: String) {
        updateSearchQuery(searchTerm)
    }
    
    /**
     * Clears the search history.
     */
    fun clearSearchHistory() {
        searchHistory.clear()
        _uiState.value = _uiState.value.copy(searchHistory = emptyList())
    }
    
    /**
     * Converts weather exceptions to user-friendly error messages for search.
     */
    private fun getSearchErrorMessage(exception: WeatherException): String {
        return when (exception) {
            is com.example.wisp.domain.exception.WeatherApiException -> {
                if (exception.message?.contains("No internet connection") == true) {
                    "No internet connection. Please check your connection and try again."
                } else {
                    "Unable to search for locations. Please check your connection and try again."
                }
            }
            else -> {
                "Search failed. Please try again."
            }
        }
    }
}
