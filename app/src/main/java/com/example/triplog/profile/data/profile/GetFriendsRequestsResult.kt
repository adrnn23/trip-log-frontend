package com.example.triplog.profile.data.profile


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetFriendsRequestsResult(
    var resultCode: Int?,
    @Json(name = "data")
    val `data`: List<Data?>?,
    @Json(name = "message")
    var message: String?,
    @Json(name = "meta")
    val meta: Meta?
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "created_at_diff")
        val createdAtDiff: String?,
        @Json(name = "id")
        val id: Int?,
        @Json(name = "user")
        val user: User?
    ) {
        @JsonClass(generateAdapter = true)
        data class User(
            @Json(name = "avatar")
            val avatar: String?,
            @Json(name = "bio")
            val bio: String?,
            @Json(name = "id")
            val id: Int?,
            @Json(name = "name")
            val name: String?
        )
    }

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