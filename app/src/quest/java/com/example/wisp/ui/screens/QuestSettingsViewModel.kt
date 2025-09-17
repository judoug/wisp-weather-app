package com.example.wisp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wisp.data.location.QuestLocationProvider
import com.example.wisp.data.location.LocationSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Quest-specific settings screen.
 * 
 * Features:
 * - Manages IP location toggle state
 * - Handles manual location selection
 * - Provides current location information
 * - Quest-specific location management
 */
@HiltViewModel
class QuestSettingsViewModel @Inject constructor(
    private val questLocationProvider: QuestLocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestSettingsUiState())
    val uiState: StateFlow<QuestSettingsUiState> = _uiState.asStateFlow()

    init {
        loadCurrentSettings()
    }

    private fun loadCurrentSettings() {
        viewModelScope.launch {
            val isIpEnabled = questLocationProvider.isIpLocationEnabled()
            val locationName = questLocationProvider.getLocationName()
            val locationSource = questLocationProvider.getLocationSource()
            
            _uiState.value = _uiState.value.copy(
                isIpLocationEnabled = isIpEnabled,
                currentLocationName = locationName,
                locationSource = locationSource
            )
        }
    }

    fun onIpLocationToggle(enabled: Boolean) {
        viewModelScope.launch {
            questLocationProvider.setIpLocationEnabled(enabled)
            _uiState.value = _uiState.value.copy(
                isIpLocationEnabled = enabled
            )
        }
    }

    fun onManualLocationClick() {
        _uiState.value = _uiState.value.copy(
            navigateToLocationSearch = true
        )
    }

    fun onNavigationHandled() {
        _uiState.value = _uiState.value.copy(
            navigateToLocationSearch = false
        )
    }

    fun refreshLocationInfo() {
        loadCurrentSettings()
    }
}

/**
 * UI state for Quest settings screen.
 */
data class QuestSettingsUiState(
    val isIpLocationEnabled: Boolean = true,
    val currentLocationName: String = "Loading...",
    val locationSource: LocationSource = LocationSource.DEFAULT,
    val navigateToLocationSearch: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
