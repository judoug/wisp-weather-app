package com.example.wisp.data.location.di

import com.example.wisp.data.location.IpGeoLocationProvider
import com.example.wisp.data.location.ManualLocationProvider
import com.example.wisp.data.location.QuestLocationProvider
import com.example.wisp.domain.provider.LocationProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Quest-specific location dependencies.
 * 
 * Provides:
 * - QuestLocationProvider as the implementation of LocationProvider
 * - Manual and IP geolocation providers
 * - Quest-specific location configuration
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class QuestLocationModule {

    /**
     * Binds QuestLocationProvider as the implementation of LocationProvider interface.
     * This is used for the Quest flavor build.
     */
    @Binds
    @Singleton
    abstract fun bindLocationProvider(
        questLocationProvider: QuestLocationProvider
    ): LocationProvider

    companion object {
        /**
         * Provides ManualLocationProvider instance.
         */
        @Provides
        @Singleton
        fun provideManualLocationProvider(
            manualLocationProvider: ManualLocationProvider
        ): ManualLocationProvider = manualLocationProvider

        /**
         * Provides IpGeoLocationProvider instance.
         */
        @Provides
        @Singleton
        fun provideIpGeoLocationProvider(
            ipGeoLocationProvider: IpGeoLocationProvider
        ): IpGeoLocationProvider = ipGeoLocationProvider
    }
}
