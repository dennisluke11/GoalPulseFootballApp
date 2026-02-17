package com.example.goalpulse.data.repository

object SeasonCalculator {
    fun getCurrentSeason(): Int {
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
        val calculatedSeason = if (currentMonth >= 6) currentYear else currentYear - 1
        
        return when {
            calculatedSeason >= 2022 && calculatedSeason <= 2024 -> calculatedSeason
            calculatedSeason > 2024 -> 2024
            else -> 2024
        }
    }
}

