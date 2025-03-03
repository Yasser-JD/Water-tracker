package com.jdcoding.watertracker.ui.main.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jdcoding.watertracker.data.WaterTrackerRepository
import com.jdcoding.watertracker.model.Goal
import com.jdcoding.watertracker.model.WaterIntake
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

/**
 * ViewModel for the Dashboard Fragment
 */
class DashboardViewModel(private val repository: WaterTrackerRepository) : ViewModel() {

    private val _todayIntakes = MutableLiveData<List<WaterIntake>>()
    val todayIntakes: LiveData<List<WaterIntake>> = _todayIntakes

    private val _currentGoal = MutableLiveData<Goal?>()
    val currentGoal: LiveData<Goal?> = _currentGoal

    private val _currentDayTotal = MutableLiveData(0)
    val currentDayTotal: LiveData<Int> = _currentDayTotal

    private val _progressPercentage = MutableLiveData(0)
    val progressPercentage: LiveData<Int> = _progressPercentage

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    /**
     * Loads user's water intake data for today
     */
    fun loadTodayIntakes(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Calculate start and end of today
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

                // Get today's intakes
                repository.getWaterIntakeByDateRange(userId, startOfDay, endOfDay).collectLatest { intakes ->
                    _todayIntakes.value = intakes.sortedByDescending { it.timestamp }
                    
                    // Calculate total intake for today
                    val total = intakes.sumOf { it.amount }
                    _currentDayTotal.value = total
                    
                    // Update progress
                    updateProgress(total)
                }
            } catch (e: Exception) {
                _error.value = "Failed to load water intakes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Loads user's current goal
     */
    fun loadCurrentGoal(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getCurrentGoal(userId).collectLatest { goal ->
                    _currentGoal.value = goal
                    goal?.let {
                        _currentDayTotal.value?.let { total ->
                            updateProgress(total)
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to load goal: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Adds a new water intake record
     */
    fun addWaterIntake(userId: Long, amount: Int) {
        viewModelScope.launch {
            try {
                // Create a new water intake with current timestamp
                val waterIntake = WaterIntake(
                    userId = userId,
                    amount = amount,
                    timestamp = Date()
                )
                
                // Insert the water intake
                repository.insertWaterIntake(waterIntake)
                
                // Refresh today's intakes
                loadTodayIntakes(userId)
            } catch (e: Exception) {
                _error.value = "Failed to add water intake: ${e.message}"
            }
        }
    }

    /**
     * Deletes a water intake record
     */
    fun deleteWaterIntake(waterIntake: WaterIntake) {
        viewModelScope.launch {
            try {
                repository.deleteWaterIntake(waterIntake)
                
                // Refresh today's intakes for the user
                waterIntake.userId?.let { userId ->
                    loadTodayIntakes(userId)
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete water intake: ${e.message}"
            }
        }
    }

    /**
     * Updates progress percentage based on current intake and goal
     */
    private fun updateProgress(totalIntake: Int) {
        val goal = _currentGoal.value?.targetAmount ?: 2000 // Default 2000ml if no goal set
        val percentage = (totalIntake * 100 / goal).coerceIn(0, 100)
        _progressPercentage.value = percentage
    }
    
    /**
     * Factory for creating a [DashboardViewModel] with a constructor that takes a [WaterTrackerRepository]
     */
    class Factory(private val repository: WaterTrackerRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                return DashboardViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
