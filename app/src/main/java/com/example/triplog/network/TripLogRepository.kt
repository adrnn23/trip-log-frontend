package com.example.triplog.network

import android.util.Log
import com.example.triplog.data.LoginRequest
import com.example.triplog.data.LoginResult
import com.example.triplog.data.RegistrationRequest
import com.example.triplog.data.RegistrationResult
import retrofit2.Response

fun returnSuccess(response: Response<LoginResult>): LoginResult? {
    return response.body()
}

fun returnUnauthorized(response: Response<LoginResult>): LoginResult? {
    return response.body()
}

fun returnErrors(response: Response<LoginResult>): LoginResult? {
    return response.body()
}

interface InterfaceRepository {
    suspend fun getLoginResult(request: LoginRequest): LoginResult?
    suspend fun getRegistrationResult(request: RegistrationRequest): RegistrationResult
}

class Repository(private val tripLogApiService: TripLogApiService) : InterfaceRepository {

    override suspend fun getLoginResult(request: LoginRequest): LoginResult? {
        val response: Response<LoginResult> = tripLogApiService.getLoginResult(request)
        when (response.code()) {
            200 -> {
                var loginResult = returnSuccess(response)
                Log.d("Success - Token", loginResult?.token.toString())
                Log.d("Success - Name", loginResult?.user!!.name.toString())
                Log.d("Success - Email", loginResult.user!!.email.toString())
                Log.d("Success - ID", loginResult.user!!.id.toString())
                loginResult = LoginResult(loginResult.token, loginResult.user, null, null)
                return loginResult
            }
            401 -> {
                var loginResult = returnUnauthorized(response)
                Log.d("Unauthorized - Message", loginResult?.message.toString())
                Log.d("Unauthorized - Password", loginResult?.errors!!.password!![0].toString())
                Log.d("Unauthorized - Email", loginResult.errors!!.email!![0].toString())
                loginResult = LoginResult(null, null, loginResult.errors, loginResult.message)
                return loginResult
            }
            422 -> {
                var loginResult = returnErrors(response)
                Log.d("Errors - Message", loginResult?.message.toString())
                Log.d("Errors - Password", loginResult?.errors!!.password!![0].toString())
                Log.d("Errors - Email", loginResult.errors!!.email!![0].toString())
                loginResult = LoginResult(null, null, loginResult.errors, loginResult.message)
                return loginResult
            }
        }
        return null
    }


    override suspend fun getRegistrationResult(request: RegistrationRequest): RegistrationResult =
        tripLogApiService.getRegistrationResult(request)
}