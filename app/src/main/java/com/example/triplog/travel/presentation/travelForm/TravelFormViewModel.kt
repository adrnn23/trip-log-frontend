package com.example.triplog.travel.presentation.travelForm

import android.annotation.SuppressLint
import android.content.Context
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
import com.example.triplog.R
import com.example.triplog.main.BackendResponse
import com.example.triplog.main.ResponseHandler
import com.example.triplog.main.SessionManager
import com.example.triplog.main.TripLogApplication
import com.example.triplog.main.navigation.Screen
import com.example.triplog.network.InterfaceRepository
import com.example.triplog.network.MapboxClient
import com.example.triplog.profile.components.showToast
import com.example.triplog.travel.data.PlaceCategoryData
import com.example.triplog.travel.data.PlaceData
import com.example.triplog.travel.data.TravelData
import com.mapbox.geojson.Point
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class TravelFormSection {
    data object Main : TravelFormSection()
    data object PlaceForm : TravelFormSection()
    data object EditTravelInformation : TravelFormSection()
    data object EditPlaceInformation : TravelFormSection()
}

sealed class CreateTravelState {
    data object Authenticated : CreateTravelState()
    data object Unauthenticated : CreateTravelState()
    data object Idle : CreateTravelState()
    data object Error : CreateTravelState()
}

@SuppressLint("MutableCollectionMutableState")
class TravelFormViewModel(
    private val repository: InterfaceRepository,
    val sessionManager: SessionManager,
    private val mapboxClient: MapboxClient,
    val responseHandler: ResponseHandler
) : ViewModel() {

    var createTravelState by mutableStateOf<CreateTravelState>(CreateTravelState.Idle)

    var travel by mutableStateOf(TravelData())
    var travelNameTemp by mutableStateOf("")
    var travelDescriptionTemp by mutableStateOf("")
    var travelImage by mutableStateOf<Uri?>(null)
    var travelPointTemp by mutableStateOf<Point?>(null)
    var travelPlaces: MutableList<PlaceData?> = emptyList<PlaceData?>().toMutableList()

    var place by mutableStateOf(PlaceData())
    var placeNameTemp by mutableStateOf("")
    var placeDescriptionTemp by mutableStateOf("")
    var placePointTemp by mutableStateOf<Point?>(null)
    var placeImage by mutableStateOf<Uri?>(null)

    var placeCategoriesList by mutableStateOf(mutableListOf<PlaceCategoryData?>())

    var section by mutableStateOf<TravelFormSection>(TravelFormSection.Main)
    var isCreateEditTravelDialogVisible by mutableStateOf(false)
    var isBackendResponseVisible by mutableStateOf(false)
    var isProgressIndicatorVisible by mutableStateOf(false)
    var editedPlaceIndex by mutableStateOf<Int?>(null)

    var editScreen by mutableStateOf(false)

    fun setTravelToEdit(travelToEdit: TravelData) {
        travel.name = travelToEdit.name
        travel.description = travelToEdit.description
        travel.startDate = travelToEdit.startDate
        travel.endDate = travelToEdit.endDate
        travel.point = travelToEdit.point
        travel.favourite = travelToEdit.favourite
        travelImage = travelToEdit.image
        travelPlaces = travelToEdit.places.toMutableList()
    }

    fun setPlaceToEdit(placeToEdit: PlaceData) {
        place.name = placeToEdit.name
        place.description = placeToEdit.description
        place.point = placeToEdit.point
        place.category = placeToEdit.category
        placeImage = placeToEdit.image
    }

    fun prepareTempTravelDataToSharedVM(): TravelData {
        val travel = travel
        travel.places = travelPlaces
        travel.image = travelImage
        return travel
    }

    fun prepareTempPlaceDataToSharedVM(): PlaceData {
        val place = place
        place.image = placeImage
        return place
    }

    var isDeleting by mutableStateOf(false)
    fun removePlaceWithLoading(place: PlaceData?, context: Context) {
        isDeleting = true
        travelPlaces.remove(place)
        viewModelScope.launch {
            delay(200)
            isDeleting = false
        }
        showToast(context, R.string.placeDeleted)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                val sessionManager = application.sessionManager
                val mapboxGeocodingClient = application.mapboxClient
                val responseHandler = ResponseHandler()
                TravelFormViewModel(
                    repository = repository,
                    sessionManager = sessionManager,
                    mapboxClient = mapboxGeocodingClient,
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
                    result.travelCategories?.forEach { item ->
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

    fun updateFavoriteStatus(isFavorite: Boolean) {
        travel = travel.copy(favourite = isFavorite)
    }

    fun searchPlace(place: String, accessToken: String, label: String) {
        viewModelScope.launch {
            if (place != "") {
                val response = mapboxClient.mapboxService.searchPlace(
                    place = place,
                    accessToken = accessToken
                )
                val responseBody = response.body()
                if (responseBody?.features != null) {
                    val coordinates = responseBody.features.first().geometry.coordinates
                    when (label) {
                        "Travel" -> {
                            travelPointTemp = Point.fromLngLat(coordinates[0], coordinates[1])
                        }

                        "Place" -> {
                            placePointTemp = Point.fromLngLat(coordinates[0], coordinates[1])
                        }
                    }
                }
            }
        }
    }
}