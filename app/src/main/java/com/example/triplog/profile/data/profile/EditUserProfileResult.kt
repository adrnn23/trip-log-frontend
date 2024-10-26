package com.example.triplog.profile.data.profile


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EditUserProfileResult(
    var resultCode: Int?,
    @Json(name = "bio")
    val bio: String?,
    @Json(name = "errors")
    var errors: Errors?,
    @Json(name = "facebook_link")
    val facebookLink: String?,
    @Json(name = "id")
    val id: Int?,
    @Json(name = "instagram_link")
    val instagramLink: String?,
    @Json(name = "message")
    var message: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "planned_trips_count")
    val plannedTripsCount: Int?,
    @Json(name = "travel_preferences")
    val travelPreferences: List<TravelPreference?>?,
    @Json(name = "trips_count")
    val tripsCount: Int?,
    @Json(name = "x_link")
    val xLink: String?
) {
    @JsonClass(generateAdapter = true)
    data class Errors(
        @Json(name = "avatar")
        val avatar: List<String?>?,
        @Json(name = "email")
        val email: List<String?>?,
        @Json(name = "name")
        val name: List<String?>?
    )

    @JsonClass(generateAdapter = true)
    data class TravelPreference(
        @Json(name = "id")
        val id: Int?,
        @Json(name = "name")
        val name: String?
    )
}