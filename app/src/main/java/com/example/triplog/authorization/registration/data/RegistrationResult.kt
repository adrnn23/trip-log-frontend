package com.example.triplog.authorization.registration.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationResult(
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
        @Json(name = "name")
        val name: List<String?>?,
        @Json(name = "password")
        val password: List<String?>?
    )
}