package com.example.wisp.data.weather.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages network connectivity state for the weather app.
 * Provides real-time network status updates and offline detection.
 */
@Singleton
class NetworkConnectivityManager @Inject constructor(
    private val context: Context
) {
    
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    /**
     * Flow that emits the current network connectivity status.
     * Emits true when connected to internet, false when offline.
     */
    val isConnectedFlow: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(isNetworkConnected())
            }
            
            override fun onLost(network: Network) {
                trySend(isNetworkConnected())
            }
            
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                trySend(isNetworkConnected())
            }
        }
        
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
            
        connectivityManager.registerNetworkCallback(request, callback)
        
        // Emit initial state
        trySend(isNetworkConnected())
        
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
    
    /**
     * Checks if the device is currently connected to the internet.
     * 
     * @return true if connected, false otherwise
     */
    fun isNetworkConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    /**
     * Checks if the device has any network connection (including unvalidated).
     * This is useful for detecting when the device is completely offline.
     * 
     * @return true if any network is available, false if completely offline
     */
    fun hasAnyNetworkConnection(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    
    /**
     * Gets the current network type if available.
     * 
     * @return NetworkType enum value or null if no network
     */
    fun getCurrentNetworkType(): NetworkType? {
        val network = connectivityManager.activeNetwork ?: return null
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return null
        
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> NetworkType.BLUETOOTH
            else -> NetworkType.UNKNOWN
        }
    }
}

/**
 * Enum representing different network connection types.
 */
enum class NetworkType {
    WIFI,
    CELLULAR,
    ETHERNET,
    BLUETOOTH,
    UNKNOWN
}
