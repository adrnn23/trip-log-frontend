package com.example.triplog.travel.data


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TravelRequest(
    @Json(name = "description")
    val description: String?,
    @Json(name = "from")
    val from: String?,
    @Json(name = "latitude")
    val latitude: Double?,
    @Json(name = "longitude")
    val longitude: Double?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "places")
    val places: List<Place?>?,
    @Json(name = "to")
    val to: String?
) {
    @JsonClass(generateAdapter = true)
    data class Place(
        @Json(name = "id")
        val id: Int?,
        @Json(name = "category_id")
        val categoryId: Int?,
        @Json(name = "description")
        val description: String?,
        @Json(name = "latitude")
        val latitude: Double?,
        @Json(name = "longitude")
        val longitude: Double?,
        @Json(name = "name")
        val name: String?
    )
}