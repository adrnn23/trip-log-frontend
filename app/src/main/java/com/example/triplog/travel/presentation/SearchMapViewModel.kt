package com.example.triplog.travel.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.triplog.main.ResponseHandler
import com.example.triplog.main.TripLogApplication
import com.example.triplog.network.MapboxClient
import com.mapbox.geojson.Point
import kotlinx.coroutines.launch

class SearchMapViewModel(
    val responseHandler: ResponseHandler,
    private val mapboxClient: MapboxClient
) : ViewModel() {
    var point by mutableStateOf<Point?>(null)

    fun searchPlace(place: String, accessToken: String) {
        viewModelScope.launch {
            if (place != "") {
                val response = mapboxClient.mapboxService.searchPlace(
                    place = place,
                    accessToken = accessToken
                )
                val responseBody = response.body()
                if (responseBody?.features != null) {
                    val coordinates = responseBody.features.first().geometry.coordinates
                    point = Point.fromLngLat(coordinates[0], coordinates[1])
                } else {
                    point = null
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val responseHandler = ResponseHandler()
                val mapboxClient = MapboxClient()
                SearchMapViewModel(
                    responseHandler = responseHandler,
                    mapboxClient = mapboxClient
                )
            }
        }
    }
}