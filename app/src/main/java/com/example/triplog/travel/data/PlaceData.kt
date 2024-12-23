package com.example.triplog.travel.data

import android.net.Uri
import com.mapbox.geojson.Point

data class PlaceData(
    var name: String? = null,
    var description: String? = null,
    var image: Uri? = null,
    var category: String? = null,
    var point: Point? = null
)