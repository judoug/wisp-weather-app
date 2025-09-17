package com.example.wisp.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.wisp.data.db.dao.PlaceDao
import com.example.wisp.data.db.dao.WeatherDao
import com.example.wisp.data.db.entity.PlaceEntity
import com.example.wisp.data.db.entity.WeatherNowEntity
import com.example.wisp.data.db.entity.WeatherHourlyEntity
import com.example.wisp.data.db.entity.WeatherDailyEntity
import com.example.wisp.data.db.migration.DatabaseMigrations

/**
 * Room database for the Wisp Weather App.
 * 
 * Version 1: Initial schema with places and weather data tables
 */
@Database(
    entities = [
        PlaceEntity::class,
        WeatherNowEntity::class,
        WeatherHourlyEntity::class,
        WeatherDailyEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WispDatabase : RoomDatabase() {
    
    abstract fun placeDao(): PlaceDao
    abstract fun weatherDao(): WeatherDao
    
    companion object {
        const val DATABASE_NAME = "wisp_database"
        
        @Volatile
        private var INSTANCE: WispDatabase? = null
        
        fun getDatabase(context: Context): WispDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WispDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(*DatabaseMigrations.getAllMigrations())
                    .fallbackToDestructiveMigration() // For development - remove in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
