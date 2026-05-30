package com.example.eparkprogram.utils

object ValidationUtils {
    fun isValidPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$")
        return regex.matches(password)
    }
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun isValidPlate(plate: String): Boolean {
        val regex = Regex("^[A-Z]{3}-\\d{3,4}$")
        return regex.matches(plate.uppercase())
    }
}