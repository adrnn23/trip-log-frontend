package com.example.triplog.authorization.login.data


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LogoutResult(
    var resultCode: Int?,
    @Json(name = "message")
    var message: String?
)