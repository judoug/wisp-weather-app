package com.example.wisp.ui.state

/**
 * Base sealed class for UI states that can be used across all screens.
 * Provides a consistent pattern for handling loading, success, and error states.
 */
sealed class UiState<out T> {
    /**
     * Represents a loading state while data is being fetched.
     */
    object Loading : UiState<Nothing>()
    
    /**
     * Represents a successful state with data.
     * @param data The data that was successfully loaded
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * Represents an error state with a user-friendly message.
     * @param message User-friendly error message
     * @param throwable Optional underlying exception for debugging
     * @param retryAction Optional action to retry the failed operation
     */
    data class Error(
        val message: String,
        val throwable: Throwable? = null,
        val retryAction: (() -> Unit)? = null
    ) : UiState<Nothing>()
}

/**
 * Extension function to check if the state is loading.
 */
fun <T> UiState<T>.isLoading(): Boolean = this is UiState.Loading

/**
 * Extension function to check if the state is successful.
 */
fun <T> UiState<T>.isSuccess(): Boolean = this is UiState.Success

/**
 * Extension function to check if the state is an error.
 */
fun <T> UiState<T>.isError(): Boolean = this is UiState.Error

/**
 * Extension function to get the data from a successful state, or null otherwise.
 */
fun <T> UiState<T>.getDataOrNull(): T? = when (this) {
    is UiState.Success -> data
    else -> null
}

/**
 * Extension function to get the error message from an error state, or null otherwise.
 */
fun <T> UiState<T>.getErrorMessageOrNull(): String? = when (this) {
    is UiState.Error -> message
    else -> null
}
