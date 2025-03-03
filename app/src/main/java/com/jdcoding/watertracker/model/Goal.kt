package com.jdcoding.watertracker.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "goals",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val targetAmount: Int, // in ml
    val startDate: Date = Date(),
    val endDate: Date? = null, // null means ongoing
    val isCompleted: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Date = Date()
)
