package com.example.wisp.analytics

import android.content.Context
import android.os.Bundle
import com.example.wisp.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Analytics and crash reporting manager
 */
@Singleton
class AnalyticsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private val crashlytics = FirebaseCrashlytics.getInstance()
    
    init {
        // Set user properties
        setupUserProperties()
        
        // Configure crashlytics
        setupCrashlytics()
    }
    
    private fun setupUserProperties() {
        // Set app version
        firebaseAnalytics.setUserProperty("app_version", BuildConfig.VERSION_NAME)
        firebaseAnalytics.setUserProperty("build_type", BuildConfig.BUILD_TYPE)
        
        // Set debug mode
        firebaseAnalytics.setUserProperty("debug_mode", BuildConfig.DEBUG_MODE.toString())
    }
    
    private fun setupCrashlytics() {
        // Set custom keys for crashlytics
        crashlytics.setCustomKey("app_version", BuildConfig.VERSION_NAME)
        crashlytics.setCustomKey("build_type", BuildConfig.BUILD_TYPE)
        crashlytics.setCustomKey("debug_mode", BuildConfig.DEBUG_MODE)
        
        // Enable/disable crashlytics based on build type
        crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG_MODE)
    }
    
    /**
     * Log screen view
     */
    fun logScreenView(screenName: String, screenClass: String? = null) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            screenClass?.let { putString(FirebaseAnalytics.Param.SCREEN_CLASS, it) }
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }
    
    /**
     * Log weather data fetch
     */
    fun logWeatherFetch(location: String, success: Boolean, responseTime: Long) {
        val bundle = Bundle().apply {
            putString("location", location)
            putBoolean("success", success)
            putLong("response_time_ms", responseTime)
        }
        firebaseAnalytics.logEvent("weather_fetch", bundle)
    }
    
    /**
     * Log location permission request
     */
    fun logLocationPermissionRequest(granted: Boolean) {
        val bundle = Bundle().apply {
            putBoolean("granted", granted)
        }
        firebaseAnalytics.logEvent("location_permission_request", bundle)
    }
    
    /**
     * Log place search
     */
    fun logPlaceSearch(query: String, resultsCount: Int) {
        val bundle = Bundle().apply {
            putString("query", query)
            putInt("results_count", resultsCount)
        }
        firebaseAnalytics.logEvent("place_search", bundle)
    }
    
    /**
     * Log place addition
     */
    fun logPlaceAdded(placeName: String, totalPlaces: Int) {
        val bundle = Bundle().apply {
            putString("place_name", placeName)
            putInt("total_places", totalPlaces)
        }
        firebaseAnalytics.logEvent("place_added", bundle)
    }
    
    /**
     * Log place removal
     */
    fun logPlaceRemoved(placeName: String, totalPlaces: Int) {
        val bundle = Bundle().apply {
            putString("place_name", placeName)
            putInt("total_places", totalPlaces)
        }
        firebaseAnalytics.logEvent("place_removed", bundle)
    }
    
    /**
     * Log settings change
     */
    fun logSettingsChange(setting: String, value: String) {
        val bundle = Bundle().apply {
            putString("setting", setting)
            putString("value", value)
        }
        firebaseAnalytics.logEvent("settings_change", bundle)
    }
    
    /**
     * Log app performance metrics
     */
    fun logPerformanceMetric(metric: String, value: Long, unit: String = "ms") {
        val bundle = Bundle().apply {
            putString("metric", metric)
            putLong("value", value)
            putString("unit", unit)
        }
        firebaseAnalytics.logEvent("performance_metric", bundle)
    }
    
    /**
     * Log error for crashlytics
     */
    fun logError(throwable: Throwable, context: String? = null) {
        context?.let { crashlytics.setCustomKey("error_context", it) }
        crashlytics.recordException(throwable)
    }
    
    /**
     * Log custom error message
     */
    fun logError(message: String, context: String? = null) {
        context?.let { crashlytics.setCustomKey("error_context", it) }
        crashlytics.log(message)
    }
    
    /**
     * Set user identifier for analytics
     */
    fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
        crashlytics.setUserId(userId)
    }
    
    /**
     * Set custom user property
     */
    fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
        crashlytics.setCustomKey(name, value)
    }
}
