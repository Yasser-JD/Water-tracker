package com.jdcoding.watertracker.model

/**
 * Data class that represents a hydration tip to be displayed to the user
 */
data class HydrationTip(
    val id: Int,
    val title: String,
    val content: String,
    val imageResource: Int? = null,
    val category: TipCategory = TipCategory.GENERAL
)

/**
 * Enum class that represents different categories of hydration tips
 */
enum class TipCategory {
    GENERAL,
    HEALTH_BENEFITS,
    DAILY_HABITS,
    EXERCISE,
    NUTRITION
}
