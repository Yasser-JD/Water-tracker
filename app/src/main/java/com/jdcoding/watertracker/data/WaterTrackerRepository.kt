package com.jdcoding.watertracker.data

import androidx.lifecycle.LiveData
import com.jdcoding.watertracker.model.Goal
import com.jdcoding.watertracker.model.User
import com.jdcoding.watertracker.model.WaterIntake
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.Date

/**
 * Repository class that provides a clean API for data access to the rest of the application.
 */
class WaterTrackerRepository(
    private val userDao: UserDao,
    private val waterIntakeDao: WaterIntakeDao,
    private val goalDao: GoalDao
) {
    // User related operations
    
    /**
     * Insert a new user and return their ID
     */
    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }
    
    /**
     * Check if a user with the given email exists
     */
    suspend fun checkEmailExists(email: String): Boolean {
        return userDao.checkEmailExists(email) > 0
    }
    
    /**
     * Check if a user with the given username exists
     */
    suspend fun checkUsernameExists(username: String): Boolean {
        return userDao.checkUsernameExists(username) > 0
    }
    
    /**
     * Attempt to login with email and password
     */
    suspend fun getUserByEmailAndPassword(email: String, password: String): User? {
        return userDao.getUserByEmailAndPassword(email, password)
    }
    
    /**
     * Get user by ID
     */
    suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)
    }
    
    /**
     * Get user by ID as LiveData
     */
    fun getUserByIdLive(userId: Long): LiveData<User?> {
        return userDao.getUserByIdLive(userId)
    }
    
    /**
     * Update user details
     */
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
    
    /**
     * Update user password
     */
    suspend fun updatePassword(userId: Long, newPassword: String) {
        userDao.updatePassword(userId, newPassword)
    }
    
    // Goal related operations
    
    /**
     * Insert a new hydration goal
     */
    suspend fun insertGoal(goal: Goal): Long {
        return goalDao.insertGoal(goal)
    }
    
    /**
     * Get all goals for a user
     */
    suspend fun getGoalsByUserId(userId: Long): List<Goal> {
        return goalDao.getGoalsByUserId(userId)
    }
    
    /**
     * Get a user's goal history as a Flow
     */
    fun getGoalHistory(userId: Long): Flow<List<Goal>> {
        return goalDao.getGoalsHistoryByUserId(userId)
    }
    
    /**
     * Get a user's current goal as a Flow
     */
    fun getCurrentGoal(userId: Long): Flow<Goal?> {
        return goalDao.getCurrentGoalByUserId(userId)
    }
    
    /**
     * Get the latest goal for a user
     */
    suspend fun getLatestGoal(userId: Long): Goal? {
        return goalDao.getLatestGoalByUserId(userId)
    }
    
    /**
     * Update a goal
     */
    suspend fun updateGoal(goal: Goal) {
        goalDao.updateGoal(goal)
    }
    
    // Water intake related operations
    
    /**
     * Insert a new water intake record
     */
    suspend fun insertWaterIntake(waterIntake: WaterIntake): Long {
        return waterIntakeDao.insertWaterIntake(waterIntake)
    }
    
    /**
     * Get total water intake for today
     */
    suspend fun getTodayWaterIntake(userId: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time
        
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.time
        
        return waterIntakeDao.getWaterIntakeAmountByDateRange(userId, startOfDay, endOfDay)
    }
    
    /**
     * Get water intake records for a specific date
     */
    suspend fun getWaterIntakeByDate(userId: Long, date: Date): List<WaterIntake> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time
        
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.time
        
        return waterIntakeDao.getWaterIntakeByDateRange(userId, startOfDay, endOfDay)
    }
    
    /**
     * Get water intake records for a date range
     */
    fun getWaterIntakeByDateRange(userId: Long, startDate: Date, endDate: Date): Flow<List<WaterIntake>> {
        return waterIntakeDao.getWaterIntakeByDateRangeFlow(userId, startDate, endDate)
    }
    
    /**
     * Delete a water intake record
     */
    suspend fun deleteWaterIntake(waterIntake: WaterIntake) {
        waterIntakeDao.deleteWaterIntake(waterIntake)
    }
}
