package com.example.wisp.performance

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.PowerManager
import com.example.wisp.analytics.AnalyticsManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Battery optimization and power management
 */
@Singleton
class BatteryOptimizationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analyticsManager: AnalyticsManager
) {
    
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    private val optimizationScope = CoroutineScope(Dispatchers.IO)
    
    /**
     * Check if device is in battery saver mode
     */
    fun isBatterySaverMode(): Boolean {
        return powerManager.isPowerSaveMode
    }
    
    /**
     * Get current battery level
     */
    fun getBatteryLevel(): Int {
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }
    
    /**
     * Check if device is charging
     */
    fun isCharging(): Boolean {
        val batteryStatus = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
        return batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                batteryStatus == BatteryManager.BATTERY_STATUS_FULL
    }
    
    /**
     * Check if device is connected to WiFi
     */
    fun isWiFiConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }
    
    /**
     * Check if device is connected to cellular data
     */
    fun isCellularConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }
    
    /**
     * Get optimal refresh interval based on battery and network conditions
     */
    fun getOptimalRefreshInterval(): Long {
        val batteryLevel = getBatteryLevel()
        val isBatterySaver = isBatterySaverMode()
        val isCharging = isCharging()
        val isWiFi = isWiFiConnected()
        
        return when {
            isBatterySaver || batteryLevel < 20 -> 30 * 60 * 1000L // 30 minutes
            batteryLevel < 50 && !isCharging -> 15 * 60 * 1000L // 15 minutes
            isWiFi -> 10 * 60 * 1000L // 10 minutes
            isCellularConnected() -> 15 * 60 * 1000L // 15 minutes
            else -> 20 * 60 * 1000L // 20 minutes
        }
    }
    
    /**
     * Check if background sync should be enabled
     */
    fun shouldEnableBackgroundSync(): Boolean {
        val batteryLevel = getBatteryLevel()
        val isBatterySaver = isBatterySaverMode()
        val isCharging = isCharging()
        
        return when {
            isBatterySaver -> false
            batteryLevel < 15 && !isCharging -> false
            batteryLevel < 30 && !isCharging -> false
            else -> true
        }
    }
    
    /**
     * Check if high-quality weather icons should be used
     */
    fun shouldUseHighQualityIcons(): Boolean {
        val batteryLevel = getBatteryLevel()
        val isBatterySaver = isBatterySaverMode()
        val isWiFi = isWiFiConnected()
        
        return when {
            isBatterySaver -> false
            batteryLevel < 30 && !isWiFi -> false
            isWiFi -> true
            else -> true
        }
    }
    
    /**
     * Check if animations should be reduced
     */
    fun shouldReduceAnimations(): Boolean {
        val batteryLevel = getBatteryLevel()
        val isBatterySaver = isBatterySaverMode()
        
        return isBatterySaver || batteryLevel < 20
    }
    
    /**
     * Check if location updates should be reduced
     */
    fun shouldReduceLocationUpdates(): Boolean {
        val batteryLevel = getBatteryLevel()
        val isBatterySaver = isBatterySaverMode()
        
        return isBatterySaver || batteryLevel < 25
    }
    
    /**
     * Get optimal location update interval
     */
    fun getOptimalLocationUpdateInterval(): Long {
        val batteryLevel = getBatteryLevel()
        val isBatterySaver = isBatterySaverMode()
        
        return when {
            isBatterySaver -> 60 * 60 * 1000L // 1 hour
            batteryLevel < 30 -> 30 * 60 * 1000L // 30 minutes
            batteryLevel < 50 -> 15 * 60 * 1000L // 15 minutes
            else -> 10 * 60 * 1000L // 10 minutes
        }
    }
    
    /**
     * Monitor battery usage and log analytics
     */
    fun startBatteryMonitoring() {
        optimizationScope.launch {
            while (true) {
                val batteryLevel = getBatteryLevel()
                val isBatterySaver = isBatterySaverMode()
                val isCharging = isCharging()
                val isWiFi = isWiFiConnected()
                
                // Log battery status
                analyticsManager.logEvent("battery_status", mapOf(
                    "battery_level" to batteryLevel,
                    "battery_saver" to isBatterySaver,
                    "charging" to isCharging,
                    "wifi_connected" to isWiFi
                ))
                
                // Log battery optimization recommendations
                val refreshInterval = getOptimalRefreshInterval()
                val backgroundSync = shouldEnableBackgroundSync()
                val highQualityIcons = shouldUseHighQualityIcons()
                val reduceAnimations = shouldReduceAnimations()
                
                analyticsManager.logEvent("battery_optimization", mapOf(
                    "refresh_interval_ms" to refreshInterval,
                    "background_sync_enabled" to backgroundSync,
                    "high_quality_icons" to highQualityIcons,
                    "reduce_animations" to reduceAnimations
                ))
                
                // Wait 5 minutes before next check
                delay(5 * 60 * 1000)
            }
        }
    }
    
    /**
     * Request battery optimization exemption
     */
    fun requestBatteryOptimizationExemption() {
        try {
            val intent = Intent().apply {
                action = "android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"
                data = android.net.Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            analyticsManager.logError(e, "Failed to request battery optimization exemption")
        }
    }
    
    /**
     * Check if app is whitelisted from battery optimization
     */
    fun isBatteryOptimizationWhitelisted(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true
        }
    }
    
    /**
     * Get battery optimization recommendations
     */
    fun getBatteryOptimizationRecommendations(): List<String> {
        val recommendations = mutableListOf<String>()
        val batteryLevel = getBatteryLevel()
        val isBatterySaver = isBatterySaverMode()
        val isCharging = isCharging()
        val isWiFi = isWiFiConnected()
        
        if (isBatterySaver) {
            recommendations.add("Battery saver mode is enabled. Weather updates will be less frequent.")
        }
        
        if (batteryLevel < 20) {
            recommendations.add("Battery level is low. Consider charging your device for optimal performance.")
        }
        
        if (!isWiFi && isCellularConnected()) {
            recommendations.add("Using cellular data. Weather updates will be optimized to save data.")
        }
        
        if (!isCharging && batteryLevel < 50) {
            recommendations.add("Consider charging your device to enable background weather updates.")
        }
        
        return recommendations
    }
    
    companion object {
        // Battery level thresholds
        const val BATTERY_LEVEL_CRITICAL = 15
        const val BATTERY_LEVEL_LOW = 30
        const val BATTERY_LEVEL_MEDIUM = 50
        
        // Default intervals (in milliseconds)
        const val DEFAULT_REFRESH_INTERVAL = 15 * 60 * 1000L // 15 minutes
        const val DEFAULT_LOCATION_INTERVAL = 10 * 60 * 1000L // 10 minutes
        const val BATTERY_SAVER_REFRESH_INTERVAL = 30 * 60 * 1000L // 30 minutes
        const val BATTERY_SAVER_LOCATION_INTERVAL = 60 * 60 * 1000L // 1 hour
    }
}
