package com.example.triplog.network

import com.example.triplog.data.LoginRegistrationResult
import com.example.triplog.data.LoginRequest
import com.example.triplog.data.LoginResult
import com.example.triplog.data.RegistrationRequest
import com.example.triplog.data.RegistrationResult

interface InterfaceRepository {
    suspend fun getLoginResult(request: LoginRequest): LoginResult
    suspend fun getRegistrationResult(request: RegistrationRequest): RegistrationResult
}

class Repository(private val tripLogApiService: TripLogApiService) : InterfaceRepository {

    override suspend fun getLoginResult(request: LoginRequest): LoginResult =
        tripLogApiService.getLoginResult(request)

    override suspend fun getRegistrationResult(request: RegistrationRequest): RegistrationResult =
        tripLogApiService.getRegistrationResult(request)
}