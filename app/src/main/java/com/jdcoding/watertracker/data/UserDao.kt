package com.jdcoding.watertracker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jdcoding.watertracker.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User table operations
 */
@Dao
interface UserDao {
    /**
     * Insert a new user into the database
     * @return the rowId of the inserted user
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    /**
     * Update existing user details
     */
    @Update
    suspend fun updateUser(user: User)

    /**
     * Delete a user from the database
     */
    @Delete
    suspend fun deleteUser(user: User)

    /**
     * Get user by id as LiveData for reactive UI updates
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdLive(userId: Long): LiveData<User?>

    /**
     * Get user by id (suspending function)
     */
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): User?

    /**
     * Get user by email (suspending function)
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?
    
    /**
     * Check if an email already exists (used during registration)
     */
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun checkEmailExists(email: String): Int
    
    /**
     * Check if a username already exists (used during registration)
     */
    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    suspend fun checkUsernameExists(username: String): Int

    /**
     * Get user by username
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?
    
    /**
     * Used for login authentication
     */
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?

    /**
     * Update the daily water goal for a user
     */
    @Query("UPDATE users SET dailyWaterGoal = :goal WHERE id = :userId")
    suspend fun updateDailyGoal(userId: Long, goal: Int)
    
    /**
     * Update a user's password
     */
    @Query("UPDATE users SET password = :newPassword WHERE id = :userId")
    suspend fun updatePassword(userId: Long, newPassword: String)
    
    /**
     * Get all users as a list
     */
    @Query("SELECT * FROM users ORDER BY id DESC")
    suspend fun getAllUsers(): List<User>
    
    /**
     * Get all users as a Flow for reactive updates
     */
    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsersFlow(): Flow<List<User>>
}
