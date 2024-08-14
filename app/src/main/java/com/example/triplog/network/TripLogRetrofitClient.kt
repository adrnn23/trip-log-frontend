package com.example.triplog.network

import com.example.triplog.data.LoginRequest
import com.example.triplog.data.LoginResult
import com.example.triplog.data.RegistrationRequest
import com.example.triplog.data.RegistrationResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface TripLogApiService {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/auth/login")
    suspend fun getLoginResult(@Body request: LoginRequest): Response<LoginResult>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/auth/register")
    suspend fun getRegistrationResult(@Body request: RegistrationRequest): Response<RegistrationResult>
}

interface RepositoryContainer {
    val repository: InterfaceRepository
}

class TripLogRetrofitClient : RepositoryContainer {

    private val url = "http://192.168.0.x/"

    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val retrofitService: TripLogApiService by lazy {
        retrofit.create(TripLogApiService::class.java)
    }

    override val repository: InterfaceRepository by lazy {
        Repository(retrofitService)
    }
}