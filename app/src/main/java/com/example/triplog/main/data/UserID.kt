package com.example.triplog.main.data


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserID(
    @Json(name = "user_id")
    val userId: Int?
)