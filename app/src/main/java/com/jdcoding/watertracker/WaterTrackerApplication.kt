package com.jdcoding.watertracker

import android.app.Application
import com.jdcoding.watertracker.data.WaterTrackerDatabase
import com.jdcoding.watertracker.data.WaterTrackerRepository
import com.jdcoding.watertracker.utils.SessionManager

class WaterTrackerApplication : Application() {
    // Lazy initialization of the database
    private val database by lazy { WaterTrackerDatabase.getInstance(this) }
    
    // Repository with access to the DAOs
    val repository by lazy { 
        WaterTrackerRepository(
            database.userDao(), 
            database.waterIntakeDao(), 
            database.goalDao()
        ) 
    }
    
    // Session manager for handling user sessions
    val sessionManager by lazy { SessionManager(this) }
    
    override fun onCreate() {
        super.onCreate()
        // Initialize WorkManager or other app-wide configuration here
    }
}
