package com.example.wisp.data.weather.di

import com.example.wisp.data.db.repository.DatabaseWeatherRepository
import com.example.wisp.data.weather.network.NetworkConnectivityManager
import com.example.wisp.data.weather.repository.WeatherDataRepository
import com.example.wisp.data.weather.service.ApiKeyInterceptor
import com.example.wisp.data.weather.service.OpenWeatherProvider
import com.example.wisp.data.weather.service.OpenWeatherService
import com.example.wisp.data.weather.sync.WeatherDataSyncService
import com.example.wisp.domain.provider.WeatherProvider
import com.example.wisp.domain.repository.WeatherRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Hilt module for weather data layer dependencies.
 * Provides network configuration, API services, and data providers.
 */
@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {
    
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    
    @Provides
    @Singleton
    @Named("openweather_api_key")
    fun provideApiKey(): String {
        // This will be injected from BuildConfig in the app module
        return "3e54101974619d8e984be198561efcc5"
    }
    
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        apiKeyInterceptor: ApiKeyInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
    
    @Provides
    @Singleton
    fun provideOpenWeatherService(retrofit: Retrofit): OpenWeatherService {
        return retrofit.create(OpenWeatherService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideWeatherProvider(
        openWeatherProvider: OpenWeatherProvider
    ): WeatherProvider {
        return openWeatherProvider
    }
    
    @Provides
    @Singleton
    fun provideNetworkConnectivityManager(
        @ApplicationContext context: android.content.Context
    ): NetworkConnectivityManager {
        return NetworkConnectivityManager(context)
    }
    
    @Provides
    @Singleton
    fun provideWeatherDataSyncService(
        weatherDataRepository: WeatherDataRepository,
        networkConnectivityManager: NetworkConnectivityManager,
        databaseRepository: DatabaseWeatherRepository
    ): WeatherDataSyncService {
        return WeatherDataSyncService(weatherDataRepository, networkConnectivityManager, databaseRepository)
    }
    
    @Provides
    @Singleton
    fun provideWeatherRepository(
        weatherDataRepository: WeatherDataRepository
    ): WeatherRepository {
        return weatherDataRepository
    }
}

