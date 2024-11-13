package com.example.triplog.travel.presentation

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.triplog.main.SessionManager
import com.example.triplog.main.TripLogApplication
import com.example.triplog.network.InterfaceRepository
import com.example.triplog.network.MapboxGeocodingClient
import com.example.triplog.travel.data.PlaceCategoryData
import com.mapbox.geojson.Point
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class CreateTravelSection {
    data object Main : CreateTravelSection()
    data object EditTravelInformation : CreateTravelSection()
    data object EditTravelDescription : CreateTravelSection()
    data object AddPlaceMain : CreateTravelSection()
    data object EditTravelPlaces : CreateTravelSection()
    data object EditPlaceInformation : CreateTravelSection()
    data object EditPlaceDescription : CreateTravelSection()
    data object EditPlaceLocalization : CreateTravelSection()
}


data class PlaceData(
    var name: String? = "",
    var description: String? = "",
    var category: String? = "",
    var image: Uri? = null,
    var point: Point? = null
)

data class TravelData(
    var name: String? = "",
    var description: String? = "",
    var startDate: String? = "",
    var endDate: String? = "",
    var tags: List<String?> = emptyList(),
    var places: List<PlaceData?> = emptyList()
)

class CreateTravelViewModel(
    private val repository: InterfaceRepository,
    val sessionManager: SessionManager,
    private val mapboxGeocodingClient: MapboxGeocodingClient
) : ViewModel() {

    var travel by mutableStateOf(TravelData())
    var travelNameTemp by mutableStateOf("")
    var travelDescriptionTemp by mutableStateOf("")
    var travelImage by mutableStateOf<Uri?>(null)
    var tagsTemp: List<String?> by mutableStateOf(emptyList())
    var travelPlaces: MutableList<PlaceData?> = emptyList<PlaceData?>().toMutableList()

    var place by mutableStateOf(PlaceData())
    var placeNameTemp by mutableStateOf("")
    var placeDescriptionTemp by mutableStateOf("")
    var pointTemp by mutableStateOf<Point?>(null)
    var placeImage by mutableStateOf<Uri?>(null)
    var placeCategories: List<String> by mutableStateOf(
        listOf(
            "Museums",
            "Theatres",
            "Historic places",
            "Monuments",
            "Temples",
            "Churches",
            "Palaces",
            "Castles",
            "Art Galleries",
            "Festivals",
            "Cathedrals",
            "Main Square",
            "Cafe",
            "Restaurant",
            "Bars",
            "Bakeries",
            "Airports",
            "Train Stations",
            "Ports",
            "Bus stations",
            "Zoos",
            "Gardens",
            "Sports Arenas",
            "Beaches",
            "Parks",
            "Hiking Trails"
        )
    )
    var placeCategoriesData: List<PlaceCategoryData> by mutableStateOf(
        listOf(
            PlaceCategoryData("Museums", false),
            PlaceCategoryData("Theatres", false),
            PlaceCategoryData("Historic places", false),
            PlaceCategoryData("Monuments", false),
            PlaceCategoryData("Temples", false),
            PlaceCategoryData("Churches", false),
            PlaceCategoryData("Palaces", false),
            PlaceCategoryData("Castles", false),
            PlaceCategoryData("Art Galleries", false),
            PlaceCategoryData("Festivals", false),
            PlaceCategoryData("Cathedrals", false),
            PlaceCategoryData("Main Square", false),
            PlaceCategoryData("Cafe", false),
            PlaceCategoryData("Restaurant", false),
            PlaceCategoryData("Bars", false),
            PlaceCategoryData("Bakeries", false),
            PlaceCategoryData("Airports", false),
            PlaceCategoryData("Train Stations", false),
            PlaceCategoryData("Ports", false),
            PlaceCategoryData("Bus stations", false),
            PlaceCategoryData("Zoos", false),
            PlaceCategoryData("Gardens", false),
            PlaceCategoryData("Sports Arenas", false),
            PlaceCategoryData("Beaches", false),
            PlaceCategoryData("Parks", false),
            PlaceCategoryData("Hiking Trails", false)
        )
    )

    var section by mutableStateOf<CreateTravelSection>(CreateTravelSection.Main)
    var isCreateNewTravelDialogVisible by mutableStateOf(false)
    var isPlacesVisible by mutableStateOf(false)


    var isDeleting by mutableStateOf(false)
    fun removePlaceWithLoading(place: PlaceData?) {
        isDeleting = true
        travelPlaces.remove(place)
        viewModelScope.launch {
            delay(500)
            isDeleting = false
            if(travelPlaces.size>0){
                isPlacesVisible = true
            }
        }
    }



    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                val sessionManager = application.sessionManager
                val mapboxGeocodingClient = application.mapboxGeocodingClient
                CreateTravelViewModel(
                    repository = repository,
                    sessionManager = sessionManager,
                    mapboxGeocodingClient = mapboxGeocodingClient
                )
            }
        }
    }

    fun searchPlace(place: String, accessToken: String){
        viewModelScope.launch {
            if(place!=""){
                val response = mapboxGeocodingClient.mapboxGeocodingService.searchPlace(
                    place = place,
                    accessToken = accessToken
                )
                val responseBody = response.body()
                if (responseBody?.features != null) {
                    val coordinates = responseBody.features.first().geometry.coordinates
                    pointTemp = Point.fromLngLat(coordinates[0], coordinates[1])
                }
            }
        }
    }
}