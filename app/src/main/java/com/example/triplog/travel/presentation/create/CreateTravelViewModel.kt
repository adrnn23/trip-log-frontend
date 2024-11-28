package com.example.triplog.travel.presentation.create

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.example.triplog.main.BackendResponse
import com.example.triplog.main.ResponseHandler
import com.example.triplog.main.SessionManager
import com.example.triplog.main.TripLogApplication
import com.example.triplog.main.navigation.Screen
import com.example.triplog.network.InterfaceRepository
import com.example.triplog.network.MapboxGeocodingClient
import com.example.triplog.travel.data.PlaceCategoryData
import com.mapbox.geojson.Point
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class CreateTravelSection {
    data object Main : CreateTravelSection()
    data object EditTravelInformation : CreateTravelSection()
    data object AddPlaceMain : CreateTravelSection()
    data object EditTravelPlaces : CreateTravelSection()
    data object EditPlaceInformation : CreateTravelSection()
    data object EditPlaceLocalization : CreateTravelSection()
}

sealed class CreateTravelState {
    data object Authenticated : CreateTravelState()
    data object Unauthenticated : CreateTravelState()
    data object Idle : CreateTravelState()
    data object Error : CreateTravelState()
}


data class PlaceData(
    var name: String? = null,
    var description: String? = null,
    var category: String? = null,
    var image: Uri? = null,
    var point: Point? = null
)

data class TravelData(
    var name: String? = null,
    var description: String? = null,
    var startDate: String? = null,
    var endDate: String? = null,
    var tags: List<String?> = emptyList(),
    var places: List<PlaceData?> = emptyList()
)

@SuppressLint("MutableCollectionMutableState")
class CreateTravelViewModel(
    private val repository: InterfaceRepository,
    val sessionManager: SessionManager,
    private val mapboxGeocodingClient: MapboxGeocodingClient,
    val responseHandler: ResponseHandler
) : ViewModel() {

    var createTravelState by mutableStateOf<CreateTravelState>(CreateTravelState.Idle)

    var travel by mutableStateOf(TravelData())
    var travelNameTemp by mutableStateOf("")
    var travelDescriptionTemp by mutableStateOf("")
    var travelImage by mutableStateOf<Uri?>(null)
    var travelPlaces: MutableList<PlaceData?> = emptyList<PlaceData?>().toMutableList()

    var place by mutableStateOf(PlaceData())
    var placeNameTemp by mutableStateOf("")
    var placeDescriptionTemp by mutableStateOf("")
    var pointTemp by mutableStateOf<Point?>(null)
    var placeImage by mutableStateOf<Uri?>(null)

    var placeCategoriesList by mutableStateOf(mutableListOf<PlaceCategoryData?>())

    var section by mutableStateOf<CreateTravelSection>(CreateTravelSection.Main)
    var isCreateNewTravelDialogVisible by mutableStateOf(false)
    var isBackendResponseVisible by mutableStateOf(false)
    var isProgressIndicatorVisible by mutableStateOf(false)
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
                val responseHandler = ResponseHandler()
                CreateTravelViewModel(
                    repository = repository,
                    sessionManager = sessionManager,
                    mapboxGeocodingClient = mapboxGeocodingClient,
                    responseHandler = responseHandler
                )
            }
        }
    }

    /**
     * Process state functions are responsible for:
     * - setting the message from server in responseHandler,
     * - setting the profile state in viewModel.
     */
    private fun processUnauthenticatedState(backendResponse: BackendResponse) {
        createTravelState = CreateTravelState.Unauthenticated
        responseHandler.showMessage(backendResponse)
    }

    private fun processErrorState(backendResponse: BackendResponse) {
        createTravelState = CreateTravelState.Error
        responseHandler.showMessage(backendResponse)
    }

    private fun processAuthenticatedState(backendResponse: BackendResponse) {
        createTravelState = CreateTravelState.Authenticated
        responseHandler.showMessage(backendResponse)
    }

    /**
     * Function handleProfileState() sets the viewModel's flags based on the viewModel's profileState.
     */
    fun handleCreateState() {
        isBackendResponseVisible = when (createTravelState) {
            CreateTravelState.Error -> true
            CreateTravelState.Authenticated -> false
            CreateTravelState.Unauthenticated -> true
            else -> false
        }
    }

    fun handleProcesses(navController: NavController) {
        when (createTravelState) {
            CreateTravelState.Error -> {
                isBackendResponseVisible = false
                responseHandler.clearMessage()
            }

            CreateTravelState.Authenticated -> {
                isBackendResponseVisible = false
                responseHandler.clearMessage()
            }

            CreateTravelState.Unauthenticated -> {
                isBackendResponseVisible = false
                responseHandler.clearMessage()
                logoutProcess(navController = navController)
            }

            else -> {
                isBackendResponseVisible = false
                responseHandler.clearMessage()
            }
        }
    }

    /**
     * Function logoutProcess() clears the session token in sessionManager and navigate user to login screen.
     */
    private fun logoutProcess(navController: NavController) {
        sessionManager.clearToken()
        navController.navigate(Screen.LoginScreen.destination)
    }


    fun getTravelCategories() {
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result = repository.getTravelCategories(token)
                if (result?.resultCode == 200 && result.travelCategories != null) {
                    result.travelCategories?.forEach { item->
                        val placeCategoryData = PlaceCategoryData(item?.name, false)
                        placeCategoriesList.add(placeCategoryData)
                    }
                    val backendResponse = BackendResponse()
                    processAuthenticatedState(backendResponse)
                } else if (result?.resultCode == 401 && result.message != null) {
                    val backendResponse = BackendResponse(message = result.message)
                    processUnauthenticatedState(backendResponse)
                } else {
                    val backendResponse = BackendResponse(message = result?.message)
                    processErrorState(backendResponse)
                }
            } catch (e: Exception) {
                val backendResponse = BackendResponse(message = e.message)
                processErrorState(backendResponse)
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