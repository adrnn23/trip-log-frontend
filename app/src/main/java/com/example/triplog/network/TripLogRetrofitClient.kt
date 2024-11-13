package com.example.triplog.network

import com.example.triplog.authorization.login.data.LoginRequest
import com.example.triplog.authorization.login.data.LoginResult
import com.example.triplog.authorization.login.data.LogoutResult
import com.example.triplog.authorization.registration.data.RegistrationRequest
import com.example.triplog.authorization.registration.data.RegistrationResult
import com.example.triplog.profile.data.profile.AuthenticatedUserProfileResult
import com.example.triplog.profile.data.profile.EditUserProfileRequest
import com.example.triplog.profile.data.profile.EditUserProfileResult
import com.example.triplog.profile.data.profile.TravelPreference
import com.example.triplog.profile.data.profile.UserProfileResult
import com.example.triplog.profile.data.updatePassword.UpdatePasswordRequest
import com.example.triplog.profile.data.updatePassword.UpdatePasswordResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface TripLogApiService {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/auth/login")
    suspend fun getLoginResult(@Body request: LoginRequest): Response<LoginResult>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/auth/register")
    suspend fun getRegistrationResult(@Body request: RegistrationRequest): Response<RegistrationResult>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("/api/users/me")
    suspend fun getAuthenticatedUserProfileResult(@Header("Authorization") token: String?): Response<AuthenticatedUserProfileResult>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @PATCH("/api/users/me/update-password")
    suspend fun updatePassword(
        @Header("Authorization") token: String?,
        @Body updatePasswordRequest: UpdatePasswordRequest
    ): Response<UpdatePasswordResult>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("/api/profiles/{user}")
    suspend fun getUserProfileResult(
        @Header("Authorization") token: String,
        @Path("user") id: Int
    ): Response<UserProfileResult>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("/api/enums/travel-preferences")
    suspend fun getTravelPreferences(
        @Header("Authorization") token: String?
    ): Response<List<TravelPreference>>


    @Headers("Content-Type: application/json", "Accept: application/json")
    @PUT("/api/profiles/{user}/update")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String?,
        @Path("user") id: Int,
        @Body editUserProfileRequest: EditUserProfileRequest
    ): Response<EditUserProfileResult>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/auth/logout")
    suspend fun getLogoutResult(@Header("Authorization") token: String?): Response<LogoutResult>
}

interface RepositoryContainer {
    val repository: InterfaceRepository
}

class TripLogRetrofitClient : RepositoryContainer {
    private val url = "http://192.168.1.9/"

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