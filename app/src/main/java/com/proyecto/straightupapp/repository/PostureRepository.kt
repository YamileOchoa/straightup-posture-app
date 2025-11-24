package com.proyecto.straightupapp.repository

import android.content.Context
import com.proyecto.straightupapp.data.AppDatabase
import com.proyecto.straightupapp.data.PostureEvent
import java.util.*

class PostureRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val postureDao = database.postureDao()

    suspend fun insertPostureEvent(event: PostureEvent): Long {
        return postureDao.insert(event)
    }

    suspend fun getEventsSince(startDate: Date): List<PostureEvent> {
        return postureDao.getEventsSince(startDate)
    }

    suspend fun getEventsInRange(startDate: Date, endDate: Date): List<PostureEvent> {
        return postureDao.getEventsInRange(startDate, endDate)
    }

    suspend fun getRecentEvents(): List<PostureEvent> {
        return postureDao.getRecentEvents()
    }

    suspend fun deleteOldEvents(date: Date) {
        postureDao.deleteOldEvents(date)
    }

    suspend fun deleteAll() {
        postureDao.deleteAll()
    }
}
