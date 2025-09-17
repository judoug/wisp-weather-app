package com.example.wisp.data.db

import androidx.room.TypeConverter

/**
 * Room type converters for complex data types.
 * Currently empty as all our data types are primitive.
 * Add converters here when needed for complex types like List<String>.
 */
class Converters {
    
    // Add type converters here if needed for complex types
    // For now, all our data types are primitive and don't need conversion
    
    // Example converter for List<String>:
    // @TypeConverter
    // fun fromStringList(value: List<String>): String {
    //     return value.joinToString(",")
    // }
    //
    // @TypeConverter
    // fun toStringList(value: String): List<String> {
    //     return value.split(",")
    // }
}
