package com.example.triplog.network

import android.util.Log
import com.example.triplog.data.LoginRequest
import com.example.triplog.data.LoginResult
import com.example.triplog.data.RegistrationRequest
import com.example.triplog.data.RegistrationResult
import com.squareup.moshi.Moshi
import okhttp3.ResponseBody
import retrofit2.Response

fun returnLoginSuccess(response: Response<LoginResult>): LoginResult? {
    return response.body()
}

fun returnRegistrationSuccess(response: Response<RegistrationResult>): RegistrationResult? {
    return response.body()
}

fun returnLoginUnauthorized(response: Response<LoginResult>): ResponseBody? {
    return response.errorBody()
}

fun returnLoginErrors(response: Response<LoginResult>): ResponseBody? {
    return response.errorBody()
}

fun returnRegistrationErrors(response: Response<RegistrationResult>): ResponseBody? {
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
                var loginResult = returnLoginSuccess(response)
                Log.d("Success - Token", loginResult?.token.toString())
                Log.d("Success - Name", loginResult?.user!!.name.toString())
                Log.d("Success - Email", loginResult.user!!.email.toString())
                Log.d("Success - ID", loginResult.user!!.id.toString())
                if (loginResult.token != null) {
                    loginResult =
                        LoginResult(
                            response.code(),
                            loginResult.token,
                            loginResult.user,
                            null,
                            null
                        )
                    return loginResult
                }
            }

            401 -> {
                val unauthorizedBody = returnLoginUnauthorized(response)?.string()
                var loginResult = Moshi.Builder().build().adapter(LoginResult::class.java)
                    .fromJson(unauthorizedBody!!)
                if (loginResult?.message?.isNotBlank() == true) {
                    Log.d("Unauthorized - Message", loginResult.message.toString())
                    loginResult =
                        LoginResult(response.code(), null, null, loginResult.message, null)
                    return loginResult
                }
            }

            422 -> {
                val errorBody = returnLoginErrors(response)?.string()
                var loginResult =
                    Moshi.Builder().build().adapter(LoginResult::class.java).fromJson(errorBody!!)

                if (loginResult?.message != null)
                    Log.d("Login Errors - Message", loginResult.message.toString())

                if (loginResult?.errors?.email?.isNotEmpty() == true)
                    Log.d("Login Errors - Email", loginResult.errors?.email!![0].toString())

                if (loginResult?.errors?.password?.isNotEmpty() == true)
                    Log.d("Login Errors - Password", loginResult.errors?.password!![0].toString())

                if (loginResult?.errors != null) {
                    loginResult =
                        LoginResult(
                            response.code(),
                            null,
                            null,
                            loginResult.message,
                            loginResult.errors
                        )
                    return loginResult
                }
            }
        }
        return null
    }


    override suspend fun getRegistrationResult(request: RegistrationRequest): RegistrationResult? {
        val response: Response<RegistrationResult> =
            tripLogApiService.getRegistrationResult(request)
        when (response.code()) {
            200 -> {
                var registrationResult = returnRegistrationSuccess(response)
                Log.d("Success - Token", registrationResult?.token.toString())
                Log.d("Success - Name", registrationResult?.user?.name.toString())
                Log.d("Success - Email", registrationResult?.user?.email.toString())
                Log.d("Success - ID", registrationResult?.user?.id.toString())

                if (registrationResult?.token != null) {
                    registrationResult = RegistrationResult(
                        response.code(),
                        registrationResult.token,
                        registrationResult.user,
                        null,
                        null
                    )
                    return registrationResult
                }
            }

            422 -> {
                val errorBody = returnRegistrationErrors(response)?.string()
                var registrationResult =
                    Moshi.Builder().build().adapter(RegistrationResult::class.java)
                        .fromJson(errorBody.toString())

                if (registrationResult?.message != null)
                    Log.d("Registration Errors - Message", registrationResult.message.toString())

                if (registrationResult?.errors?.name?.isNotEmpty() == true)
                    Log.d("Registration Errors - Name", registrationResult.errors?.name!![0].toString())

                if (registrationResult?.errors?.email?.isNotEmpty() == true)
                    Log.d("Registration Errors - Email", registrationResult.errors?.email!![0].toString())

                if (registrationResult?.errors?.password?.isNotEmpty() == true)
                    Log.d(
                        "Registration Errors - Password",
                        registrationResult.errors?.password!![0].toString()
                    )

                if (registrationResult?.errors != null) {
                    registrationResult =
                        RegistrationResult(
                            response.code(),
                            null,
                            null,
                            registrationResult.message,
                            registrationResult.errors
                        )
                    return registrationResult
                }
            }
        }
        return null
    }
}