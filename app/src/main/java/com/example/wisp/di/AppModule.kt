package com.example.wisp.di

import com.example.wisp.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

/**
 * App-level DI module that provides configuration values from BuildConfig.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    @Named("openweather_api_key")
    fun provideOpenWeatherApiKey(): String {
        return BuildConfig.OPENWEATHER_API_KEY
    }
}
