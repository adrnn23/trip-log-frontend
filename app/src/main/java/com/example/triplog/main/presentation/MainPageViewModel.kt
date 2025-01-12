package com.example.triplog.main.presentation

import android.annotation.SuppressLint
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
import com.example.triplog.main.data.SearchFilters
import com.example.triplog.main.data.SearchProfilesResult
import com.example.triplog.main.data.TimelineResult.TimelineTravel
import com.example.triplog.main.data.UserID
import com.example.triplog.main.navigation.Screen
import com.example.triplog.network.InterfaceRepository
import com.example.triplog.network.MapboxClient
import com.example.triplog.travel.data.PlaceData
import com.example.triplog.travel.data.TravelData
import com.example.triplog.travel.data.TravelResult
import com.mapbox.geojson.Point
import kotlinx.coroutines.launch

sealed class MainPageState {
    data object Authenticated : MainPageState()
    data object Idle : MainPageState()
    data object Unauthenticated : MainPageState()
    data object Error : MainPageState()
    data object AuthenticationError : MainPageState()
    data object LoggedOut : MainPageState()
}

sealed class MainPageSection {
    data object Main : MainPageSection()
    data object SearchSection : MainPageSection()
    data object TravelPostOverviewSection : MainPageSection()
}

data class AuthenticatedUserProfileData(
    var username: String? = "",
    var id: Int? = null,
    var email: String? = "",
)

