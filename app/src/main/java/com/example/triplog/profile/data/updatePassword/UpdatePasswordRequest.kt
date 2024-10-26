package com.example.triplog.profile.data.updatePassword


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdatePasswordRequest(
    @Json(name = "current_password")
    val oldPassword: String,
    @Json(name = "password")
    val password: String,
    @Json(name = "password_confirmation")
    val passwordConfirmation: String
)