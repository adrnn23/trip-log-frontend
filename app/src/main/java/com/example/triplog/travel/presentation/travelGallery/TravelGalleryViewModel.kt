package com.example.triplog.travel.presentation.travelGallery

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.example.triplog.main.BackendResponse
import com.example.triplog.main.LoadingState
import com.example.triplog.main.ResponseHandler
import com.example.triplog.main.SessionManager
import com.example.triplog.main.TripLogApplication
import com.example.triplog.main.navigation.Screen
import com.example.triplog.network.InterfaceRepository
import com.example.triplog.travel.data.PlaceData
import com.example.triplog.travel.data.TravelData
import com.example.triplog.travel.data.TravelResult
import com.example.triplog.travel.data.UserTravelsResult
import com.mapbox.geojson.Point
import kotlinx.coroutines.launch

sealed class TravelGallerySection {
    data object Main : TravelGallerySection()
    data object TravelOverview : TravelGallerySection()
}

sealed class TravelGalleryState {
    data object Authenticated : TravelGalleryState()
    data object Unauthenticated : TravelGalleryState()
    data object Idle : TravelGalleryState()
    data object Error : TravelGalleryState()
    data object TravelDeleted : TravelGalleryState()
    data object TravelSuccess : TravelGalleryState()
}

class TravelGalleryViewModel(
    private val repository: InterfaceRepository,
    val sessionManager: SessionManager,
    val responseHandler: ResponseHandler,
    val id: Int?
) : ViewModel() {

    var finishedTravelsList by mutableStateOf<List<UserTravelsResult.TravelDataResult>?>(listOf())
    var favouriteTravelsList by mutableStateOf<List<UserTravelsResult.TravelDataResult>?>(listOf())
    var plannedTravelsList by mutableStateOf<List<UserTravelsResult.TravelDataResult>?>(listOf())
    private var userId by mutableStateOf<Int?>(null)
    var travelGalleryState by mutableStateOf<TravelGalleryState>(TravelGalleryState.Idle)
    var loadingState: LoadingState by mutableStateOf(LoadingState.NotLoaded)
    var isProgressIndicatorVisible by mutableStateOf(false)
    var isBackendResponseVisible by mutableStateOf(false)
    var isDeleteTravelDialogVisible by mutableStateOf(false)
    var section by mutableStateOf<TravelGallerySection>(TravelGallerySection.Main)
    var travelOverview by mutableStateOf(TravelData())

    var currentPageOfFinishedTravels by mutableIntStateOf(1)
    var totalPagesOfFinishedTravels by mutableIntStateOf(0)
    var currentPageOfFavouriteTravels by mutableIntStateOf(1)
    var totalPagesOfFavouriteTravels by mutableIntStateOf(0)
    var currentPageOfPlannedTravels by mutableIntStateOf(1)
    var totalPagesOfPlannedTravels by mutableIntStateOf(0)

    init {
        userId = id
        loadFinishedTravels()
        loadFavouriteTravels()
        loadPlannedTravels()
    }

    fun setDeleteDialogVisibility(isVisible: Boolean) {
        isDeleteTravelDialogVisible = isVisible
    }

    fun prepareTempTravelDataToSharedVM(): TravelData {
        return travelOverview
    }

    fun navigateToEditTravel(navController: NavController) {
        navController.navigate(Screen.TravelFormScreen.destination)
    }

    /**
     * Function logoutProcess() clears the session token in sessionManager and navigate user to login screen.
     */
    private fun logoutProcess(navController: NavController) {
        sessionManager.clearToken()
        navController.navigate(Screen.LoginScreen.destination)
    }

    fun loadNextPageOfFinishedTravels() {
        if (currentPageOfFinishedTravels < totalPagesOfFinishedTravels)
            loadFinishedTravels(currentPageOfFinishedTravels + 1)
    }

    fun loadPreviousPageOfFinishedTravels() {
        if (currentPageOfFinishedTravels > 1)
            loadFinishedTravels(currentPageOfFinishedTravels - 1)
    }

    fun loadNextPageOfFavouriteTravels() {
        if (currentPageOfFavouriteTravels < totalPagesOfFavouriteTravels)
            loadFavouriteTravels(currentPageOfFavouriteTravels + 1)
    }

    fun loadPreviousPageOfFavouriteTravels() {
        if (currentPageOfFavouriteTravels > 1)
            loadFavouriteTravels(currentPageOfFavouriteTravels - 1)
    }

    fun loadNextPageOfPlannedTravels() {
        if (currentPageOfPlannedTravels < totalPagesOfPlannedTravels)
            loadPlannedTravels(currentPageOfPlannedTravels + 1)
    }

    fun loadPreviousPageOfPlannedTravels() {
        if (currentPageOfPlannedTravels > 1)
            loadPlannedTravels(currentPageOfPlannedTravels - 1)
    }

    private fun loadFinishedTravels(page: Int = 1) {
        if (page < 1) return

        loadingState = LoadingState.Loading
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result =
                    userId?.let { repository.getUserFinishedTravels(token, it, page = page) }
                if (result?.resultCode == 200 && (result.data != null)) {
                    finishedTravelsList = result.data
                    currentPageOfFinishedTravels = page
                    totalPagesOfFinishedTravels = result.meta?.lastPage ?: 1
                    val backendResponse = BackendResponse()
                    processTravelSuccessState(backendResponse)
                } else if (result?.resultCode == 401 && result.message != null) {
                    val backendResponse = BackendResponse(message = result.message)
                    finishedTravelsList = null
                    processUnauthenticatedState(backendResponse)
                } else {
                    val backendResponse = BackendResponse(message = result?.message)
                    finishedTravelsList = null
                    processErrorState(backendResponse)
                }
            } catch (e: Exception) {
                val backendResponse = BackendResponse(message = e.message)
                finishedTravelsList = null
                processErrorState(backendResponse)
            }
            loadingState = LoadingState.Loaded
        }
    }

    private fun loadFavouriteTravels(page: Int = 1) {
        if (page < 1) return
        loadingState = LoadingState.Loading
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result =
                    userId?.let { repository.getUserFavouriteTravels(token, it, page = page) }
                if (result?.resultCode == 200 && (result.data != null)) {
                    favouriteTravelsList = result.data
                    currentPageOfFavouriteTravels = page
                    totalPagesOfFavouriteTravels = result.meta?.lastPage ?: 1
                    val backendResponse = BackendResponse()
                    processTravelSuccessState(backendResponse)
                } else if (result?.resultCode == 401 && result.message != null) {
                    val backendResponse = BackendResponse(message = result.message)
                    favouriteTravelsList = null
                    processUnauthenticatedState(backendResponse)
                } else {
                    val backendResponse = BackendResponse(message = result?.message)
                    favouriteTravelsList = null
                    processErrorState(backendResponse)
                }
            } catch (e: Exception) {
                val backendResponse = BackendResponse(message = e.message)
                favouriteTravelsList = null
                processErrorState(backendResponse)
            }
            loadingState = LoadingState.Loaded
        }
    }

    private fun loadPlannedTravels(page: Int = 1) {
        if (page < 1) return
        loadingState = LoadingState.Loading
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result =
                    userId?.let { repository.getUserPlannedTravels(token, it, page = page) }
                if (result?.resultCode == 200 && (result.data != null)) {
                    plannedTravelsList = result.data
                    currentPageOfPlannedTravels = page
                    totalPagesOfPlannedTravels = result.meta?.lastPage ?: 1
                    val backendResponse = BackendResponse()
                    processTravelSuccessState(backendResponse)
                } else if (result?.resultCode == 401 && result.message != null) {
                    val backendResponse = BackendResponse(message = result.message)
                    plannedTravelsList = null
                    processUnauthenticatedState(backendResponse)
                } else {
                    val backendResponse = BackendResponse(message = result?.message)
                    plannedTravelsList = null
                    processErrorState(backendResponse)
                }
            } catch (e: Exception) {
                val backendResponse = BackendResponse(message = e.message)
                plannedTravelsList = null
                processErrorState(backendResponse)
            }
            loadingState = LoadingState.Loaded
        }
    }

    private fun refreshLists() {
        loadFinishedTravels(1)
        loadPlannedTravels()
        loadFavouriteTravels()
        section = TravelGallerySection.Main
    }

    fun deleteTravel() {
        loadingState = LoadingState.Loading
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result = repository.deleteTravel(token, travelOverview.id!!)
                if (result != null) {
                    if (result.resultCode == 200 && result.message != null) {
                        val backendResponse =
                            BackendResponse(message = "Travel deleted successfully.")
                        processTravelDeletedState(backendResponse)
                    } else if (result.resultCode == 401 && result.message != null) {
                        val backendResponse = BackendResponse(message = result.message)
                        processUnauthenticatedState(backendResponse)
                    } else {
                        val backendResponse = BackendResponse(message = result.message)
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

    fun showTravel() {
        loadingState = LoadingState.Loading
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result = travelOverview.id?.let { repository.getTravel(token, it) }
                if (result != null) {
                    if (result.resultCode == 200 && (result.id != null) && (result.name != null)) {
                        travelOverview.id = result.id
                        travelOverview.name = result.name
                        travelOverview.description = result.description
                        travelOverview.startDate = result.from
                        travelOverview.endDate = result.to
                        travelOverview.favourite = result.favourite
                        travelOverview.places = convertPlaces(result.places)
                        travelOverview.imageUrl = result.image?.get(result.image.size - 1)?.url
                        travelOverview.timeAgo = result.created
                        if (result.longitude != null && result.latitude != null)
                            travelOverview.point =
                                Point.fromLngLat(result.longitude!!, result.latitude!!)
                        section = TravelGallerySection.TravelOverview
                    } else if (result.resultCode == 401 && result.message != null) {
                        val backendResponse = BackendResponse(message = result.message)
                        travelOverview.id = null
                        processUnauthenticatedState(backendResponse)
                    } else {
                        val backendResponse = BackendResponse(message = result.message)
                        travelOverview.id = null
                        processErrorState(backendResponse)
                    }
                }
            } catch (e: Exception) {
                val backendResponse = BackendResponse(message = e.message)
                travelOverview.id = null
                processErrorState(backendResponse)
            }
            loadingState = LoadingState.Loaded
        }
    }

    private fun toggleFavourite(isFavorite: Boolean) {
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result = repository.toggleFavouriteTravel(token, travelOverview.id!!)
                if (result != null) {
                    if (result.resultCode == 200) {
                        travelOverview = travelOverview.copy(favourite = isFavorite)
                        val backendResponse = BackendResponse()
                        processTravelSuccessState(backendResponse)
                        loadFavouriteTravels()
                    } else if (result.resultCode == 401 && result.message != null) {
                        val backendResponse = BackendResponse(message = result.message)
                        processUnauthenticatedState(backendResponse)
                    } else {
                        val backendResponse = BackendResponse(message = result.message)
                        processErrorState(backendResponse)
                    }
                }
            } catch (e: Exception) {
                val backendResponse = BackendResponse(message = "Exception - ${e.message}")
                processErrorState(backendResponse)
            }
        }
    }

    fun updateFavoriteStatus(isFavorite: Boolean) {
        toggleFavourite(isFavorite)
    }

    private fun convertPlaces(travelPlaces: List<TravelResult.Place?>?): List<PlaceData?> {
        return travelPlaces?.map { place ->
            PlaceData(
                id = place?.id,
                name = place?.name,
                description = place?.description,
                category = place?.categoryId?.name,
                point = place?.let { Point.fromLngLat(it.longitude!!, it.latitude!!) },
                imageUrl = place?.image?.get(place.image.size - 1)?.url
            )
        } ?: emptyList()
    }

    companion object {
        fun provideFactory(
            id: Int?
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                val sessionManager = application.sessionManager
                val responseHandler = ResponseHandler()
                TravelGalleryViewModel(
                    repository = repository,
                    sessionManager = sessionManager,
                    responseHandler = responseHandler,
                    id = id
                )
            }
        }
    }

    private fun processUnauthenticatedState(backendResponse: BackendResponse) {
        travelGalleryState = TravelGalleryState.Unauthenticated
        responseHandler.showMessage(backendResponse)
    }

    private fun processTravelSuccessState(backendResponse: BackendResponse) {
        travelGalleryState = TravelGalleryState.TravelSuccess
        responseHandler.showMessage(backendResponse)
    }

    private fun processTravelDeletedState(backendResponse: BackendResponse) {
        travelGalleryState = TravelGalleryState.TravelDeleted
        responseHandler.showMessage(backendResponse)
    }

    private fun processErrorState(backendResponse: BackendResponse) {
        travelGalleryState = TravelGalleryState.Error
        responseHandler.showMessage(backendResponse)
    }

    private fun processAuthenticatedState(backendResponse: BackendResponse) {
        travelGalleryState = TravelGalleryState.Authenticated
        responseHandler.showMessage(backendResponse)
    }

    fun handleTravelGalleryState() {
        isBackendResponseVisible = when (travelGalleryState) {
            TravelGalleryState.Error -> true
            TravelGalleryState.Authenticated -> false
            TravelGalleryState.TravelDeleted -> true
            TravelGalleryState.TravelSuccess -> false
            TravelGalleryState.Unauthenticated -> true
            else -> false
        }
    }

    fun handleProcesses(navController: NavController) {
        when (travelGalleryState) {
            TravelGalleryState.Error -> {
                clearBackendResponse()
                travelGalleryState = TravelGalleryState.Idle
            }

            TravelGalleryState.Authenticated -> {
                clearBackendResponse()
                travelGalleryState = TravelGalleryState.Idle
            }

            TravelGalleryState.TravelDeleted -> {
                clearBackendResponse()
                refreshLists()
                travelGalleryState = TravelGalleryState.Idle
            }

            TravelGalleryState.TravelSuccess -> {
                clearBackendResponse()
                travelGalleryState = TravelGalleryState.Idle
            }

            TravelGalleryState.Unauthenticated -> {
                clearBackendResponse()
                logoutProcess(navController = navController)
            }

            TravelGalleryState.Idle -> {}
        }
    }

    private fun clearBackendResponse() {
        isBackendResponseVisible = false
        responseHandler.clearMessage()
    }
}