package com.example.triplog.travel.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplog.travel.data.PlaceData
import com.example.triplog.travel.data.TravelData
import com.mapbox.geojson.Point

enum class PointType {
    Travel, Place, None
}


class SharedTravelViewModel : ViewModel() {
    var isTravelToEdit by mutableStateOf(false)
        private set
    var tempTravelData by mutableStateOf(TravelData())
    var tempPlaceData by mutableStateOf(PlaceData())
    var tempPointType by mutableStateOf(PointType.None)
    var editedPlaceIndex by mutableStateOf<Int?>(null)

    fun setTravelData(data: TravelData) {
        tempTravelData = data.copy()
    }

    fun clearTravelData() {
        tempTravelData = TravelData()
    }

    fun setPlaceData(data: PlaceData) {
        tempPlaceData = data.copy()
    }

    fun clearPlaceData() {
        tempPlaceData = PlaceData()
    }

    fun setPointType(type: PointType) {
        tempPointType = type
    }

    fun clearPointType() {
        tempPointType = PointType.None
    }

    fun setNewPointInTravelOrPlace(point: Point) {
        when (tempPointType) {
            PointType.Travel -> {
                tempTravelData.point = point
            }
            PointType.Place -> {
                tempPlaceData.point = point
            }
            else -> {}
        }
    }

    fun setTravelEdit(toEdit: Boolean) {
        isTravelToEdit = toEdit
    }
}
