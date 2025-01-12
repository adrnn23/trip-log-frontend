package com.example.triplog.travel.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodingSearchResponse(
    @Json(name = "query") val query: List<String>,
    @Json(name = "features") val features: List<Feature>
) {
    @JsonClass(generateAdapter = true)
    data class Feature(
        @Json(name = "place_name") val placeName: String,
        @Json(name = "geometry") val geometry: Geometry
    ) {
        @JsonClass(generateAdapter = true)
        data class Geometry(
            @Json(name = "coordinates") val coordinates: List<Double>
        )
    }
}