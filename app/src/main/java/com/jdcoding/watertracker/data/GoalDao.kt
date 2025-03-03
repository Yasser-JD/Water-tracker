package com.jdcoding.watertracker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jdcoding.watertracker.model.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert
    suspend fun insertGoal(goal: Goal): Long

    @Update
    suspend fun updateGoal(goal: Goal)

    @Query("SELECT * FROM goals WHERE userId = :userId ORDER BY startDate DESC")
    fun getAllGoalsForUser(userId: Long): LiveData<List<Goal>>
    
    @Query("SELECT * FROM goals WHERE userId = :userId ORDER BY startDate DESC")
    suspend fun getGoalsByUserId(userId: Long): List<Goal>
    
    @Query("SELECT * FROM goals WHERE userId = :userId ORDER BY startDate DESC")
    fun getGoalsHistoryByUserId(userId: Long): Flow<List<Goal>>
    
    @Query("SELECT * FROM goals WHERE userId = :userId AND isCompleted = 0 ORDER BY startDate DESC LIMIT 1")
    fun getCurrentGoalForUser(userId: Long): LiveData<Goal?>
    
    @Query("SELECT * FROM goals WHERE userId = :userId AND isCompleted = 0 ORDER BY startDate DESC LIMIT 1")
    fun getCurrentGoalByUserId(userId: Long): Flow<Goal?>
    
    @Query("SELECT * FROM goals WHERE userId = :userId ORDER BY startDate DESC LIMIT 1")
    suspend fun getLatestGoalByUserId(userId: Long): Goal?

    @Query("UPDATE goals SET isCompleted = :isCompleted WHERE id = :goalId")
    suspend fun updateGoalCompletion(goalId: Long, isCompleted: Boolean)
}
