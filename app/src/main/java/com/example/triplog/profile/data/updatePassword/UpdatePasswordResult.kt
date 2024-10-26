package com.example.triplog.profile.data.updatePassword


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdatePasswordResult(
    var resultCode: Int?,
    @Json(name = "errors")
    val errors: Errors?,
    @Json(name = "message")
    val message: String?
) {
    @JsonClass(generateAdapter = true)
    data class Errors(
        @Json(name = "current_password")
        val oldPassword: List<String?>?,
        @Json(name = "password")
        val password: List<String?>?
    )
}