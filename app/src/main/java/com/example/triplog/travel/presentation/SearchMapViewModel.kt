package com.example.triplog.travel.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.triplog.main.BackendResponse
import com.example.triplog.main.ResponseHandler
import com.example.triplog.network.MapboxClient
import com.mapbox.geojson.Point
import kotlinx.coroutines.launch

sealed class SearchMapState {
    data object Idle : SearchMapState()
    data object SearchedSuccessfully : SearchMapState()
    data object EmptySearchList : SearchMapState()
    data object SearchError : SearchMapState()
}

class SearchMapViewModel(
    val responseHandler: ResponseHandler,
    private val mapboxClient: MapboxClient
) : ViewModel() {
    var point by mutableStateOf<Point?>(null)
    var placeName by mutableStateOf<String?>(null)
    var searchMapState by mutableStateOf<SearchMapState>(SearchMapState.Idle)
    var isMapboxResponseVisible by mutableStateOf(false)

    fun searchPlace(place: String, accessToken: String) {
        viewModelScope.launch {
            try {
                if (place != "") {
                    val response = mapboxClient.mapboxService.searchPlace(
                        place = place,
                        accessToken = accessToken
                    )
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.features?.isNotEmpty() == true) {
                            val coordinates = responseBody.features.first().geometry.coordinates
                            point = Point.fromLngLat(coordinates[0], coordinates[1])
                            placeName = responseBody.features.first().placeName
                            val backendResponse = BackendResponse()
                            processSearchedSuccessfully(backendResponse = backendResponse)
                        } else {
                            val backendResponse =
                                BackendResponse(message = "No location found based on the entered data.")
                            processEmptySearchList(backendResponse = backendResponse)
                        }
                    } else {
                        val responseCode = response.code()
                        val backendResponse =
                            BackendResponse(message = "The operation was unsuccessful (${responseCode} response code).")
                        processSearchError(backendResponse = backendResponse)
                    }
                }
            } catch (e: Exception) {
                val backendResponse = BackendResponse(message = e.message)
                processSearchError(backendResponse = backendResponse)
            }
        }
    }

    fun getPlaceNameByCoordinates(point: Point, accessToken: String) {
        viewModelScope.launch {
            try {
                val response = mapboxClient.mapboxService.reverseSearchPlace(
                    longitude = point.longitude(),
                    latitude = point.latitude(),
                    accessToken = accessToken
                )
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.features?.isNotEmpty() == true) {
                        placeName = responseBody.features.first().placeName
                        val backendResponse = BackendResponse()
                        processSearchedSuccessfully(backendResponse = backendResponse)
                    } else {
                        placeName = null
                    }
                } else {
                    val responseCode = response.code()
                    val backendResponse =
                        BackendResponse(message = "The operation was unsuccessful (${responseCode} response code).")
                    processSearchError(backendResponse = backendResponse)
                }
            } catch (e: Exception) {
                val backendResponse = BackendResponse(message = e.message)
                processSearchError(backendResponse = backendResponse)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val responseHandler = ResponseHandler()
                val mapboxClient = MapboxClient()
                SearchMapViewModel(
                    responseHandler = responseHandler,
                    mapboxClient = mapboxClient
                )
            }
        }
    }

    private fun processSearchedSuccessfully(backendResponse: BackendResponse) {
        searchMapState = SearchMapState.SearchedSuccessfully
        responseHandler.showMessage(backendResponse)
    }

    private fun processEmptySearchList(backendResponse: BackendResponse) {
        searchMapState = SearchMapState.EmptySearchList
        responseHandler.showMessage(backendResponse)
    }

    private fun processSearchError(backendResponse: BackendResponse) {
        searchMapState = SearchMapState.SearchError
        responseHandler.showMessage(backendResponse)
    }

    fun handleSearchMapState() {
        isMapboxResponseVisible = when (searchMapState) {
            SearchMapState.SearchError -> true
            SearchMapState.EmptySearchList -> true
            else -> false
        }
    }

    fun handleProcesses() {
        isMapboxResponseVisible = false
        responseHandler.clearMessage()
        searchMapState = SearchMapState.Idle
    }
}