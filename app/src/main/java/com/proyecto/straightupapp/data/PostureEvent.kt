package com.proyecto.straightupapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.*

@Entity(tableName = "posture_events")
@TypeConverters(DateConverter::class)
data class PostureEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Date,
    val eventType: String, // "BAD_POSTURE" o "GOOD_POSTURE"
    val duration: Long = 0 // duración en milisegundos
)

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}