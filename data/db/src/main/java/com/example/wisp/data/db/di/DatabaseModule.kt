package com.example.wisp.data.db.di

import android.content.Context
import com.example.wisp.data.db.WispDatabase
import com.example.wisp.data.db.dao.PlaceDao
import com.example.wisp.data.db.dao.WeatherDao
import com.example.wisp.data.db.repository.DatabaseWeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for database dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideWispDatabase(@ApplicationContext context: Context): WispDatabase {
        return WispDatabase.getDatabase(context)
    }
    
    @Provides
    fun providePlaceDao(database: WispDatabase): PlaceDao {
        return database.placeDao()
    }
    
    @Provides
    fun provideWeatherDao(database: WispDatabase): WeatherDao {
        return database.weatherDao()
    }
    
    @Provides
    @Singleton
    fun provideDatabaseWeatherRepository(
        database: WispDatabase
    ): DatabaseWeatherRepository {
        return DatabaseWeatherRepository(database)
    }
}
