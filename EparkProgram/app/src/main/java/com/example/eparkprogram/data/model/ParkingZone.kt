package com.example.eparkprogram.data.model

data class ParkingZone(
    val zoneId: Int,
    val municipalityId: Int,
    val municipalityName: String,
    val zoneName: String,
    val description: String? = null,
    val latitude: Double,
    val longitude: Double,
    val operationStartTime: String,
    val operationEndTime: String,
    val totalSpaces: Int,
    val status: String,
    val availableSpaces: Int,
    val hourlyRate: Double,
    val currencyCode: String
)
