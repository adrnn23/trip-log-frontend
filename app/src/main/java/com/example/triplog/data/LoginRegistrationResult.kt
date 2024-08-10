package com.example.triplog.data


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRegistrationResult(
    @Json(name = "token")
    val token: String,
    @Json(name = "user")
    val user: User
) {
    @JsonClass(generateAdapter = true)
    data class User(
        @Json(name = "email")
        val email: String,
        @Json(name = "id")
        val id: Int,
        @Json(name = "name")
        val name: String
    )
}