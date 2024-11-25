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
import com.example.triplog.main.data.SearchProfilesResult
import com.example.triplog.main.data.UserID
import com.example.triplog.main.navigation.Screen
import com.example.triplog.network.InterfaceRepository
import kotlinx.coroutines.delay
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
    val responseHandler: ResponseHandler
) :
    ViewModel() {

    var mainPageState: MainPageState by mutableStateOf(MainPageState.Idle)
    var mainPageSection: MainPageSection by mutableStateOf(MainPageSection.Main)
    var loadingState: LoadingState by mutableStateOf(LoadingState.NotLoaded)

    private var authenticatedUserProfile: AuthenticatedUserProfileData by mutableStateOf(
        AuthenticatedUserProfileData()
    )

    /**
     * Flags which are used by UI to display individual parts of the UI such as dialogues, loading effect.
     */
    var isProgressIndicatorVisible by mutableStateOf(false)
    var isBackendResponseVisible by mutableStateOf(false)
    var isLogoutDialogVisible by mutableStateOf(false)

    var query by mutableStateOf("")
    var page by mutableIntStateOf(1)
    var searchedProfilesList by mutableStateOf<MutableList<SearchProfilesResult.Data?>?>(null)

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                val sessionManager = application.sessionManager
                val responseHandler = ResponseHandler()
                MainPageViewModel(
                    repository = repository,
                    sessionManager = sessionManager,
                    responseHandler = responseHandler
                )
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
                isBackendResponseVisible = false
                responseHandler.clearMessage()
            }

            MainPageState.Authenticated -> {
                isBackendResponseVisible = false
                responseHandler.clearMessage()
            }

            MainPageState.Unauthenticated -> {
                isBackendResponseVisible = false
                responseHandler.clearMessage()
                logoutProcess(navController = navController)
            }

            MainPageState.LoggedOut -> {
                isBackendResponseVisible = false
                responseHandler.clearMessage()
                logoutProcess(navController = navController)
            }

            MainPageState.AuthenticationError -> {
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

    fun handleLoadingState() {
        isProgressIndicatorVisible =
            loadingState == LoadingState.Loading
    }

    private fun logoutProcess(navController: NavController) {
        sessionManager.clearToken()
        navController.navigate(Screen.LoginScreen.destination)
    }

    fun getAuthenticatedUserProfileData() {
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

    fun getSearchProfilesResult() {
        loadingState = LoadingState.Loading
        searchedProfilesList?.clear()
        val token = sessionManager.getToken()
        viewModelScope.launch {
            delay(100)
            try {
                val result = repository.getSearchProfilesResult(token, query, page)
                if ((result?.resultCode == 200) && (result.data != null)) {
                    searchedProfilesList =
                        result.data as MutableList<SearchProfilesResult.Data?>?
                    val backendResponse = BackendResponse()
                    processAuthenticatedState(backendResponse)
                } else if (result?.resultCode == 401) {
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
            delay(100)
            loadingState = LoadingState.Loaded
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
                        getSearchProfilesResult()
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
                        getSearchProfilesResult()
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
                        getSearchProfilesResult()
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
}