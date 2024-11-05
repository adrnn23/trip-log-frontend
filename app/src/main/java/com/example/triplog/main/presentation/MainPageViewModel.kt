package com.example.triplog.main.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.example.triplog.main.SessionManager
import com.example.triplog.main.TripLogApplication
import com.example.triplog.main.navigation.Screen
import com.example.triplog.main.states.LoadingState
import com.example.triplog.network.InterfaceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class MainPageState {
    data object Authenticated : MainPageState()
    data object Idle : MainPageState()
    data object Unauthenticated : MainPageState()
    data object Error : MainPageState()
    data object LoggedOut : MainPageState()
}

data class AuthenticatedUserProfileData(
    var username: String? = "",
    var id: Int? = null,
    var email: String? = "",
)

class BackendResponse(
    var message: String? = "",
    private var errorList: MutableList<String?> = emptyList<String?>().toMutableList()
) {
    init {
        buildBackendResponse()
    }

    var errors: String = ""

    fun addError(error: String?) {
        errorList.add(error)
    }

    fun buildBackendResponse() {
        errors = message + "\n"
        errorList.forEach { item ->
            errors = "$errors- $item\n"
        }
    }

    fun clearResponse(){
        errorList.clear()
        errors = ""
        message = ""
    }
}

class MainPageViewModel(
    private val repository: InterfaceRepository,
    val sessionManager: SessionManager
) :
    ViewModel() {

    var mainPageState: MainPageState by mutableStateOf(MainPageState.Idle)
    var loadingState: LoadingState by mutableStateOf(LoadingState.NotLoaded)

    var authenticatedUserProfile: AuthenticatedUserProfileData by mutableStateOf(
        AuthenticatedUserProfileData()
    )
    var backendResponse = mutableStateOf(BackendResponse())
    var isProgressIndicatorVisible by mutableStateOf(false)
    var isBackendResponseVisible by mutableStateOf(false)

    fun clearBackendResponse(){
        backendResponse.value.clearResponse()
    }

    fun getAuthenticatedUserProfileData() {
        loadingState = LoadingState.Loading
        val token = sessionManager.getToken()
        viewModelScope.launch {
            delay(250)
            try {
                val result = repository.getAuthenticatedUserProfileResult(token)
                if ((result?.resultCode == 200) && (result.id != null) && (result.name != null) && (result.email != null)) {
                    authenticatedUserProfile =
                        AuthenticatedUserProfileData(result.name, result.id, result.email)
                    mainPageState = MainPageState.Authenticated
                } else if (result?.resultCode == 401) {
                    mainPageState = MainPageState.Unauthenticated
                    backendResponse.value.message= result.message + "\nYou will be redirected to the login screen."
                    backendResponse.value.buildBackendResponse()
                } else {
                    mainPageState = MainPageState.Error
                    backendResponse.value.message= result?.message
                    backendResponse.value.buildBackendResponse()
                }
            } catch (e: Exception) {
                mainPageState = MainPageState.Error
                backendResponse.value.message= e.message
                backendResponse.value.buildBackendResponse()
            }
            delay(250)
            loadingState = LoadingState.Loaded
        }
    }

    fun logout() {
        loadingState = LoadingState.Loading
        val token = sessionManager.getToken()
        viewModelScope.launch {
            delay(250)
            try {
                val result = repository.getLogoutResult(token)
                if ((result?.resultCode == 200) && (result.message != null)) {
                    mainPageState = MainPageState.LoggedOut
                    backendResponse.value.message= result.message + "\nYou will be redirected to the login screen."
                    backendResponse.value.buildBackendResponse()
                } else if (result?.resultCode == 401 && result.message != null) {
                    mainPageState = MainPageState.Unauthenticated
                    backendResponse.value.message= result.message + "\nYou will be redirected to the login screen."
                    backendResponse.value.buildBackendResponse()
                } else {
                    mainPageState = MainPageState.Error
                    backendResponse.value.message= result?.message
                    backendResponse.value.buildBackendResponse()
                }
            } catch (e: Exception) {
                mainPageState = MainPageState.Error
                backendResponse.value.message= e.message
                backendResponse.value.buildBackendResponse()
            }
            delay(250)
            loadingState = LoadingState.Loaded
        }
    }

    fun handleLoadingState() {
        isProgressIndicatorVisible =
            loadingState == LoadingState.Loading
    }

    fun logoutProcess(navController: NavController) {
        isBackendResponseVisible = false
        clearBackendResponse()
        sessionManager.clearToken()
        navController.popBackStack()
        navController.navigate(Screen.LoginScreen.destination)
    }

    fun handleMainPageState(navController: NavController) {
        when (mainPageState) {
            MainPageState.Error -> {
                isBackendResponseVisible = true
            }

            MainPageState.Unauthenticated -> {
                isBackendResponseVisible = true
            }

            MainPageState.Authenticated -> {
                isBackendResponseVisible = false
            }

            MainPageState.Idle -> {}

            MainPageState.LoggedOut -> {
                isBackendResponseVisible = true
            }
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                val sessionManager = application.sessionManager
                MainPageViewModel(
                    repository = repository,
                    sessionManager = sessionManager
                )
            }
        }
    }
}