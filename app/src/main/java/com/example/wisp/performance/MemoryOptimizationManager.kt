package com.example.wisp.performance

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import com.example.wisp.analytics.AnalyticsManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Memory optimization and monitoring
 */
@Singleton
class MemoryOptimizationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analyticsManager: AnalyticsManager
) {
    
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val memoryScope = CoroutineScope(Dispatchers.IO)
    
    /**
     * Get current memory usage
     */
    fun getCurrentMemoryUsage(): MemoryInfo {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        val availableMemory = maxMemory - usedMemory
        
        return MemoryInfo(
            usedMemory = usedMemory,
            maxMemory = maxMemory,
            availableMemory = availableMemory,
            usedPercentage = (usedMemory * 100) / maxMemory
        )
    }
    
    /**
     * Get system memory info
     */
    fun getSystemMemoryInfo(): ActivityManager.MemoryInfo {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo
    }
    
    /**
     * Check if memory is low
     */
    fun isMemoryLow(): Boolean {
        val memoryInfo = getSystemMemoryInfo()
        return memoryInfo.lowMemory
    }
    
    /**
     * Get memory usage by the app
     */
    fun getAppMemoryUsage(): Long {
        val memoryInfo = getSystemMemoryInfo()
        val pid = android.os.Process.myPid()
        val memoryInfos = activityManager.getProcessMemoryInfo(intArrayOf(pid))
        return if (memoryInfos.isNotEmpty()) {
            memoryInfos[0].totalPss * 1024L // Convert from KB to bytes
        } else {
            0L
        }
    }
    
    /**
     * Force garbage collection
     */
    fun forceGarbageCollection() {
        System.gc()
        System.runFinalization()
        System.gc()
    }
    
    /**
     * Check if memory usage is within acceptable limits
     */
    fun isMemoryUsageAcceptable(): Boolean {
        val memoryInfo = getCurrentMemoryUsage()
        return memoryInfo.usedPercentage < MEMORY_THRESHOLD_HIGH
    }
    
    /**
     * Get memory optimization recommendations
     */
    fun getMemoryOptimizationRecommendations(): List<String> {
        val recommendations = mutableListOf<String>()
        val memoryInfo = getCurrentMemoryUsage()
        val systemMemoryInfo = getSystemMemoryInfo()
        
        if (memoryInfo.usedPercentage > MEMORY_THRESHOLD_CRITICAL) {
            recommendations.add("Memory usage is critical. Consider closing other apps.")
        } else if (memoryInfo.usedPercentage > MEMORY_THRESHOLD_HIGH) {
            recommendations.add("Memory usage is high. Weather updates may be slower.")
        }
        
        if (systemMemoryInfo.lowMemory) {
            recommendations.add("System memory is low. App performance may be affected.")
        }
        
        if (memoryInfo.availableMemory < MIN_AVAILABLE_MEMORY) {
            recommendations.add("Available memory is low. Consider restarting the app.")
        }
        
        return recommendations
    }
    
    /**
     * Start memory monitoring
     */
    fun startMemoryMonitoring() {
        memoryScope.launch {
            while (true) {
                val memoryInfo = getCurrentMemoryUsage()
                val systemMemoryInfo = getSystemMemoryInfo()
                val appMemoryUsage = getAppMemoryUsage()
                
                // Log memory metrics
                analyticsManager.logEvent("memory_usage", mapOf(
                    "used_memory_mb" to (memoryInfo.usedMemory / (1024 * 1024)),
                    "max_memory_mb" to (memoryInfo.maxMemory / (1024 * 1024)),
                    "used_percentage" to memoryInfo.usedPercentage,
                    "app_memory_mb" to (appMemoryUsage / (1024 * 1024)),
                    "system_low_memory" to systemMemoryInfo.lowMemory
                ))
                
                // Log performance metrics
                analyticsManager.logEvent("memory_performance", mapOf(
                    "available_memory_mb" to (memoryInfo.availableMemory / (1024 * 1024)),
                    "memory_pressure" to getMemoryPressure(),
                    "gc_recommended" to (memoryInfo.usedPercentage > MEMORY_THRESHOLD_HIGH)
                ))
                
                // Force GC if memory usage is high
                if (memoryInfo.usedPercentage > MEMORY_THRESHOLD_CRITICAL) {
                    forceGarbageCollection()
                    analyticsManager.logEvent("memory_gc_forced", mapOf(
                        "memory_before_mb" to (memoryInfo.usedMemory / (1024 * 1024))
                    ))
                }
                
                // Wait 30 seconds before next check
                delay(30 * 1000)
            }
        }
    }
    
    /**
     * Get memory pressure level
     */
    private fun getMemoryPressure(): String {
        val memoryInfo = getCurrentMemoryUsage()
        return when {
            memoryInfo.usedPercentage > MEMORY_THRESHOLD_CRITICAL -> "CRITICAL"
            memoryInfo.usedPercentage > MEMORY_THRESHOLD_HIGH -> "HIGH"
            memoryInfo.usedPercentage > MEMORY_THRESHOLD_MEDIUM -> "MEDIUM"
            else -> "LOW"
        }
    }
    
    /**
     * Optimize memory usage
     */
    fun optimizeMemoryUsage() {
        val memoryInfo = getCurrentMemoryUsage()
        
        if (memoryInfo.usedPercentage > MEMORY_THRESHOLD_HIGH) {
            // Force garbage collection
            forceGarbageCollection()
            
            // Log optimization
            analyticsManager.logEvent("memory_optimization", mapOf(
                "memory_before_mb" to (memoryInfo.usedMemory / (1024 * 1024)),
                "optimization_triggered" to true
            ))
        }
    }
    
    /**
     * Check if app should reduce memory usage
     */
    fun shouldReduceMemoryUsage(): Boolean {
        val memoryInfo = getCurrentMemoryUsage()
        val systemMemoryInfo = getSystemMemoryInfo()
        
        return memoryInfo.usedPercentage > MEMORY_THRESHOLD_HIGH || systemMemoryInfo.lowMemory
    }
    
    /**
     * Get optimal cache size based on available memory
     */
    fun getOptimalCacheSize(): Long {
        val memoryInfo = getCurrentMemoryUsage()
        val availableMemory = memoryInfo.availableMemory
        
        return when {
            availableMemory > LARGE_MEMORY_THRESHOLD -> 50 * 1024 * 1024L // 50MB
            availableMemory > MEDIUM_MEMORY_THRESHOLD -> 25 * 1024 * 1024L // 25MB
            availableMemory > SMALL_MEMORY_THRESHOLD -> 10 * 1024 * 1024L // 10MB
            else -> 5 * 1024 * 1024L // 5MB
        }
    }
    
    /**
     * Get optimal image cache size
     */
    fun getOptimalImageCacheSize(): Long {
        val memoryInfo = getCurrentMemoryUsage()
        val availableMemory = memoryInfo.availableMemory
        
        return when {
            availableMemory > LARGE_MEMORY_THRESHOLD -> 20 * 1024 * 1024L // 20MB
            availableMemory > MEDIUM_MEMORY_THRESHOLD -> 10 * 1024 * 1024L // 10MB
            availableMemory > SMALL_MEMORY_THRESHOLD -> 5 * 1024 * 1024L // 5MB
            else -> 2 * 1024 * 1024L // 2MB
        }
    }
    
    data class MemoryInfo(
        val usedMemory: Long,
        val maxMemory: Long,
        val availableMemory: Long,
        val usedPercentage: Int
    )
    
    companion object {
        // Memory thresholds (percentages)
        const val MEMORY_THRESHOLD_LOW = 50
        const val MEMORY_THRESHOLD_MEDIUM = 70
        const val MEMORY_THRESHOLD_HIGH = 85
        const val MEMORY_THRESHOLD_CRITICAL = 95
        
        // Memory thresholds (bytes)
        const val MIN_AVAILABLE_MEMORY = 50 * 1024 * 1024L // 50MB
        const val SMALL_MEMORY_THRESHOLD = 100 * 1024 * 1024L // 100MB
        const val MEDIUM_MEMORY_THRESHOLD = 200 * 1024 * 1024L // 200MB
        const val LARGE_MEMORY_THRESHOLD = 500 * 1024 * 1024L // 500MB
    }
}
