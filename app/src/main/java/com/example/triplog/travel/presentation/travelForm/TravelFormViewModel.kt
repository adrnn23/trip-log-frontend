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
import com.example.triplog.main.LoadingState
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
import com.example.triplog.travel.data.TravelRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

sealed class TravelFormSection {
    data object Main : TravelFormSection()
    data object PlaceForm : TravelFormSection()
    data object EditTravelInformation : TravelFormSection()
    data object EditPlaceInformation : TravelFormSection()
}

sealed class TravelFormState {
    data object Authenticated : TravelFormState()
    data object Unauthenticated : TravelFormState()
    data object Idle : TravelFormState()
    data object Error : TravelFormState()
    data object TravelSuccess : TravelFormState()
}

@SuppressLint("MutableCollectionMutableState")
class TravelFormViewModel(
    private val repository: InterfaceRepository,
    val sessionManager: SessionManager,
    private val mapboxClient: MapboxClient,
    val responseHandler: ResponseHandler
) : ViewModel() {

    var travelFormState by mutableStateOf<TravelFormState>(TravelFormState.Idle)
    var travel by mutableStateOf(TravelData())
    var travelNameTemp by mutableStateOf<String?>(null)
    var travelDescriptionTemp by mutableStateOf<String?>(null)
    var travelImage by mutableStateOf<Uri?>(null)
    var travelPlaces: MutableList<PlaceData?> = emptyList<PlaceData?>().toMutableList()
    var travelStaticMapUrl by mutableStateOf<String?>(null)

    var place by mutableStateOf(PlaceData())
    var placeNameTemp by mutableStateOf<String?>(null)
    var placeDescriptionTemp by mutableStateOf<String?>(null)
    var placeImage by mutableStateOf<Uri?>(null)
    var placeStaticMapUrl by mutableStateOf<String?>(null)
    var placeCategoriesList by mutableStateOf(mutableListOf<PlaceCategoryData?>())
    private var placeCategoriesWithIdList by mutableStateOf(mutableListOf<Pair<Int, String>>())

    var section by mutableStateOf<TravelFormSection>(TravelFormSection.Main)
    var isCreateEditTravelDialogVisible by mutableStateOf(false)
    var isBackendResponseVisible by mutableStateOf(false)
    var isProgressIndicatorVisible by mutableStateOf(false)
    var loadingState: LoadingState by mutableStateOf(LoadingState.NotLoaded)
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
        travel.id = travelToEdit.id
        travel.imageUrl = travelToEdit.imageUrl
    }

    fun setPlaceToEdit(placeToEdit: PlaceData) {
        place.name = placeToEdit.name
        place.description = placeToEdit.description
        place.point = placeToEdit.point
        place.category = placeToEdit.category
        placeImage = placeToEdit.image
        place.id = placeToEdit.id
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
        place?.let { travelPlaces.remove(it) }
        viewModelScope.launch {
            delay(200)
            isDeleting = false
        }
        this.place = PlaceData()
        showToast(context, R.string.placeDeleted)
    }


    fun getStaticMap(label: String, accessToken: String) {
        viewModelScope.launch {
            try {
                when (label) {
                    "Travel" -> {
                        val darkMarker = "pin-s+555555(${travel.point?.coordinates()?.get(0)},${
                            travel.point?.coordinates()?.get(1)
                        })"
                        if (travel.point != null) {
                            travelStaticMapUrl =
                                mapboxClient.getStaticMapUrl(
                                    travel.point!!.longitude(),
                                    travel.point!!.latitude(),
                                    marker = darkMarker,
                                    accessToken = accessToken
                                )
                        }
                    }

                    "Place" -> {
                        val darkMarker = "pin-s+555555(${place.point?.coordinates()?.get(0)},${
                            place.point?.coordinates()?.get(1)
                        })"
                        placeStaticMapUrl =
                            mapboxClient.getStaticMapUrl(
                                place.point!!.longitude(),
                                place.point!!.latitude(),
                                marker = darkMarker,
                                accessToken = accessToken
                            )
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun addTravel() {
        loadingState = LoadingState.Loading
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val request = TravelRequest(
                    name = travel.name,
                    description = travel.description,
                    from = travel.startDate,
                    to = travel.endDate,
                    longitude = travel.point?.longitude(),
                    latitude = travel.point?.latitude(),
                    places = toTravelRequestPlaceList(travelPlaces, placeCategoriesWithIdList)
                )
                val result = repository.createTravel(token, request)
                var backendResponse = BackendResponse()
                processAuthenticatedState(backendResponse)

                if (result != null) {
                    if (result.resultCode == 200 && (result.id != null)) {
                        travel.id = result.id
                        updateImage("App\\Models\\Travel", travel.id!!, travel.imagePart)
                        travelPlaces.forEachIndexed { index, placeData ->
                            placeData?.id = result.places?.get(index)?.id
                            updateImage("App\\Models\\Place", placeData?.id!!, placeData.imagePart)
                        }
                    } else if (result.resultCode == 401 && result.message != null) {
                        backendResponse = BackendResponse(message = result.message)
                        processUnauthenticatedState(backendResponse)
                    } else {
                        backendResponse =
                            BackendResponse(
                                message = result.message, errors = listOfNotNull(
                                    result.errors?.name?.joinToString(separator = ", "),
                                    result.errors?.from?.joinToString(separator = ", ")
                                )
                            )
                        processErrorState(backendResponse)
                    }
                }
            } catch (e: Exception) {
                val backendResponse = BackendResponse(message = e.message)
                processErrorState(backendResponse)
            }
            loadingState = LoadingState.Loaded
        }
    }

    private suspend fun updateImage(type: String, id: Int, image: MultipartBody.Part?) {
        val messagePart =
            if (!editScreen) "Travel created successfully." else "Travel edited successfully."
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val imageableType = type.toRequestBody("text/plain".toMediaTypeOrNull())
                val imageableId =
                    id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val result = repository.updateImage(token, imageableType, imageableId, image)
                if (result != null) {
                    if (result.resultCode == 200 && (result.url != null)) {
                        val backendResponse =
                            BackendResponse(message = messagePart)
                        processTravelSuccess(backendResponse)
                    } else if (result.resultCode == 401 && result.message != null) {
                        val backendResponse =
                            BackendResponse(message = messagePart + " " + result.message)
                        processUnauthenticatedState(backendResponse)
                    } else {
                        val backendResponse = BackendResponse(
                            message = messagePart + " " + result.message,
                            errors = listOfNotNull(result.errors?.image?.joinToString(separator = ", "))
                        )
                        processErrorState(backendResponse)
                    }
                }
            } catch (e: Exception) {
                val backendResponse = BackendResponse(message = e.message)
                processErrorState(backendResponse)
            }
        }
    }


    private fun toTravelRequestPlaceList(
        places: MutableList<PlaceData?>, categories: List<Pair<Int, String>>
    ): List<TravelRequest.Place?> {
        return places.map { place ->
            place?.let {
                val categoryId =
                    categories.find { category -> category.second == it.category }?.first ?: 1
                TravelRequest.Place(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    categoryId = categoryId,
                    longitude = it.point?.longitude(),
                    latitude = it.point?.latitude()
                )
            }
        }
    }

    fun editTravel() {
        loadingState = LoadingState.Loading
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val request = TravelRequest(
                    name = travel.name,
                    description = travel.description,
                    from = travel.startDate,
                    to = travel.endDate,
                    longitude = travel.point?.longitude(),
                    latitude = travel.point?.latitude(),
                    places = toTravelRequestPlaceList(travelPlaces, placeCategoriesWithIdList)
                )
                val result = travel.id?.let { repository.updateTravel(token, it, request) }
                if (result != null) {
                    if (result.resultCode == 200 && (result.id != null)) {
                        travel.id = result.id
                        if (travel.imagePart != null) {
                            updateImage("App\\Models\\Travel", travel.id!!, travel.imagePart)
                        }
                        travelPlaces.forEachIndexed { index, placeData ->
                            placeData?.id = result.places?.get(index)?.id
                            if (placeData?.imagePart != null) {
                                updateImage(
                                    "App\\Models\\Place",
                                    placeData.id!!,
                                    placeData.imagePart
                                )
                            }
                        }
                        val backendResponse = BackendResponse()
                        processAuthenticatedState(backendResponse)
                    } else if (result.resultCode == 401 && result.message != null) {
                        val backendResponse = BackendResponse(message = result.message)
                        processUnauthenticatedState(backendResponse)
                    } else {
                        val backendResponse =
                            BackendResponse(
                                message = result.message, errors = listOfNotNull(
                                    result.errors?.name?.joinToString(separator = ", "),
                                    result.errors?.from?.joinToString(separator = ", ")
                                )
                            )
                        processErrorState(backendResponse)
                    }
                }
            } catch (e: Exception) {
                val backendResponse = BackendResponse(message = e.message)
                processErrorState(backendResponse)
            }
            loadingState = LoadingState.Loaded
        }
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
        travelFormState = TravelFormState.Unauthenticated
        responseHandler.showMessage(backendResponse)
    }

    private fun processErrorState(backendResponse: BackendResponse) {
        travelFormState = TravelFormState.Error
        responseHandler.showMessage(backendResponse)
    }

    private fun processAuthenticatedState(backendResponse: BackendResponse) {
        travelFormState = TravelFormState.Authenticated
        responseHandler.showMessage(backendResponse)
    }

    private fun processTravelSuccess(backendResponse: BackendResponse) {
        travelFormState = TravelFormState.TravelSuccess
        responseHandler.showMessage(backendResponse)
    }

    /**
     * Function handleTravelFormState() sets the viewModel's flags based on the viewModel's profileState.
     */
    fun handleTravelFormState() {
        isBackendResponseVisible = when (travelFormState) {
            TravelFormState.Error -> true
            TravelFormState.Authenticated -> false
            TravelFormState.Unauthenticated -> true
            TravelFormState.TravelSuccess -> true
            else -> false
        }
    }

    fun handleProcesses(navController: NavController) {
        when (travelFormState) {
            TravelFormState.Error -> {
                clearBackendResponse()
                travelFormState = TravelFormState.Idle
            }

            TravelFormState.Authenticated -> {
                clearBackendResponse()
                travelFormState = TravelFormState.Idle
            }

            TravelFormState.Unauthenticated -> {
                clearBackendResponse()
                logoutProcess(navController = navController)
            }

            TravelFormState.TravelSuccess -> {
                clearBackendResponse()
                navController.navigate(Screen.MainPageScreen.destination)
            }

            TravelFormState.Idle -> {}
        }
    }

    private fun clearBackendResponse() {
        isBackendResponseVisible = false
        responseHandler.clearMessage()
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
                    placeCategoriesWithIdList = result.travelCategories!!.mapNotNull { item ->
                        item?.id?.let { id -> id to (item.name ?: "") }
                    }.toMutableList()

                    placeCategoriesList.clear()
                    result.travelCategories!!.forEach { item ->
                        val placeCategoryData = PlaceCategoryData(
                            category = item?.name,
                            isSelected = false
                        )
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
}