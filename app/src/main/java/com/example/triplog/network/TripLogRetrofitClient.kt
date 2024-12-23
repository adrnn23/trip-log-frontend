package com.example.triplog.network

import com.example.triplog.authorization.login.data.LoginRequest
import com.example.triplog.authorization.login.data.LoginResult
import com.example.triplog.authorization.login.data.LogoutResult
import com.example.triplog.authorization.registration.data.RegistrationRequest
import com.example.triplog.authorization.registration.data.RegistrationResult
import com.example.triplog.main.data.SearchProfilesResult
import com.example.triplog.main.data.UserID
import com.example.triplog.profile.data.profile.AuthenticatedUserProfileResult
import com.example.triplog.profile.data.profile.EditUserProfileRequest
import com.example.triplog.profile.data.profile.EditUserProfileResult
import com.example.triplog.profile.data.profile.FriendsOperationResult
import com.example.triplog.profile.data.profile.GetFriendsListResult
import com.example.triplog.profile.data.profile.GetFriendsRequestsResult
import com.example.triplog.profile.data.profile.TravelPreference
import com.example.triplog.profile.data.profile.UserProfileResult
import com.example.triplog.profile.data.updatePassword.UpdatePasswordRequest
import com.example.triplog.profile.data.updatePassword.UpdatePasswordResult
import com.example.triplog.travel.data.TravelCategory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


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

    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("/api/friends/list")
    suspend fun getFriendsList(
        @Header("Authorization") token: String?
    ): Response<GetFriendsListResult>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("/api/friends/requests")
    suspend fun getFriendsRequests(
        @Header("Authorization") token: String?
    ): Response<GetFriendsRequestsResult>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/api/friends/send-request")
    suspend fun sendFriendRequest(
        @Header("Authorization") token: String?,
        @Body userID: UserID
    ): Response<FriendsOperationResult>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @PATCH("/api/friends/accept-request/{friend_request}")
    suspend fun acceptFriendRequest(
        @Header("Authorization") token: String?,
        @Path("friend_request") id: Int
    ): Response<FriendsOperationResult>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @DELETE("/api/friends/reject-request/{friend_request}")
    suspend fun rejectFriendRequest(
        @Header("Authorization") token: String?,
        @Path("friend_request") id: Int
    ): Response<FriendsOperationResult>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("/api/profiles/search")
    suspend fun getSearchProfilesResult(
        @Header("Authorization") token: String?,
        @Query("query") query: String,
        @Query("page") page: Int?,
    ): Response<SearchProfilesResult>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("/api/enums/travel-categories")
    suspend fun getTravelCategories(
        @Header("Authorization") token: String?
    ): Response<List<TravelCategory>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @DELETE("/api/friends/delete/{friend_id}")
    suspend fun deleteFriendRequest(
        @Header("Authorization") token: String?,
        @Path("friend_id") id: Int
    ): Response<FriendsOperationResult>
}

interface RepositoryContainer {
    val repository: InterfaceRepository
}

class TripLogRetrofitClient : RepositoryContainer {
    private val url = "http://192.168.120.141/"

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