package com.example.eparkprogram.data.repository

import com.example.eparkprogram.data.model.Fine
import com.example.eparkprogram.data.model.FinishedSession
import com.example.eparkprogram.data.model.ParkingSession
import com.example.eparkprogram.data.model.Vehicle
import com.example.eparkprogram.data.remote.DriverHomeResponse
import com.example.eparkprogram.data.remote.RetrofitClient
import com.example.eparkprogram.data.remote.SessionHistoryDto
import com.example.eparkprogram.data.remote.StartSessionRequest

class ParkingRepository(
    private val api: com.example.eparkprogram.data.remote.ApiService = RetrofitClient.api
) {
    suspend fun getDriverHome(): DriverHomeResponse = api.getDriverHome()

    suspend fun getVehicles(): List<Vehicle> = api.getVehicles()

    suspend fun getActiveSession(): ParkingSession? = api.getActiveSession()

    suspend fun startSession(vehicleId: Int, zoneId: Int, spaceCode: String): Long =
        api.startSession(StartSessionRequest(vehicleId, zoneId, spaceCode)).sessionId

    suspend fun finishSession(sessionId: Long): FinishedSession =
        api.finishSession(sessionId)

    suspend fun getFines(): List<Fine> = api.getFines()

    suspend fun getSessionHistory(): List<SessionHistoryDto> = api.getSessionHistory()
}
