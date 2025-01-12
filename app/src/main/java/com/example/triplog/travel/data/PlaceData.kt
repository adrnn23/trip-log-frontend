package com.example.triplog.travel.data

import android.net.Uri
import com.mapbox.geojson.Point
import okhttp3.MultipartBody

data class PlaceData(
    var id: Int? = null,
    var name: String? = null,
    var description: String? = null,
    var image: Uri? = null,
    var category: String? = null,
    var point: Point? = null,
    var imagePart: MultipartBody.Part? = null,
    var imageUrl: String? = null,
    )