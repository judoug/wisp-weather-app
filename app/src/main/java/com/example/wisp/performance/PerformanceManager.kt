package com.example.wisp.performance

import android.content.Context
import com.example.wisp.analytics.AnalyticsManager
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Performance monitoring and optimization manager
 */
@Singleton
class PerformanceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analyticsManager: AnalyticsManager
) {
    
    private val firebasePerformance = FirebasePerformance.getInstance()
    private val performanceScope = CoroutineScope(Dispatchers.IO)
    
    /**
     * Start a performance trace
     */
    fun startTrace(traceName: String): Trace {
        return firebasePerformance.newTrace(traceName).apply {
            start()
        }
    }
    
    /**
     * Stop a performance trace
     */
    fun stopTrace(trace: Trace) {
        trace.stop()
    }
    
    /**
     * Measure execution time of a block
     */
    suspend fun <T> measureExecution(
        traceName: String,
        block: suspend () -> T
    ): T = withContext(Dispatchers.IO) {
        val trace = startTrace(traceName)
        try {
            val startTime = System.currentTimeMillis()
            val result = block()
            val endTime = System.currentTimeMillis()
            val executionTime = endTime - startTime
            
            // Log to analytics
            performanceScope.launch {
                analyticsManager.logPerformanceMetric(traceName, executionTime)
            }
            
            result
        } finally {
            stopTrace(trace)
        }
    }
    
    /**
     * Measure weather API call performance
     */
    suspend fun <T> measureWeatherApiCall(
        location: String,
        block: suspend () -> T
    ): T = measureExecution("weather_api_call_$location", block)
    
    /**
     * Measure database operation performance
     */
    suspend fun <T> measureDatabaseOperation(
        operation: String,
        block: suspend () -> T
    ): T = measureExecution("database_$operation", block)
    
    /**
     * Measure UI rendering performance
     */
    suspend fun <T> measureUIRendering(
        screen: String,
        block: suspend () -> T
    ): T = measureExecution("ui_rendering_$screen", block)
    
    /**
     * Monitor memory usage
     */
    fun logMemoryUsage(context: String) {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        val memoryUsagePercent = (usedMemory * 100) / maxMemory
        
        analyticsManager.logPerformanceMetric("memory_usage_$context", memoryUsagePercent, "percent")
    }
    
    /**
     * Monitor app startup time
     */
    fun logAppStartupTime(startupTime: Long) {
        analyticsManager.logPerformanceMetric("app_startup", startupTime)
    }
    
    /**
     * Monitor screen load time
     */
    fun logScreenLoadTime(screen: String, loadTime: Long) {
        analyticsManager.logPerformanceMetric("screen_load_$screen", loadTime)
    }
    
    /**
     * Monitor network request performance
     */
    fun logNetworkRequest(url: String, responseTime: Long, success: Boolean) {
        val metricName = if (success) "network_request_success" else "network_request_failure"
        analyticsManager.logPerformanceMetric(metricName, responseTime)
    }
    
    companion object {
        // Common trace names
        const val TRACE_APP_STARTUP = "app_startup"
        const val TRACE_WEATHER_FETCH = "weather_fetch"
        const val TRACE_LOCATION_FETCH = "location_fetch"
        const val TRACE_DATABASE_QUERY = "database_query"
        const val TRACE_UI_RENDER = "ui_render"
        const val TRACE_PLACE_SEARCH = "place_search"
    }
}
