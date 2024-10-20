package com.example.triplog.authorization.registration.data


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationRequest(
    @Json(name = "name")
    val name: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "password")
    val password: String,
    @Json(name = "password_confirmation")
    val passwordConfirmation: String,
    @Json(name = "device_name")
    val deviceName: String
)