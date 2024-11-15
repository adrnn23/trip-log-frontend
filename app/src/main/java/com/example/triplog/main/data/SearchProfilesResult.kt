package com.example.triplog.main.data


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchProfilesResult(
    var resultCode: Int?,
    @Json(name = "data")
    val `data`: List<Data?>?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "meta")
    val meta: Meta?
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "avatar")
        val avatar: String?,
        @Json(name = "bio")
        val bio: String?,
        @Json(name = "friend_status")
        val friendStatus: Int?,
        @Json(name = "id")
        val id: Int?,
        @Json(name = "name")
        val name: String?,
        @Json(name = "received_request_id")
        val receivedRequestId: Int?
    )

    @JsonClass(generateAdapter = true)
    data class Meta(
        @Json(name = "current_page")
        val currentPage: Int?,
        @Json(name = "last_page")
        val lastPage: Int?,
        @Json(name = "per_page")
        val perPage: Int?,
        @Json(name = "total")
        val total: Int?
    )
}