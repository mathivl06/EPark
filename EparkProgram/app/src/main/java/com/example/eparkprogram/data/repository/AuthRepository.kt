package com.example.eparkprogram.data.repository

import com.example.eparkprogram.data.model.User
import com.example.eparkprogram.data.remote.AuthSession
import com.example.eparkprogram.data.remote.LoginRequest
import com.example.eparkprogram.data.remote.RegisterDriverRequest
import com.example.eparkprogram.data.remote.RetrofitClient

class AuthRepository(
    private val api: com.example.eparkprogram.data.remote.ApiService = RetrofitClient.api
) {
    suspend fun login(email: String, password: String): User {
        val response = api.login(LoginRequest(email.trim().lowercase(), password))
        AuthSession.token = response.token
        AuthSession.userRole = response.user.roleCode
        AuthSession.fullName = response.user.fullName
        return response.user
    }

    suspend fun registerDriver(
        fullName: String,
        nationalId: String,
        email: String,
        password: String,
        vehiclePlate: String
    ): User {
        val response = api.registerDriver(
            RegisterDriverRequest(
                fullName = fullName.trim(),
                nationalId = nationalId.trim(),
                email = email.trim().lowercase(),
                password = password,
                vehiclePlate = vehiclePlate.trim().uppercase()
            )
        )
        AuthSession.token = response.token
        AuthSession.userRole = response.user.roleCode
        AuthSession.fullName = response.user.fullName
        return response.user
    }
}
