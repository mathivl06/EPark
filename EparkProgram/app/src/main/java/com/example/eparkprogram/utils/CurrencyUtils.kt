package com.example.eparkprogram.utils

object CurrencyUtils {
    fun formatColones(amount: Double): String {
        return "₡${String.format("%,.0f", amount)}"
    }
    fun calculateCost(elapsedSeconds: Long, hourlyRate: Double): Double {
        return (elapsedSeconds / 3600.0) * hourlyRate
    }
}