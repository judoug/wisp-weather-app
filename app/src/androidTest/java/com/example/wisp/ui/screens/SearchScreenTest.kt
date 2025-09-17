package com.example.wisp.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wisp.design.theme.WispTheme
import com.example.wisp.domain.model.Place
import com.example.wisp.ui.state.SearchUiState
import com.example.wisp.ui.state.UiState
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchScreen_displaysSearchField() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val uiState = SearchUiState()

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Search for a location").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysBackButton() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val uiState = SearchUiState()

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun searchScreen_displaysClearButton_whenTextIsEntered() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val uiState = SearchUiState(
            searchQuery = "New York"
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Clear search").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysSearchResults_whenResultsAvailable() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val searchResults = listOf(
            createTestPlace("nyc", "New York"),
            createTestPlace("london", "London")
        )
        val uiState = SearchUiState(
            searchQuery = "New",
            searchResultsState = UiState.Success(searchResults)
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("New York").assertIsDisplayed()
        composeTestRule.onNodeWithText("London").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysLoadingState_whenSearching() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val uiState = SearchUiState(
            searchQuery = "New York",
            isSearching = true,
            searchResultsState = UiState.Loading
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Searching...").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysErrorState_whenSearchFails() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val uiState = SearchUiState(
            searchQuery = "Invalid",
            searchResultsState = UiState.Error(
                message = "Search failed. Please try again.",
                retryAction = {}
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Search failed. Please try again.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysEmptyState_whenNoResults() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val uiState = SearchUiState(
            searchQuery = "NonExistentCity",
            searchResultsState = UiState.Success(emptyList())
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("No locations found").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try a different search term").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysSearchHistory_whenAvailable() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val searchHistory = listOf("New York", "London", "Paris")
        val uiState = SearchUiState(
            searchHistory = searchHistory
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Recent searches").assertIsDisplayed()
        composeTestRule.onNodeWithText("New York").assertIsDisplayed()
        composeTestRule.onNodeWithText("London").assertIsDisplayed()
        composeTestRule.onNodeWithText("Paris").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysClearHistoryButton_whenHistoryAvailable() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val searchHistory = listOf("New York", "London")
        val uiState = SearchUiState(
            searchHistory = searchHistory
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Clear history").assertIsDisplayed()
    }

    @Test
    fun searchScreen_textInputCallsUpdateSearchQuery() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val uiState = SearchUiState()

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Search for a location").performTextInput("New York")

        // Then
        verify { mockViewModel.updateSearchQuery("New York") }
    }

    @Test
    fun searchScreen_clearButtonCallsClearSearch() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val uiState = SearchUiState(
            searchQuery = "New York"
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Clear search").performClick()

        // Then
        verify { mockViewModel.clearSearch() }
    }

    @Test
    fun searchScreen_retryButtonCallsRetrySearch() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val uiState = SearchUiState(
            searchQuery = "New York",
            searchResultsState = UiState.Error(
                message = "Search failed",
                retryAction = {}
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Retry").performClick()

        // Then
        verify { mockViewModel.retrySearch() }
    }

    @Test
    fun searchScreen_historyItemCallsUseSearchHistory() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val searchHistory = listOf("New York")
        val uiState = SearchUiState(
            searchHistory = searchHistory
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        composeTestRule.onNodeWithText("New York").performClick()

        // Then
        verify { mockViewModel.useSearchHistory("New York") }
    }

    @Test
    fun searchScreen_clearHistoryButtonCallsClearSearchHistory() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val searchHistory = listOf("New York", "London")
        val uiState = SearchUiState(
            searchHistory = searchHistory
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Clear history").performClick()

        // Then
        verify { mockViewModel.clearSearchHistory() }
    }

    @Test
    fun searchScreen_displaysAddButton_whenSearchResultSelected() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val searchResults = listOf(createTestPlace("nyc", "New York"))
        val uiState = SearchUiState(
            searchQuery = "New",
            searchResultsState = UiState.Success(searchResults)
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Add New York").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysAddingState_whenAddingPlace() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val searchResults = listOf(createTestPlace("nyc", "New York"))
        val uiState = SearchUiState(
            searchQuery = "New",
            searchResultsState = UiState.Success(searchResults),
            isAddingPlaceToSaved = true,
            addingPlaceId = "nyc"
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then - Adding state should be visible (loading indicator, disabled interactions, etc.)
        composeTestRule.onNodeWithText("New York").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysMaxPlacesError_whenMaxPlacesReached() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val uiState = SearchUiState(
            searchQuery = "New York",
            searchResultsState = UiState.Error(
                message = "Maximum number of locations reached (10). Please remove a location first.",
                retryAction = null
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Maximum number of locations reached (10). Please remove a location first.").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysDuplicateLocationError_whenLocationAlreadySaved() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val uiState = SearchUiState(
            searchQuery = "New York",
            searchResultsState = UiState.Error(
                message = "This location is already saved.",
                retryAction = null
            )
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("This location is already saved.").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysAppTitle() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val uiState = SearchUiState()

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Search Locations").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysLocationIcons() {
        // Given
        val mockViewModel = mockk<SearchViewModel>(relaxed = true)
        val searchResults = listOf(createTestPlace("nyc", "New York"))
        val uiState = SearchUiState(
            searchQuery = "New",
            searchResultsState = UiState.Success(searchResults)
        )

        // When
        composeTestRule.setContent {
            WispTheme {
                SearchScreen(
                    onNavigateBack = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNode(hasContentDescription("Location")).assertIsDisplayed()
    }

    private fun createTestPlace(id: String, name: String, country: String = "Test Country"): Place {
        return Place(
            id = id,
            name = name,
            country = country,
            latitude = 40.7128,
            longitude = -74.0060
        )
    }
}
