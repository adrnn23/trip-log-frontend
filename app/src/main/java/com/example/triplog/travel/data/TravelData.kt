package com.example.triplog.travel.data

import android.net.Uri
import com.mapbox.geojson.Point
import okhttp3.MultipartBody


data class TravelData(
    var id: Int? = null,
    var name: String? = null,
    var description: String? = null,
    var image: Uri? = null,
    var startDate: String? = null,
    var endDate: String? = null,
    var point: Point? = null,
    var favourite: Boolean? = null,
    var places: List<PlaceData?> = emptyList(),
    var userProfileImage: Int? = null,
    var userName: String? = null,
    var timeAgo: String? = null,
    var imagePart: MultipartBody.Part? = null,
    var imageUrl: String? = null,
)

