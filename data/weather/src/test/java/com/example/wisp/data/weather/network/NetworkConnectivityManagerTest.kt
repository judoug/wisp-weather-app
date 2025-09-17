package com.example.wisp.data.weather.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NetworkConnectivityManagerTest {
    
    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkConnectivityManager: NetworkConnectivityManager
    
    @BeforeEach
    fun setup() {
        context = mockk()
        connectivityManager = mockk()
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        networkConnectivityManager = NetworkConnectivityManager(context)
    }
    
    @Test
    fun `isNetworkConnected should return true when network is available and validated`() {
        // Given
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns true
        
        // When
        val result = networkConnectivityManager.isNetworkConnected()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `isNetworkConnected should return false when network is not available`() {
        // Given
        every { connectivityManager.activeNetwork } returns null
        
        // When
        val result = networkConnectivityManager.isNetworkConnected()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `isNetworkConnected should return false when network has no internet capability`() {
        // Given
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns false
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns true
        
        // When
        val result = networkConnectivityManager.isNetworkConnected()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `isNetworkConnected should return false when network is not validated`() {
        // Given
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns false
        
        // When
        val result = networkConnectivityManager.isNetworkConnected()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `hasAnyNetworkConnection should return true when network has internet capability`() {
        // Given
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        
        // When
        val result = networkConnectivityManager.hasAnyNetworkConnection()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `hasAnyNetworkConnection should return false when no network is available`() {
        // Given
        every { connectivityManager.activeNetwork } returns null
        
        // When
        val result = networkConnectivityManager.hasAnyNetworkConnection()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `getCurrentNetworkType should return WIFI when network uses WiFi transport`() {
        // Given
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns true
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) } returns false
        
        // When
        val result = networkConnectivityManager.getCurrentNetworkType()
        
        // Then
        assertEquals(NetworkType.WIFI, result)
    }
    
    @Test
    fun `getCurrentNetworkType should return CELLULAR when network uses cellular transport`() {
        // Given
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns true
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) } returns false
        
        // When
        val result = networkConnectivityManager.getCurrentNetworkType()
        
        // Then
        assertEquals(NetworkType.CELLULAR, result)
    }
    
    @Test
    fun `getCurrentNetworkType should return ETHERNET when network uses ethernet transport`() {
        // Given
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns true
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) } returns false
        
        // When
        val result = networkConnectivityManager.getCurrentNetworkType()
        
        // Then
        assertEquals(NetworkType.ETHERNET, result)
    }
    
    @Test
    fun `getCurrentNetworkType should return BLUETOOTH when network uses bluetooth transport`() {
        // Given
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) } returns true
        
        // When
        val result = networkConnectivityManager.getCurrentNetworkType()
        
        // Then
        assertEquals(NetworkType.BLUETOOTH, result)
    }
    
    @Test
    fun `getCurrentNetworkType should return UNKNOWN when network has unknown transport`() {
        // Given
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) } returns false
        
        // When
        val result = networkConnectivityManager.getCurrentNetworkType()
        
        // Then
        assertEquals(NetworkType.UNKNOWN, result)
    }
    
    @Test
    fun `getCurrentNetworkType should return null when no network is available`() {
        // Given
        every { connectivityManager.activeNetwork } returns null
        
        // When
        val result = networkConnectivityManager.getCurrentNetworkType()
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun `getCurrentNetworkType should return null when network capabilities are null`() {
        // Given
        val network = mockk<Network>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns null
        
        // When
        val result = networkConnectivityManager.getCurrentNetworkType()
        
        // Then
        assertNull(result)
    }
}
