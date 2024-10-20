package com.example.triplog.network

import android.util.Log
import com.example.triplog.authorization.login.data.LoginRequest
import com.example.triplog.authorization.login.data.LoginResult
import com.example.triplog.authorization.registration.data.RegistrationRequest
import com.example.triplog.authorization.registration.data.RegistrationResult
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
                        Log.d("Unauthorized - Message", loginResult.message.toString())
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
                        Log.d("Unprocessable Content - Message", loginResult.message.toString())

                        if (loginResult.errors?.email?.isNotEmpty() == true)
                            Log.d(
                                "Unprocessable Content - Email",
                                loginResult.errors?.email!![0].toString()
                            )

                        if (loginResult.errors?.password?.isNotEmpty() == true)
                            Log.d(
                                "Unprocessable Content - Password",
                                loginResult.errors?.password!![0].toString()
                            )
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
        val response: Response<RegistrationResult> = tripLogApiService.getRegistrationResult(request)
        when (response.code()) {
            201 -> {
                var registrationResult = registrationSuccess(response)
                if (registrationResult != null) {
                    if (registrationResult.token != null) {
                        Log.d("Success - Token", registrationResult.token.toString())
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
                var registrationResult = Moshi.Builder().build().adapter(RegistrationResult::class.java)
                        .fromJson(errorBody!!)

                if (registrationResult != null) {
                    if (registrationResult.errors != null && registrationResult.message != null) {
                        Log.d("Unprocessable Content - Message", registrationResult.message.toString())
                        if (registrationResult.errors?.name != null)
                            Log.d(
                                "Unprocessable Content - Name",
                                registrationResult.errors?.name!![0].toString()
                            )

                        if (registrationResult.errors?.email != null)
                            Log.d(
                                "Unprocessable Content - Email",
                                registrationResult.errors?.email!![0].toString()
                            )

                        if (registrationResult.errors?.password != null)
                            Log.d(
                                "Unprocessable Content - Password",
                                registrationResult.errors?.password!![0].toString()
                            )
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
}