package com.example.wisp.data.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database migrations for the Wisp Weather App.
 * 
 * This file contains all database schema migrations to handle
 * database updates between app versions.
 */
object DatabaseMigrations {
    
    /**
     * Migration from version 1 to 2.
     * Example: Adding a new column to places table.
     */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Example migration - add a new column
            // database.execSQL("ALTER TABLE places ADD COLUMN description TEXT")
            
            // For now, this is a placeholder migration
            // In the future, when we need to add new columns or tables,
            // we can implement the actual migration logic here
        }
    }
    
    /**
     * Migration from version 2 to 3.
     * Example: Creating a new table for user preferences.
     */
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Example migration - create a new table
            // database.execSQL("""
            //     CREATE TABLE user_preferences (
            //         id TEXT PRIMARY KEY NOT NULL,
            //         key TEXT NOT NULL,
            //         value TEXT NOT NULL
            //     )
            // """)
            
            // For now, this is a placeholder migration
        }
    }
    
    /**
     * Gets all available migrations.
     * Add new migrations to this list as the database schema evolves.
     */
    fun getAllMigrations(): Array<Migration> {
        return arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3
        )
    }
}
