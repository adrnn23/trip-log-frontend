package com.example.triplog.network

import android.util.Log
import com.example.triplog.authorization.login.data.LoginRequest
import com.example.triplog.authorization.login.data.LoginResult
import com.example.triplog.authorization.registration.data.RegistrationRequest
import com.example.triplog.authorization.registration.data.RegistrationResult
import com.example.triplog.profile.data.profile.AuthenticatedUserProfileResult
import com.example.triplog.profile.data.profile.EditUserProfileRequest
import com.example.triplog.profile.data.profile.EditUserProfileResult
import com.example.triplog.profile.data.profile.TravelPreference
import com.example.triplog.profile.data.profile.TravelPreferencesResult
import com.example.triplog.profile.data.profile.UserProfileResult
import com.example.triplog.profile.data.updatePassword.UpdatePasswordRequest
import com.example.triplog.profile.data.updatePassword.UpdatePasswordResult
import com.squareup.moshi.Moshi
import okhttp3.ResponseBody
import retrofit2.Response

fun loginSuccess(response: Response<LoginResult>): LoginResult? {
    return response.body()
}

fun loginClientError(response: Response<LoginResult>): ResponseBody? {
    return response.errorBody()
}

fun registrationSuccess(response: Response<RegistrationResult>): RegistrationResult? {
    return response.body()
}

fun registrationClientError(response: Response<RegistrationResult>): ResponseBody? {
    return response.errorBody()
}

interface InterfaceRepository {
    suspend fun getLoginResult(request: LoginRequest): LoginResult?
    suspend fun getRegistrationResult(request: RegistrationRequest): RegistrationResult?
    suspend fun getAuthenticatedUserProfileResult(token: String?): AuthenticatedUserProfileResult?
    suspend fun updatePassword(
        token: String?,
        updatePasswordRequest: UpdatePasswordRequest
    ): UpdatePasswordResult?

    suspend fun getUserProfileResult(token: String?, id: Int): UserProfileResult?
    suspend fun getTravelPreferences(token: String?): TravelPreferencesResult?
    suspend fun editUserProfile(
        token: String?,
        id: Int,
        editUserProfileRequest: EditUserProfileRequest
    ): EditUserProfileResult?
}

class Repository(private val tripLogApiService: TripLogApiService) : InterfaceRepository {
    override suspend fun getLoginResult(request: LoginRequest): LoginResult? {
        val response: Response<LoginResult> = tripLogApiService.getLoginResult(request)
        when (response.code()) {
            200 -> {
                var loginResult = loginSuccess(response)
                if (loginResult != null) {
                    if (loginResult.token != null) {
                        Log.d("Success - Token", loginResult.token.toString())
                        loginResult = LoginResult(response.code(), loginResult.token, null, null)
                        return loginResult
                    }
                }
            }

            401 -> {
                val unauthorizedBody = loginClientError(response)?.string()
                var loginResult = Moshi.Builder().build().adapter(LoginResult::class.java)
                    .fromJson(unauthorizedBody!!)
                if (loginResult != null) {
                    if (loginResult.message != null) {
                        loginResult =
                            LoginResult(response.code(), null, loginResult.message, null)
                        return loginResult
                    }
                }
            }

            422 -> {
                val errorBody = loginClientError(response)?.string()
                var loginResult =
                    Moshi.Builder().build().adapter(LoginResult::class.java).fromJson(errorBody!!)
                if (loginResult != null) {
                    if (loginResult.errors != null && loginResult.message != null) {
                        loginResult = LoginResult(
                            response.code(),
                            null,
                            loginResult.message,
                            loginResult.errors
                        )
                        return loginResult
                    }
                }
            }
        }
        return null
    }

    override suspend fun getRegistrationResult(request: RegistrationRequest): RegistrationResult? {
        val response: Response<RegistrationResult> =
            tripLogApiService.getRegistrationResult(request)
        when (response.code()) {
            201 -> {
                var registrationResult = registrationSuccess(response)
                if (registrationResult != null) {
                    if (registrationResult.token != null) {
                        registrationResult = RegistrationResult(
                            response.code(),
                            registrationResult.token,
                            null,
                            null
                        )
                        return registrationResult
                    }
                }
            }

            422 -> {
                val errorBody = registrationClientError(response)?.string()
                var registrationResult =
                    Moshi.Builder().build().adapter(RegistrationResult::class.java)
                        .fromJson(errorBody!!)

                if (registrationResult != null) {
                    if (registrationResult.errors != null && registrationResult.message != null) {
                        registrationResult = RegistrationResult(
                            response.code(),
                            null,
                            registrationResult.message,
                            registrationResult.errors
                        )
                        return registrationResult
                    }
                }
            }
        }
        return null
    }

