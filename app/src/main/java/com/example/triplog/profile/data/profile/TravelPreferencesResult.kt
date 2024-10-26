package com.example.triplog.profile.data.profile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TravelPreference(
    @Json(name = "id")
    val id: Int?,
    @Json(name = "name")
    val name: String?,
    var isSelected: Boolean = false
)

data class TravelPreferencesResult(
    var resultCode: Int?,
    var message: String?,
    var travelPreference: List<TravelPreference?>?
)