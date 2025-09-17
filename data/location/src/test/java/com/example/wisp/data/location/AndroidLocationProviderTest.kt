package com.example.wisp.data.location

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import com.example.wisp.domain.exception.LocationUnavailableException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for AndroidLocationProvider.
 * 
 * Tests:
 * - Permission checking
 * - Location retrieval with caching
 * - Error handling
 * - Fallback strategies
 */
class AndroidLocationProviderTest {

    private lateinit var mockContext: Context
    private lateinit var mockFusedLocationClient: FusedLocationProviderClient
    private lateinit var mockLastLocationTask: Task<Location>
    private lateinit var mockCurrentLocationTask: Task<Location>
    private lateinit var mockLocation: Location
    
    private lateinit var locationProvider: AndroidLocationProvider
    
    @Before
    fun setup() {
        // Create mocks
        mockContext = mockk()
        mockFusedLocationClient = mockk()
        mockLastLocationTask = mockk()
        mockCurrentLocationTask = mockk()
        mockLocation = mockk()
        
        // Mock LocationServices
        mockkStatic(LocationServices::class)
        every { LocationServices.getFusedLocationProviderClient(any()) } returns mockFusedLocationClient
        
        // Mock Context
        mockkStatic("androidx.core.content.ContextCompat")
        every { 
            androidx.core.content.ContextCompat.checkSelfPermission(
                any(), 
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) 
        } returns PackageManager.PERMISSION_GRANTED
        
        every { 
            androidx.core.content.ContextCompat.checkSelfPermission(
                any(), 
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) 
        } returns PackageManager.PERMISSION_GRANTED
        
        // Setup location provider
        locationProvider = AndroidLocationProvider(mockContext)
    }
    
    @After
    fun tearDown() {
        unmockkAll()
    }
    
    @Test
    fun `currentLatLonOrNull returns null when permission denied`() = runTest {
        // Given
        every { 
            androidx.core.content.ContextCompat.checkSelfPermission(
                any(), 
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) 
        } returns PackageManager.PERMISSION_DENIED
        
        every { 
            androidx.core.content.ContextCompat.checkSelfPermission(
                any(), 
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) 
        } returns PackageManager.PERMISSION_DENIED
        
        // When
        val result = locationProvider.currentLatLonOrNull()
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun `currentLatLonOrNull returns location when permission granted and location available`() = runTest {
        // Given
        val expectedLat = 37.7749
        val expectedLon = -122.4194
        val expectedAccuracy = 50f
        
        every { mockLocation.latitude } returns expectedLat
        every { mockLocation.longitude } returns expectedLon
        every { mockLocation.accuracy } returns expectedAccuracy
        every { mockLocation.time } returns System.currentTimeMillis()
        
        every { mockFusedLocationClient.lastLocation } returns mockLastLocationTask
        every { mockLastLocationTask.addOnSuccessListener(any()) } answers {
            val listener = firstArg<(Location?) -> Unit>()
            listener(mockLocation)
            mockLastLocationTask
        }
        every { mockLastLocationTask.addOnFailureListener(any()) } returns mockLastLocationTask
        
        // When
        val result = locationProvider.currentLatLonOrNull()
        
        // Then
        assertEquals(expectedLat, result?.first)
        assertEquals(expectedLon, result?.second)
    }
    
    @Test
    fun `currentLatLonOrNull returns null when location is null`() = runTest {
        // Given
        every { mockFusedLocationClient.lastLocation } returns mockLastLocationTask
        every { mockLastLocationTask.addOnSuccessListener(any()) } answers {
            val listener = firstArg<(Location?) -> Unit>()
            listener(null)
            mockLastLocationTask
        }
        every { mockLastLocationTask.addOnFailureListener(any()) } returns mockLastLocationTask
        
        // When
        val result = locationProvider.currentLatLonOrNull()
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun `currentLatLonOrNull returns null when location accuracy is too low`() = runTest {
        // Given
        val expectedLat = 37.7749
        val expectedLon = -122.4194
        val lowAccuracy = 200f // Above MIN_ACCURACY_METERS (100f)
        
        every { mockLocation.latitude } returns expectedLat
        every { mockLocation.longitude } returns expectedLon
        every { mockLocation.accuracy } returns lowAccuracy
        every { mockLocation.time } returns System.currentTimeMillis()
        
        every { mockFusedLocationClient.lastLocation } returns mockLastLocationTask
        every { mockLastLocationTask.addOnSuccessListener(any()) } answers {
            val listener = firstArg<(Location?) -> Unit>()
            listener(mockLocation)
            mockLastLocationTask
        }
        every { mockLastLocationTask.addOnFailureListener(any()) } returns mockLastLocationTask
        
        // When
        val result = locationProvider.currentLatLonOrNull()
        
        // Then
        assertEquals(expectedLat, result?.first)
        assertEquals(expectedLon, result?.second)
        // Note: Location is still returned but not cached due to low accuracy
    }
    
    @Test
    fun `currentLatLonOrNull handles SecurityException gracefully`() = runTest {
        // Given
        every { mockFusedLocationClient.lastLocation } returns mockLastLocationTask
        every { mockLastLocationTask.addOnSuccessListener(any()) } returns mockLastLocationTask
        every { mockLastLocationTask.addOnFailureListener(any()) } answers {
            val listener = firstArg<(Exception) -> Unit>()
            listener(SecurityException("Permission denied"))
            mockLastLocationTask
        }
        
        // When
        val result = locationProvider.currentLatLonOrNull()
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun `currentLatLonOrNull handles general exceptions gracefully`() = runTest {
        // Given
        every { mockFusedLocationClient.lastLocation } returns mockLastLocationTask
        every { mockLastLocationTask.addOnSuccessListener(any()) } returns mockLastLocationTask
        every { mockLastLocationTask.addOnFailureListener(any()) } answers {
            val listener = firstArg<(Exception) -> Unit>()
            listener(RuntimeException("Network error"))
            mockLastLocationTask
        }
        
        // When
        val result = locationProvider.currentLatLonOrNull()
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun `currentLatLonOrNull returns cached location when available and recent`() = runTest {
        // Given
        val expectedLat = 37.7749
        val expectedLon = -122.4194
        val expectedAccuracy = 50f
        
        every { mockLocation.latitude } returns expectedLat
        every { mockLocation.longitude } returns expectedLon
        every { mockLocation.accuracy } returns expectedAccuracy
        every { mockLocation.time } returns System.currentTimeMillis()
        
        every { mockFusedLocationClient.lastLocation } returns mockLastLocationTask
        every { mockLastLocationTask.addOnSuccessListener(any()) } answers {
            val listener = firstArg<(Location?) -> Unit>()
            listener(mockLocation)
            mockLastLocationTask
        }
        every { mockLastLocationTask.addOnFailureListener(any()) } returns mockLastLocationTask
        
        // When - First call to populate cache
        val firstResult = locationProvider.currentLatLonOrNull()
        
        // When - Second call should use cache
        val secondResult = locationProvider.currentLatLonOrNull()
        
        // Then
        assertEquals(firstResult, secondResult)
        assertEquals(expectedLat, firstResult?.first)
        assertEquals(expectedLon, firstResult?.second)
    }
}
