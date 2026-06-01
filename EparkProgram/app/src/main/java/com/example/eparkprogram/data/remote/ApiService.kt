package com.example.eparkprogram.data.remote

import com.example.eparkprogram.data.model.Fine
import com.example.eparkprogram.data.model.FinishedSession
import com.example.eparkprogram.data.model.ParkingSession
import com.example.eparkprogram.data.model.ParkingZone
import com.example.eparkprogram.data.model.User
import com.example.eparkprogram.data.model.Vehicle
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register-driver")
    suspend fun registerDriver(@Body request: RegisterDriverRequest): AuthResponse

    @GET("municipalities")
    suspend fun getMunicipalities(): List<MunicipalityDto>

    @GET("zones")
    suspend fun getZones(@Query("municipalityId") municipalityId: Int? = null): List<ParkingZone>

    @GET("driver/home")
    suspend fun getDriverHome(): DriverHomeResponse

    @GET("driver/vehicles")
    suspend fun getVehicles(): List<Vehicle>

    @GET("driver/sessions/active")
    suspend fun getActiveSession(): ParkingSession?

    @POST("driver/sessions/start")
    suspend fun startSession(@Body request: StartSessionRequest): StartSessionResponse

    @POST("driver/sessions/{id}/finish")
    suspend fun finishSession(@Path("id") sessionId: Long): FinishedSession

    @GET("driver/fines")
    suspend fun getFines(): List<Fine>

    @GET("driver/sessions/history")
    suspend fun getSessionHistory(): List<ParkingSession>

    @GET("admin/reports/summary")
    suspend fun getAdminReportSummary(): AdminReportSummary
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterDriverRequest(
    val fullName: String,
    val nationalId: String,
    val email: String,
    val password: String,
    val vehiclePlate: String
)

data class AuthResponse(
    val token: String,
    val user: User
)

data class MunicipalityDto(
    val municipalityId: Int,
    val municipalityName: String,
    val province: String?
)

data class DriverHomeResponse(
    val fullName: String,
    val primaryVehicle: Vehicle?,
    val activeSession: ParkingSession?
)

data class StartSessionRequest(
    val vehicleId: Int,
    val zoneId: Int,
    val spaceCode: String
)

data class StartSessionResponse(
    val sessionId: Long
)

data class AdminReportSummary(
    val totalZones: Int,
    val activeSessions: Int,
    val revenue: Double
)
