package com.example.wisp

import android.app.Application
import com.example.wisp.performance.QuestPerformanceManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Quest-specific Application class with VR/AR optimizations.
 * 
 * Features:
 * - Quest performance management
 * - VR-specific initialization
 * - Memory optimization
 * - Battery optimization
 */
@HiltAndroidApp
class WispApplication : Application() {
    
    @Inject
    lateinit var questPerformanceManager: QuestPerformanceManager
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Quest-specific optimizations
        initializeQuestOptimizations()
    }
    
    private fun initializeQuestOptimizations() {
        // Performance optimizations will be handled by Hilt injection
        // when the QuestPerformanceManager is first used
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        // Force garbage collection on low memory
        System.gc()
    }
    
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        
        when (level) {
            TRIM_MEMORY_RUNNING_CRITICAL -> {
                // Critical memory pressure - aggressive cleanup
                System.gc()
            }
            TRIM_MEMORY_RUNNING_LOW -> {
                // Low memory - moderate cleanup
                System.gc()
            }
            TRIM_MEMORY_RUNNING_MODERATE -> {
                // Moderate memory pressure - light cleanup
                // Could implement more specific cleanup here
            }
        }
    }
}
