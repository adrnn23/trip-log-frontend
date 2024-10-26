package com.example.triplog.profile.data.profile


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EditUserProfileRequest(
    @Json(name = "avatar")
    val avatar: String?,
    @Json(name = "bio")
    val bio: String?,
    @Json(name = "email")
    val email: String?,
    @Json(name = "facebook_link")
    val facebookLink: String?,
    @Json(name = "instagram_link")
    val instagramLink: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "travel_preferences")
    val travelPreferences: List<String?>?,
    @Json(name = "x_link")
    val xLink: String?
)