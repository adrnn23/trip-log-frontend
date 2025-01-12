package com.example.triplog.profile.data.profile


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserProfileResult(
    var resultCode: Int?,
    @Json(name = "avatar")
    val avatar: String?,
    @Json(name = "bio")
    val bio: String?,
    @Json(name = "facebook_link")
    val facebookLink: String?,
    @Json(name = "id")
    val id: Int?,
    @Json(name = "instagram_link")
    val instagramLink: String?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "planned_travels_count")
    val plannedTripsCount: Int?,
    @Json(name = "travel_preferences")
    val travelPreferences: List<TravelPreference?>?,
    @Json(name = "finished_travels_count")
    val tripsCount: Int?,
    @Json(name = "x_link")
    val xLink: String?
) {
    @JsonClass(generateAdapter = true)
    data class TravelPreference(
        @Json(name = "id")
        val id: Int?,
        @Json(name = "name")
        val name: String?,
        var isSelected: Boolean = false
    )
}