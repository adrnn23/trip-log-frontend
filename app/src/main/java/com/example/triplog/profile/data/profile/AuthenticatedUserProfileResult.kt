package com.example.triplog.profile.data.profile


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthenticatedUserProfileResult(
    var resultCode: Int?,
    @Json(name = "avatar")
    val avatar: Any?,
    @Json(name = "email")
    val email: String?,
    @Json(name = "id")
    val id: Int?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "name")
    val name: String?
)