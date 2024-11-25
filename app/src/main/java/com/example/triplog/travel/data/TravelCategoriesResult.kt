package com.example.triplog.travel.data


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TravelCategory(
    @Json(name = "id")
    val id: Int?,
    @Json(name = "name")
    val name: String?,
    var isSelected: Boolean = false
)

data class TravelCategoriesResult(
    var resultCode: Int?,
    var message: String?,
    var travelCategories: List<TravelCategory?>?
)