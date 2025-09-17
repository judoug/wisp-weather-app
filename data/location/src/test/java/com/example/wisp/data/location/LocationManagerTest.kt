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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for LocationManager.
 * 
 * Tests:
 * - Permission checking methods
 * - Location retrieval methods
 * - Distance calculations
 * - Location accuracy checks
 */
class LocationManagerTest {

    private lateinit var mockContext: Context
    private lateinit var mockFusedLocationClient: FusedLocationProviderClient
    private lateinit var mockLastLocationTask: Task<Location>
    private lateinit var mockCurrentLocationTask: Task<Location>
    private lateinit var mockLocation: Location
    
    private lateinit var locationManager: LocationManager
    
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
        
        // Setup location manager
        locationManager = LocationManager(mockContext)
    }
    
    @After
    fun tearDown() {
        unmockkAll()
    }
    
    @Test
    fun `hasLocationPermission returns true when fine location permission granted`() {
        // Given
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
        } returns PackageManager.PERMISSION_DENIED
        
        // When
        val result = locationManager.hasLocationPermission()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `hasLocationPermission returns true when coarse location permission granted`() {
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
        } returns PackageManager.PERMISSION_GRANTED
        
        // When
        val result = locationManager.hasLocationPermission()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `hasLocationPermission returns false when no location permissions granted`() {
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
        val result = locationManager.hasLocationPermission()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `hasFineLocationPermission returns true when fine location permission granted`() {
        // Given
        every { 
            androidx.core.content.ContextCompat.checkSelfPermission(
                any(), 
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) 
        } returns PackageManager.PERMISSION_GRANTED
        
        // When
        val result = locationManager.hasFineLocationPermission()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `hasFineLocationPermission returns false when fine location permission denied`() {
        // Given
        every { 
            androidx.core.content.ContextCompat.checkSelfPermission(
                any(), 
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) 
        } returns PackageManager.PERMISSION_DENIED
        
        // When
        val result = locationManager.hasFineLocationPermission()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `getLastKnownLocation returns location when permission granted and location available`() = runTest {
        // Given
        val expectedLat = 37.7749
        val expectedLon = -122.4194
        
        every { mockLocation.latitude } returns expectedLat
        every { mockLocation.longitude } returns expectedLon
        
        every { 
            androidx.core.content.ContextCompat.checkSelfPermission(
                any(), 
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) 
        } returns PackageManager.PERMISSION_GRANTED
        
        every { mockFusedLocationClient.lastLocation } returns mockLastLocationTask
        every { mockLastLocationTask.addOnSuccessListener(any()) } answers {
            val listener = firstArg<(Location?) -> Unit>()
            listener(mockLocation)
            mockLastLocationTask
        }
        every { mockLastLocationTask.addOnFailureListener(any()) } returns mockLastLocationTask
        
        // When
        val result = locationManager.getLastKnownLocation()
        
        // Then
        assertEquals(expectedLat, result?.latitude)
        assertEquals(expectedLon, result?.longitude)
    }
    
    @Test
    fun `getLastKnownLocation throws exception when permission denied`() = runTest {
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
        
        // When & Then
        try {
            locationManager.getLastKnownLocation()
            assert(false) { "Expected LocationUnavailableException" }
        } catch (e: LocationUnavailableException) {
            assertEquals("Location permission not granted", e.message)
        }
    }
    
    @Test
    fun `getLastKnownLocation returns null when location is null`() = runTest {
        // Given
        every { 
            androidx.core.content.ContextCompat.checkSelfPermission(
                any(), 
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) 
        } returns PackageManager.PERMISSION_GRANTED
        
        every { mockFusedLocationClient.lastLocation } returns mockLastLocationTask
        every { mockLastLocationTask.addOnSuccessListener(any()) } answers {
            val listener = firstArg<(Location?) -> Unit>()
            listener(null)
            mockLastLocationTask
        }
        every { mockLastLocationTask.addOnFailureListener(any()) } returns mockLastLocationTask
        
        // When
        val result = locationManager.getLastKnownLocation()
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun `calculateDistance returns correct distance between two points`() {
        // Given - San Francisco to Los Angeles (approximately 559 km)
        val sfLat = 37.7749
        val sfLon = -122.4194
        val laLat = 34.0522
        val laLon = -118.2437
        
        // When
        val distance = locationManager.calculateDistance(sfLat, sfLon, laLat, laLon)
        
        // Then - Allow for some margin of error in distance calculation
        assertTrue(distance > 550_000) // At least 550 km
        assertTrue(distance < 570_000) // At most 570 km
    }
    
    @Test
    fun `calculateDistance returns zero for same coordinates`() {
        // Given
        val lat = 37.7749
        val lon = -122.4194
        
        // When
        val distance = locationManager.calculateDistance(lat, lon, lat, lon)
        
        // Then
        assertEquals(0f, distance)
    }
    
    @Test
    fun `isWithinRadius returns true when locations are within radius`() {
        // Given - Two points approximately 100 meters apart
        val lat1 = 37.7749
        val lon1 = -122.4194
        val lat2 = 37.7750 // Slightly north
        val lon2 = -122.4194
        val radiusMeters = 200f
        
        // When
        val result = locationManager.isWithinRadius(lat1, lon1, lat2, lon2, radiusMeters)
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `isWithinRadius returns false when locations are outside radius`() {
        // Given - Two points far apart
        val sfLat = 37.7749
        val sfLon = -122.4194
        val laLat = 34.0522
        val laLon = -118.2437
        val radiusMeters = 1000f // 1 km radius
        
        // When
        val result = locationManager.isWithinRadius(sfLat, sfLon, laLat, laLon, radiusMeters)
        
        // Then
        assertFalse(result)
    }
}
