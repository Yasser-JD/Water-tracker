package com.jdcoding.watertracker.ui.main.goals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jdcoding.watertracker.data.WaterTrackerRepository
import com.jdcoding.watertracker.model.Goal
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

/**
 * ViewModel for the Goals Fragment
 */
class GoalViewModel(private val repository: WaterTrackerRepository) : ViewModel() {
    
    private val _currentGoal = MutableLiveData<Goal>()
    val currentGoal: LiveData<Goal> = _currentGoal

    private val _goalHistory = MutableLiveData<List<Goal>>()
    val goalHistory: LiveData<List<Goal>> = _goalHistory

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    /**
     * Loads the user's current goal
     */
    fun loadCurrentGoal(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getCurrentGoal(userId).collectLatest { goal ->
                    _currentGoal.value = goal
                }
            } catch (e: Exception) {
                _error.value = "Failed to load current goal: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Loads the user's goal history
     */
    fun loadGoalHistory(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getGoalHistory(userId).collectLatest { goals ->
                    _goalHistory.value = goals.sortedByDescending { it.createdAt }
                }
            } catch (e: Exception) {
                _error.value = "Failed to load goal history: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Creates a new goal for the user
     */
    fun createGoal(userId: Long, targetAmount: Int) {
        viewModelScope.launch {
            // First, deactivate current goal if it exists
            _currentGoal.value?.let { currentGoal ->
                if (currentGoal.isActive) {
                    val updatedGoal = currentGoal.copy(isActive = false)
                    repository.updateGoal(updatedGoal)
                }
            }
            
            // Create new goal
            val newGoal = Goal(
                userId = userId,
                targetAmount = targetAmount,
                createdAt = Date(),
                isActive = true
            )
            repository.insertGoal(newGoal)
        }
    }

    /**
     * Updates the current goal
     */
    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            repository.updateGoal(goal)
        }
    }

    /**
     * Factory for creating the ViewModel
     */
    class Factory(private val repository: WaterTrackerRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GoalViewModel::class.java)) {
                return GoalViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
