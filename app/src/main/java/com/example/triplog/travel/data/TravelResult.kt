package com.example.triplog.travel.data


import com.example.triplog.main.data.TimelineResult.Image
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TravelResult(
    var resultCode: Int?,
    @Json(name = "id")
    var id: Int?,
    @Json(name = "created")
    var created: String?,
    @Json(name = "description")
    var description: String?,
    @Json(name = "errors")
    var errors: Errors?,
    @Json(name = "favourite")
    var favourite: Boolean?,
    @Json(name = "from")
    var from: String?,
    @Json(name = "latitude")
    var latitude: Double?,
    @Json(name = "longitude")
    var longitude: Double?,
    @Json(name = "message")
    var message: String?,
    @Json(name = "name")
    var name: String?,
    @Json(name = "places")
    var places: List<Place?>?,
    @Json(name = "to")
    var to: String?,
    @Json(name = "images")
    val image: List<Image>? = null,
) {
    @JsonClass(generateAdapter = true)
    data class Errors(
        @Json(name = "from")
        var from: List<String?>?,
        @Json(name = "name")
        var name: List<String?>?
    )

    @JsonClass(generateAdapter = true)
    data class Place(
        @Json(name = "category")
        val categoryId: Category?,
        @Json(name = "description")
        var description: String?,
        @Json(name = "id")
        var id: Int?,
        @Json(name = "latitude")
        var latitude: Double?,
        @Json(name = "longitude")
        var longitude: Double?,
        @Json(name = "name")
        var name: String?,
        @Json(name = "images")
        val image: List<Image>? = null,
    )

    @JsonClass(generateAdapter = true)
    data class Category(
        @Json(name = "id") val id: Int,
        @Json(name = "name") val name: String
    )
}