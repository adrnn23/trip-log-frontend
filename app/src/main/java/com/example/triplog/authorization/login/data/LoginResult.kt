package com.example.triplog.authorization.login.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResult(
    val resultCode: Int?,
    @Json(name = "token")
    val token: String?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "errors")
    val errors: Errors?
) {
    @JsonClass(generateAdapter = true)
    data class Errors(
        @Json(name = "email")
        val email: List<String?>?,
        @Json(name = "password")
        val password: List<String?>?
    )
}

