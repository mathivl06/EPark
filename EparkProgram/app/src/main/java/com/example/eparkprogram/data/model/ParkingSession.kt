package com.example.eparkprogram.data.model

data class ParkingSession(
    val sessionId: Long,
    val zoneName: String,
    val spaceCode: String,
    val plateNumber: String? = null,
    val startedAt: String,
    val hourlyRateApplied: Double,
    val status: String,
    val elapsedMinutes: Int? = null,
    val totalAmount: Double? = null,
    val syncedFromDevice: Boolean? = false
)

data class FinishedSession(
    val sessionId: Long,
    val elapsedMinutes: Int,
    val totalAmount: Double
)
