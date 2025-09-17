package com.example.wisp

import android.app.Application
import android.os.SystemClock
import com.example.wisp.analytics.AnalyticsManager
import com.example.wisp.performance.PerformanceManager
import com.example.wisp.performance.StartupOptimizationManager
import com.example.wisp.performance.BatteryOptimizationManager
import com.example.wisp.performance.MemoryOptimizationManager
import com.example.wisp.security.SecurityManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class WispApplication : Application() {
    
    @Inject
    lateinit var analyticsManager: AnalyticsManager
    
    @Inject
    lateinit var performanceManager: PerformanceManager
    
    @Inject
    lateinit var startupOptimizationManager: StartupOptimizationManager
    
    @Inject
    lateinit var batteryOptimizationManager: BatteryOptimizationManager
    
    @Inject
    lateinit var memoryOptimizationManager: MemoryOptimizationManager
    
    @Inject
    lateinit var securityManager: SecurityManager
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val appStartTime = SystemClock.elapsedRealtime()
    
    override fun onCreate() {
        super.onCreate()
        
        // Record app start time
        startupOptimizationManager.recordAppStart()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Record application create time
        startupOptimizationManager.recordApplicationCreate()
        
        // Initialize app components
        initializeApp()
    }
    
    private fun initializeApp() {
        applicationScope.launch {
            try {
                // Log app startup
                val startupTime = SystemClock.elapsedRealtime() - appStartTime
                performanceManager.logAppStartupTime(startupTime)
                
                // Initialize analytics
                initializeAnalytics()
                
                // Initialize security
                initializeSecurity()
                
                // Start performance monitoring
                startPerformanceMonitoring()
                
                // Log successful initialization
                analyticsManager.logEvent("app_initialized", mapOf(
                    "startup_time_ms" to startupTime,
                    "build_type" to BuildConfig.BUILD_TYPE
                ))
                
            } catch (e: Exception) {
                analyticsManager.logError(e, "App initialization failed")
            }
        }
    }
    
    private fun initializeAnalytics() {
        // Set up analytics user properties
        analyticsManager.setUserProperty("app_version", BuildConfig.VERSION_NAME)
        analyticsManager.setUserProperty("build_type", BuildConfig.BUILD_TYPE)
        analyticsManager.setUserProperty("debug_mode", BuildConfig.DEBUG_MODE.toString())
        
        // Log app launch
        analyticsManager.logEvent("app_launched", mapOf(
            "version" to BuildConfig.VERSION_NAME,
            "build_type" to BuildConfig.BUILD_TYPE
        ))
    }
    
    private fun initializeSecurity() {
        // Initialize secure storage
        // Check if this is first launch
        val isFirstLaunch = securityManager.getSecureData(SecurityManager.KEY_APP_FIRST_LAUNCH) == null
        if (isFirstLaunch) {
            securityManager.storeSecureData(SecurityManager.KEY_APP_FIRST_LAUNCH, "false")
            analyticsManager.logEvent("app_first_launch", emptyMap())
        }
    }
    
    private fun startPerformanceMonitoring() {
        // Start battery monitoring
        batteryOptimizationManager.startBatteryMonitoring()
        
        // Start memory monitoring
        memoryOptimizationManager.startMemoryMonitoring()
    }
    
    override fun onTerminate() {
        super.onTerminate()
        // Log app termination
        analyticsManager.logEvent("app_terminated", emptyMap())
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        // Log low memory warning
        performanceManager.logMemoryUsage("low_memory_warning")
        analyticsManager.logEvent("low_memory_warning", emptyMap())
    }
    
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // Log memory trim
        performanceManager.logMemoryUsage("memory_trim_$level")
        analyticsManager.logEvent("memory_trim", mapOf("level" to level))
    }
}

