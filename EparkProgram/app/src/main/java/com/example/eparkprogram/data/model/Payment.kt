package com.example.eparkprogram.data.model

data class Payment(
    val paymentId: Long,
    val amount: Double,
    val currencyCode: String,
    val status: String,
    val receiptNumber: String? = null
)
