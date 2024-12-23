package com.example.triplog.travel.presentation.travelGallery

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.example.triplog.main.ResponseHandler
import com.example.triplog.main.SessionManager
import com.example.triplog.main.TripLogApplication
import com.example.triplog.main.navigation.Screen
import com.example.triplog.network.InterfaceRepository
import com.example.triplog.travel.data.PlaceData
import com.example.triplog.travel.data.TravelData
import com.mapbox.geojson.Point

sealed class TravelGallerySection {
    data object Main : TravelGallerySection()
    data object TravelOverview : TravelGallerySection()
}

class TravelGalleryViewModel(
    private val repository: InterfaceRepository,
    val sessionManager: SessionManager,
    val responseHandler: ResponseHandler
) : ViewModel() {

    var isProgressIndicatorVisible by mutableStateOf(false)
    var isBackendResponseVisible by mutableStateOf(false)
    var isDeleteTravelDialogVisible by mutableStateOf(false)

    var section by mutableStateOf<TravelGallerySection>(TravelGallerySection.Main)

    val samplePlaces = listOf(
        PlaceData(
            name = "Eiffel Tower",
            description = "Iconic landmark of Paris with stunning views.",
            category = "Sightseeing",
            image = null,
            point = Point.fromLngLat(2.2945, 48.8584)
        ),
        PlaceData(
            name = "Grand Canyon",
            description = "Breathtaking natural wonder in Arizona.",
            category = "Nature",
            image = null,
            point = Point.fromLngLat(-112.1401, 36.0544)
        ),
        PlaceData(
            name = "Santorini",
            description = "Beautiful Greek island known for white-washed buildings.",
            category = "Beach",
            image = null,
            point = Point.fromLngLat(25.4319, 36.3932)
        )
    )

    val travel = TravelData(
        name = "Summer Vacation",
        description = "A memorable trip to sunny beaches.",
        startDate = "2024-06-01",
        endDate = "2024-06-15",
        favourite = true,
        image = null,
        point = Point.fromLngLat(2.2945, 48.8584),
        places = samplePlaces
    )


    val travelList = listOf(travel)
    var travelOverview by mutableStateOf(TravelData())

    fun setDeleteDialogVisibility(isVisible: Boolean) {
        isDeleteTravelDialogVisible = isVisible
    }

    fun prepareTempTravelDataToSharedVM(): TravelData {
        val travel = travel
        travel.places = travel.places
        travel.image = travel.image
        return travel
    }

    fun navigateToEditTravel(navController: NavController) {
        navController.navigate(Screen.CreateTravelScreen.destination)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                val sessionManager = application.sessionManager
                val responseHandler = ResponseHandler()
                TravelGalleryViewModel(
                    repository = repository,
                    sessionManager = sessionManager,
                    responseHandler = responseHandler
                )
            }
        }
    }
}