package com.example.triplog.profile.data.profile


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FriendsOperationResult(
    var resultCode: Int?,
    @Json(name = "message")
    val message: String?
)