@SuppressLint("MutableCollectionMutableState")
class MainPageViewModel(
    private val repository: InterfaceRepository,
    val sessionManager: SessionManager,
    val responseHandler: ResponseHandler,
    val mapboxClient: MapboxClient
) :
    ViewModel() {

    var mainPageState: MainPageState by mutableStateOf(MainPageState.Idle)
    var mainPageSection: MainPageSection by mutableStateOf(MainPageSection.Main)
    var loadingState: LoadingState by mutableStateOf(LoadingState.NotLoaded)
    var travelOverview by mutableStateOf(TravelData())
    var timelineTravels by mutableStateOf<List<TimelineTravel>>(emptyList())
    var searchedTravels by mutableStateOf<List<TimelineTravel>>(emptyList())
    var currentPageOfTimelineTravels by mutableIntStateOf(1)
    var totalPagesOfTimelineTravels by mutableIntStateOf(0)
    var selectedFilters by mutableStateOf(SearchFilters())
    var query by mutableStateOf("")
    var searchedProfilesCurrentPage by mutableIntStateOf(1)
    var searchedProfilesTotalPages by mutableIntStateOf(0)
    var searchedTravelsCurrentPage by mutableIntStateOf(1)
    var searchedTravelsTotalPages by mutableIntStateOf(0)
    var searchedProfiles by mutableStateOf<List<SearchProfilesResult.Data>?>(emptyList())

    private var authenticatedUserProfile: AuthenticatedUserProfileData by mutableStateOf(
        AuthenticatedUserProfileData()
    )

    /**
     * Flags which are used by UI to display individual parts of the UI such as dialogues, loading effect.
     */
    var isProgressIndicatorVisible by mutableStateOf(false)
    var isBackendResponseVisible by mutableStateOf(false)
    var isLogoutDialogVisible by mutableStateOf(false)

    init {
        getAuthenticatedUserProfileData()
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                val sessionManager = application.sessionManager
                val responseHandler = ResponseHandler()
                val mapboxClient = application.mapboxClient
                MainPageViewModel(
                    repository = repository,
                    sessionManager = sessionManager,
                    responseHandler = responseHandler,
                    mapboxClient = mapboxClient
                )
            }
        }
    }

    fun prepareTempTravelDataToSharedVM(): TravelData {
        val travel = travelOverview
        travel.places = travelOverview.places
        travel.image = travelOverview.image
        return travel
    }

    fun handleLoadingState() {
        isProgressIndicatorVisible =
            loadingState == LoadingState.Loading
    }

    private fun logoutProcess(navController: NavController) {
        sessionManager.clearToken()
        navController.navigate(Screen.LoginScreen.destination)
    }

    private fun getAuthenticatedUserProfileData() {
        loadingState = LoadingState.Loading
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result = repository.getAuthenticatedUserProfileResult(token)
                if ((result?.resultCode == 200) && (result.id != null) && (result.name != null) && (result.email != null)) {
                    authenticatedUserProfile =
                        AuthenticatedUserProfileData(result.name, result.id, result.email)
                    sessionManager.saveUserId(result.id)
                    sessionManager.saveUserEmail(result.email)
                    sessionManager.saveUserName(result.name)
                    mainPageState = MainPageState.Authenticated
                    val backendResponse = BackendResponse(message = null)
                    processAuthenticatedState(backendResponse)
                    loadTimelineTravels()
                } else if (result?.resultCode == 401) {
                    val backendResponse = BackendResponse(message = result.message)
                    processUnauthenticatedState(backendResponse)
                } else {
                    val backendResponse = BackendResponse(message = result?.message)
                    processAuthenticationErrorState(backendResponse)
                }
            } catch (e: Exception) {
                val backendResponse = BackendResponse(message = e.message)
                processAuthenticationErrorState(backendResponse)
            }
            loadingState = LoadingState.Loaded
        }
    }

    fun loadTimelineTravels(
        page: Int = 1,
        dateFrom: String = "",
        dateTo: String = "",
        sortDirection: String = ""
    ) {
        loadingState = LoadingState.Loading
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result = repository.getTimeline(token, page, dateFrom, dateTo, sortDirection)
                if (result != null) {
                    if (result.resultCode == 200 && result.data != null) {
                        timelineTravels = result.data
                        currentPageOfTimelineTravels = page
                        totalPagesOfTimelineTravels = result.meta?.lastPage ?: 1
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

    fun searchTravels(page: Int) {
        loadingState = LoadingState.Loading
        isProgressIndicatorVisible = true

        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {

                val sortingDirection = when (selectedFilters.sortingDirection) {
                    "Ascending" -> "asc"
                    "Descending" -> "desc"
                    else -> {
                        "asc"
                    }
                }

                val result = repository.getTimeline(token, page, selectedFilters.dateFrom, selectedFilters.dateTo, sortingDirection)
                if ((result?.resultCode == 200) && (result.data != null)) {
                    searchedTravels = result.data
                    searchedTravelsCurrentPage = page
                    searchedTravelsTotalPages = result.meta?.lastPage ?: 1
                    mainPageState = MainPageState.Authenticated
                } else if (result?.resultCode == 401) {
                    processUnauthenticatedState(BackendResponse(message = result.message))
                } else {
                    processErrorState(BackendResponse(message = result?.message))
                }
            } catch (e: Exception) {
                processErrorState(BackendResponse(message = e.message))
            } finally {
                loadingState = LoadingState.Loaded
                isProgressIndicatorVisible = false
            }
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
                        travelOverview.imageUrl = result.image?.get(result.image.size - 1)?.url
                        travelOverview.timeAgo = result.created
                        travelOverview.places = convertPlaces(result.places)
                        if (result.longitude != null && result.latitude != null)
                            travelOverview.point =
                                Point.fromLngLat(result.longitude!!, result.latitude!!)
                        mainPageSection = MainPageSection.TravelPostOverviewSection
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

    fun logout() {
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result = repository.getLogoutResult(token)
                if ((result?.resultCode == 200) && (result.message != null)) {
                    val backendResponse = BackendResponse(message = result.message)
                    processLoggedOutState(backendResponse)
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

    fun searchProfiles(page: Int) {
        if (loadingState == LoadingState.Loading || query.isBlank() || page < 1) return

        loadingState = LoadingState.Loading
        isProgressIndicatorVisible = true

        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result = repository.getSearchProfilesResult(token, query, page)
                if ((result?.resultCode == 200) && (result.data != null)) {
                    searchedProfiles = result.data as List<SearchProfilesResult.Data>?
                    searchedProfilesCurrentPage = page
                    searchedProfilesTotalPages = result.meta?.lastPage ?: 1
                    mainPageState = MainPageState.Authenticated
                } else if (result?.resultCode == 401) {
                    processUnauthenticatedState(BackendResponse(message = result.message))
                } else {
                    processErrorState(BackendResponse(message = result?.message))
                }
            } catch (e: Exception) {
                processErrorState(BackendResponse(message = e.message))
            } finally {
                loadingState = LoadingState.Loaded
                isProgressIndicatorVisible = false
            }
        }
    }

    fun loadNextPage() {
        when(selectedFilters.searchType){
            "Users" -> {
                if (searchedProfilesCurrentPage < searchedProfilesTotalPages) {
                    searchProfiles(searchedProfilesCurrentPage + 1)
                }
            }
            "Travels" -> {
                if (searchedTravelsCurrentPage < searchedTravelsTotalPages) {
                    searchTravels(searchedProfilesCurrentPage + 1)
                }
            }
        }
    }

    fun loadPreviousPage() {
        when(selectedFilters.searchType){
            "Users" -> {
                if (searchedProfilesCurrentPage > 1) {
                    searchProfiles(searchedProfilesCurrentPage - 1)
                }
            }
            "Travels" -> {
                if (searchedTravelsCurrentPage > 1) {
                    searchTravels(searchedProfilesCurrentPage - 1)
                }
            }
        }
    }

    fun acceptFriendRequest(requestId: Int) {
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result = repository.acceptFriendRequest(token, requestId)
                when (result?.resultCode) {
                    200 -> {
                        val backendResponse = BackendResponse()
                        processAuthenticatedState(backendResponse)
                        searchProfiles(searchedProfilesCurrentPage)
                    }

                    401 -> {
                        val backendResponse = BackendResponse(message = result.message)
                        processUnauthenticatedState(backendResponse)
                    }

                    else -> {
                        val backendResponse = BackendResponse(message = result?.message)
                        processErrorState(backendResponse)
                    }
                }
            } catch (e: Exception) {
                val backendResponse = BackendResponse(message = e.message)
                processErrorState(backendResponse)
            }
        }
    }

    fun rejectFriendRequest(requestId: Int) {
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result = repository.rejectFriendRequest(token, requestId)
                when (result?.resultCode) {
                    200 -> {
                        val backendResponse = BackendResponse()
                        processAuthenticatedState(backendResponse)
                        searchProfiles(searchedProfilesCurrentPage)
                    }

                    401 -> {
                        val backendResponse = BackendResponse(message = result.message)
                        processUnauthenticatedState(backendResponse)
                    }

                    else -> {
                        val backendResponse = BackendResponse(message = result?.message)
                        processErrorState(backendResponse)
                    }
                }
            } catch (e: Exception) {
                val backendResponse = BackendResponse(message = e.message)
                processErrorState(backendResponse)
            }
        }
    }

    fun sendFriendRequest(userId: Int) {
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val userID = UserID(userId = userId)
                val result = repository.sendFriendRequest(token, userID)
                when (result?.resultCode) {
                    200 -> {
                        val backendResponse = BackendResponse()
                        processAuthenticatedState(backendResponse)
                        searchProfiles(searchedProfilesCurrentPage)
                    }

                    401 -> {
                        val backendResponse = BackendResponse(message = result.message)
                        processUnauthenticatedState(backendResponse)
                    }

                    else -> {
                        val backendResponse = BackendResponse(message = result?.message)
                        processErrorState(backendResponse)
                    }
                }
            } catch (e: Exception) {
                val backendResponse = BackendResponse(message = e.message)
                processErrorState(backendResponse)
            }
        }
    }

    private fun processUnauthenticatedState(backendResponse: BackendResponse) {
        mainPageState = MainPageState.Unauthenticated
        responseHandler.showMessage(backendResponse)
    }

    private fun processErrorState(backendResponse: BackendResponse) {
        mainPageState = MainPageState.Error
        responseHandler.showMessage(backendResponse)
    }

    private fun processLoggedOutState(backendResponse: BackendResponse) {
        mainPageState = MainPageState.LoggedOut
        responseHandler.showMessage(backendResponse)
    }

    private fun processAuthenticatedState(backendResponse: BackendResponse) {
        mainPageState = MainPageState.Authenticated
        responseHandler.showMessage(backendResponse)
    }

    private fun processAuthenticationErrorState(backendResponse: BackendResponse) {
        mainPageState = MainPageState.AuthenticationError
        responseHandler.showMessage(backendResponse)
    }

    fun handleMainPageState() {
        isBackendResponseVisible = when (mainPageState) {
            MainPageState.Error -> true
            MainPageState.Authenticated -> false
            MainPageState.Unauthenticated -> true
            MainPageState.LoggedOut -> true
            MainPageState.AuthenticationError -> true
            else -> false
        }
    }

    fun handleProcesses(navController: NavController) {
        when (mainPageState) {
            MainPageState.Error -> {
                clearBackendResponse()
                mainPageState = MainPageState.Idle
            }

            MainPageState.Authenticated -> {
                clearBackendResponse()
                mainPageState = MainPageState.Idle
            }

            MainPageState.Unauthenticated -> {
                clearBackendResponse()
                logoutProcess(navController = navController)
            }

            MainPageState.LoggedOut -> {
                clearBackendResponse()
                logoutProcess(navController = navController)
            }

            MainPageState.AuthenticationError -> {
                clearBackendResponse()
                logoutProcess(navController = navController)
            }

            MainPageState.Idle -> {}
        }
    }

    private fun clearBackendResponse() {
        isBackendResponseVisible = false
        responseHandler.clearMessage()
    }
}