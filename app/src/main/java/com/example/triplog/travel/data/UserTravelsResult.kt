package com.example.triplog.travel.data


import com.example.triplog.main.data.TimelineResult.Image
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserTravelsResult(
    var resultCode: Int? = null,
    @Json(name = "data")
    val data: List<TravelDataResult>? = null,
    @Json(name = "message")
    val message: String? = null,
    @Json(name = "meta")
    val meta: Meta? = null
) {
    @JsonClass(generateAdapter = true)
    data class TravelDataResult(
        @Json(name = "image")
        val image: Image? = null,
        @Json(name = "created")
        val created: String? = null,
        @Json(name = "description")
        val description: String? = null,
        @Json(name = "favourite")
        val favourite: Boolean? = null,
        @Json(name = "from")
        val from: String? = null,
        @Json(name = "id")
        val id: Int? = null,
        @Json(name = "latitude")
        val latitude: Double? = null,
        @Json(name = "longitude")
        val longitude: Double? = null,
        @Json(name = "name")
        val name: String? = null,
        @Json(name = "to")
        val to: String? = null
    )

    @JsonClass(generateAdapter = true)
    data class Meta(
        @Json(name = "current_page")
        val currentPage: Int? = null,
        @Json(name = "last_page")
        val lastPage: Int? = null,
        @Json(name = "per_page")
        val perPage: Int? = null,
        @Json(name = "total")
        val total: Int? = null
    )
}