    override suspend fun getAuthenticatedUserProfileResult(token: String?): AuthenticatedUserProfileResult? {
        val response: Response<AuthenticatedUserProfileResult> =
            tripLogApiService.getAuthenticatedUserProfileResult("Bearer $token")

        val authenticatedUserProfileResult: AuthenticatedUserProfileResult?
        if (response.isSuccessful) {
            authenticatedUserProfileResult = response.body()
            authenticatedUserProfileResult?.resultCode = response.code()
            return authenticatedUserProfileResult
        } else {
            val errorBody = response.errorBody()?.string()
            authenticatedUserProfileResult =
                Moshi.Builder().build().adapter(AuthenticatedUserProfileResult::class.java)
                    .fromJson(errorBody!!)
            authenticatedUserProfileResult?.resultCode = response.code()
            return authenticatedUserProfileResult
        }
    }

    override suspend fun updatePassword(
        token: String?,
        updatePasswordRequest: UpdatePasswordRequest
    ): UpdatePasswordResult? {
        val response: Response<UpdatePasswordResult> =
            tripLogApiService.updatePassword("Bearer $token", updatePasswordRequest)

        val updatePasswordResult: UpdatePasswordResult?

        if (response.isSuccessful) {
            updatePasswordResult = response.body()
            updatePasswordResult?.resultCode = response.code()
            return updatePasswordResult
        } else {
            val errorBody = response.errorBody()?.string()
            updatePasswordResult = Moshi.Builder().build().adapter(UpdatePasswordResult::class.java)
                .fromJson(errorBody!!)
            updatePasswordResult?.resultCode = response.code()
            return updatePasswordResult
        }
    }

    override suspend fun getUserProfileResult(token: String?, id: Int): UserProfileResult? {
        val response: Response<UserProfileResult> =
            tripLogApiService.getUserProfileResult(
                token = "Bearer $token",
                id = id
            )
        val userProfileResult: UserProfileResult?
        if (response.isSuccessful) {
            userProfileResult = response.body()
            userProfileResult?.resultCode = response.code()
            return userProfileResult
        } else {
            val errorBody = response.errorBody()?.string()
            userProfileResult = Moshi.Builder().build().adapter(UserProfileResult::class.java)
                .fromJson(errorBody!!)
            userProfileResult?.resultCode = response.code()
            return userProfileResult
        }
    }


    override suspend fun getTravelPreferences(token: String?): TravelPreferencesResult {
        val response: Response<List<TravelPreference>> =
            tripLogApiService.getTravelPreferences("Bearer $token")

        val travelPreferencesResult = TravelPreferencesResult(null, null, null)

        if (response.isSuccessful) {
            travelPreferencesResult.travelPreference = response.body()
            travelPreferencesResult.resultCode = response.code()
            return travelPreferencesResult
        } else {
            val errorBody = response.errorBody()?.string()
            travelPreferencesResult.message = errorBody ?: "Error"
            travelPreferencesResult.resultCode = response.code()
            return travelPreferencesResult
        }
    }


    override suspend fun editUserProfile(
        token: String?,
        id: Int,
        editUserProfileRequest: EditUserProfileRequest
    ): EditUserProfileResult? {
        val response: Response<EditUserProfileResult> =
            tripLogApiService.updateUserProfile("Bearer $token", id, editUserProfileRequest)
        val editUserProfileResult: EditUserProfileResult?

        if (response.isSuccessful) {
            editUserProfileResult = response.body()
            editUserProfileResult?.resultCode = response.code()
            return editUserProfileResult
        } else {
            val errorBody = response.errorBody()?.string()
            editUserProfileResult =
                Moshi.Builder().build().adapter(EditUserProfileResult::class.java)
                    .fromJson(errorBody!!)
            editUserProfileResult?.resultCode = response.code()
            editUserProfileResult?.message = editUserProfileResult?.message
            editUserProfileResult?.errors = editUserProfileResult?.errors
            return editUserProfileResult
        }
    }
}