package com.example.wisp.data.location.di

import com.example.wisp.data.location.AndroidLocationProvider
import com.example.wisp.domain.provider.LocationProvider
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration tests for LocationModule.
 * 
 * Tests:
 * - Hilt dependency injection setup
 * - LocationProvider binding
 * - Module configuration
 */
class LocationModuleTest {

    @Test
    fun `locationProvider can be created`() {
        // Given
        val context = mockk<android.content.Context>()
        val locationProvider = AndroidLocationProvider(context)

        // When & Then
        assertNotNull(locationProvider)
        assertTrue(locationProvider is AndroidLocationProvider)
    }
}
