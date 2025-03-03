package com.jdcoding.watertracker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jdcoding.watertracker.model.WaterIntake
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface WaterIntakeDao {
    @Insert
    suspend fun insertWaterIntake(waterIntake: WaterIntake): Long
    
    @Update
    suspend fun updateWaterIntake(waterIntake: WaterIntake)
    
    @Delete
    suspend fun deleteWaterIntake(waterIntake: WaterIntake)

    @Query("SELECT * FROM water_intakes WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllWaterIntakesForUser(userId: Long): LiveData<List<WaterIntake>>
    
    @Query("SELECT * FROM water_intakes WHERE userId = :userId AND timestamp BETWEEN :startDate AND :endDate")
    suspend fun getWaterIntakeByDateRange(userId: Long, startDate: Date, endDate: Date): List<WaterIntake>
    
    @Query("SELECT * FROM water_intakes WHERE userId = :userId AND timestamp BETWEEN :startDate AND :endDate")
    fun getWaterIntakeByDateRangeFlow(userId: Long, startDate: Date, endDate: Date): Flow<List<WaterIntake>>
    
    @Query("SELECT SUM(amount) FROM water_intakes WHERE userId = :userId AND timestamp BETWEEN :startDate AND :endDate")
    suspend fun getWaterIntakeAmountByDateRange(userId: Long, startDate: Date, endDate: Date): Int

    @Query("SELECT * FROM water_intakes WHERE userId = :userId AND timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun getWaterIntakesForUserInRange(
        userId: Long,
        startDate: Date,
        endDate: Date
    ): LiveData<List<WaterIntake>>

    @Query("SELECT SUM(amount) FROM water_intakes WHERE userId = :userId AND timestamp BETWEEN :startDate AND :endDate")
    suspend fun getTotalWaterIntakeForUserInRange(
        userId: Long,
        startDate: Date,
        endDate: Date
    ): Int?

    @Query("SELECT SUM(amount) FROM water_intakes WHERE userId = :userId AND timestamp BETWEEN :startDate AND :endDate GROUP BY strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')")
    fun getDailyWaterIntakeForUserInRange(
        userId: Long,
        startDate: Date,
        endDate: Date
    ): LiveData<List<Int>>
}
