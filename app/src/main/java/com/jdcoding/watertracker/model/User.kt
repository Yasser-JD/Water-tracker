package com.jdcoding.watertracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val email: String,
    val password: String, // In a real app, this should be hashed/encrypted
    val dateOfBirth: Date,
    val dailyWaterGoal: Int = 2000, // Default goal: 2000ml
    val createdAt: Date = Date()
)
