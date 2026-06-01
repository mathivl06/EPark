package com.example.eparkprogram.data.model

data class User(
    val userId: Int,
    val email: String,
    val fullName: String,
    val roleCode: String,
    val municipalityId: Int? = null
)
