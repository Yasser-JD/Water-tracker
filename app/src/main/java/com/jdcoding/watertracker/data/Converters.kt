package com.jdcoding.watertracker.data

import androidx.room.TypeConverter
import java.util.Date

/**
 * Type converters for Room database to convert complex data types to and from primitive types
 * that Room can store.
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
