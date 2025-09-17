package com.example.wisp.data.location.di

import com.example.wisp.data.location.AndroidLocationProvider
import com.example.wisp.domain.provider.LocationProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for location-related dependencies.
 * 
 * Provides:
 * - AndroidLocationProvider as the implementation of LocationProvider
 * - Location services configuration
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    /**
     * Binds AndroidLocationProvider as the implementation of LocationProvider interface.
     */
    @Binds
    @Singleton
    abstract fun bindLocationProvider(
        androidLocationProvider: AndroidLocationProvider
    ): LocationProvider
}
