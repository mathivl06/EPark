package com.example.eparkprogram.data.model

data class Fine(
    val fineId: Long,
    val fineNumber: String,
    val reason: String,
    val fineDate: String,
    val amount: Double,
    val status: String
)
