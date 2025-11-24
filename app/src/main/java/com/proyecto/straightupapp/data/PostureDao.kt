package com.proyecto.straightupapp.data

import androidx.room.*
import java.util.*

@Dao
interface PostureDao {
    @Insert
    suspend fun insert(event: PostureEvent): Long

    @Query("SELECT * FROM posture_events WHERE timestamp >= :startDate ORDER BY timestamp DESC")
    suspend fun getEventsSince(startDate: Date): List<PostureEvent>

    @Query("SELECT * FROM posture_events WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    suspend fun getEventsInRange(startDate: Date, endDate: Date): List<PostureEvent>

    @Query("SELECT * FROM posture_events ORDER BY timestamp DESC LIMIT 100")
    suspend fun getRecentEvents(): List<PostureEvent>

    @Query("DELETE FROM posture_events WHERE timestamp < :date")
    suspend fun deleteOldEvents(date: Date)

    @Query("DELETE FROM posture_events")
    suspend fun deleteAll()
}
