package com.jdcoding.watertracker.ui.main.tips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jdcoding.watertracker.R
import com.jdcoding.watertracker.data.WaterTrackerRepository
import com.jdcoding.watertracker.model.HydrationTip
import com.jdcoding.watertracker.model.TipCategory

/**
 * ViewModel for the Tips Fragment
 */
class TipsViewModel : ViewModel() {

    // All tips
    private val _allTips = MutableLiveData<List<HydrationTip>>()
    val allTips: LiveData<List<HydrationTip>> = _allTips
    
    // Filtered tips
    private val _filteredTips = MutableLiveData<List<HydrationTip>>()
    val filteredTips: LiveData<List<HydrationTip>> = _filteredTips
    
    // Currently selected category
    private val _selectedCategory = MutableLiveData<TipCategory?>()
    val selectedCategory: LiveData<TipCategory?> = _selectedCategory

    init {
        loadTips()
        _selectedCategory.value = null
        _filteredTips.value = _allTips.value
    }

    /**
     * Loads all hydration tips
     */
    private fun loadTips() {
        val tipsList = listOf(
            HydrationTip(
                id = 1,
                title = "Drink Before You're Thirsty",
                content = "By the time you feel thirsty, you're already mildly dehydrated. Make it a habit to drink water regularly throughout the day to stay ahead of your hydration needs.",
                category = TipCategory.GENERAL,
                imageResource = R.drawable.ic_water_glass
            ),
            HydrationTip(
                id = 2,
                title = "Water and Brain Function",
                content = "Even mild dehydration (1-2% of body weight) can impair cognitive performance. Proper hydration enhances concentration, alertness, and short-term memory.",
                category = TipCategory.HEALTH_BENEFITS,
                imageResource = R.drawable.ic_water_glass
            ),
            HydrationTip(
                id = 3,
                title = "Water First Thing in the Morning",
                content = "Drinking a glass of water first thing in the morning helps activate your internal organs and removes toxins before your first meal of the day.",
                category = TipCategory.DAILY_HABITS,
                imageResource = R.drawable.ic_water_glass
            ),
            HydrationTip(
                id = 4,
                title = "Hydration for Exercise",
                content = "Drink about 17-20 oz of water 2-3 hours before exercise, 8 oz 20-30 minutes before exercise, 7-10 oz every 10-20 minutes during exercise, and 8 oz within 30 minutes after exercise.",
                category = TipCategory.EXERCISE,
                imageResource = R.drawable.ic_water_glass
            ),
            HydrationTip(
                id = 5,
                title = "Hydration from Foods",
                content = "About 20% of our daily water intake comes from food. Fruits and vegetables like watermelon, cucumber, strawberries, and lettuce are over 90% water.",
                category = TipCategory.NUTRITION,
                imageResource = R.drawable.ic_water_glass
            ),
            HydrationTip(
                id = 6,
                title = "Carry a Water Bottle",
                content = "Keep a reusable water bottle with you at all times. Having water readily available makes it easier to develop the habit of drinking water throughout the day.",
                category = TipCategory.DAILY_HABITS,
                imageResource = R.drawable.ic_water_glass
            ),
            HydrationTip(
                id = 7,
                title = "Water and Weight Management",
                content = "Drinking water before meals can reduce appetite and increase weight loss. Studies show drinking 500ml (17oz) of water before meals helped dieters consume fewer calories and lose 44% more weight.",
                category = TipCategory.HEALTH_BENEFITS,
                imageResource = R.drawable.ic_water_glass
            ),
            HydrationTip(
                id = 8,
                title = "Water Quality Matters",
                content = "The quality of water you drink is just as important as the quantity. Consider using a water filter if your tap water quality is questionable or has an unpleasant taste.",
                category = TipCategory.GENERAL,
                imageResource = R.drawable.ic_water_glass
            ),
            HydrationTip(
                id = 9,
                title = "Sports Drinks vs Water",
                content = "For most workouts under an hour, water is sufficient for hydration. Only during intense exercise lasting over an hour might electrolyte-enhanced sports drinks be beneficial.",
                category = TipCategory.EXERCISE,
                imageResource = R.drawable.ic_water_glass
            ),
            HydrationTip(
                id = 10,
                title = "Hydration and Skin Health",
                content = "Proper hydration helps maintain skin elasticity and appearance. While not a magic cure for wrinkles, staying hydrated supports overall skin health and function.",
                category = TipCategory.HEALTH_BENEFITS,
                imageResource = R.drawable.ic_water_glass
            )
        )
        
        _allTips.value = tipsList
        _filteredTips.value = tipsList
    }

    /**
     * Filters tips by category
     */
    fun filterByCategory(category: TipCategory?) {
        _selectedCategory.value = category
        
        if (category == null) {
            _filteredTips.value = _allTips.value
        } else {
            _filteredTips.value = _allTips.value?.filter { it.category == category }
        }
    }

    /**
     * Factory for creating the ViewModel
     */
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TipsViewModel::class.java)) {
                return TipsViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
