package com.example.triplog.travel.data


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateImageResult(
    var resultCode: Int?,
    @Json(name = "errors")
    var errors: Errors?,
    @Json(name = "message")
    var message: String?,
    @Json(name = "url")
    var url: String?
) {
    @JsonClass(generateAdapter = true)
    data class Errors(
        @Json(name = "image")
        var image: List<String?>?
    )
}