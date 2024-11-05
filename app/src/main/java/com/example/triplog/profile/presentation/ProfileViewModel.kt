package com.example.triplog.profile.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Link
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
import com.example.triplog.main.presentation.BackendResponse
import com.example.triplog.main.states.LoadingState
import com.example.triplog.network.InterfaceRepository
import com.example.triplog.profile.data.LinkData
import com.example.triplog.profile.data.profile.UserProfileResult.TravelPreference
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class ProfileState {
    data object Authenticated : ProfileState()
    data object Unauthenticated : ProfileState()
    data object Idle : ProfileState()
    data object Error : ProfileState()
    data object LoggedOut : ProfileState()
}

data class UserProfileData(
    var username: String? = "",
    var id: Int? = null,
    var bio: String? = "",
    var email: String? = "",
    var tripsCount: Int? = null,
    var plannedCount: Int? = null,
    var favoriteCount: Int? = 0,
    var travelPreferences: List<TravelPreference?>? = emptyList(),
    var links: MutableList<LinkData?> = emptyList<LinkData?>().toMutableList()
)


class ProfileViewModel(
    private val repository: InterfaceRepository,
    val token: String?,
    private val sessionManager: SessionManager
) :
    ViewModel() {

    var profileState by mutableStateOf<ProfileState>(ProfileState.Idle)
    var loadingState: LoadingState by mutableStateOf(LoadingState.NotLoaded)

    var isProgressIndicatorVisible by mutableStateOf(false)
    var isBackendResponseVisible by mutableStateOf(false)

    var userProfile by mutableStateOf(UserProfileData())
    var backendResponse = mutableStateOf(BackendResponse())

    fun initParams(iId: Int?, iEmail: String?) {
        userProfile.id = iId
        userProfile.email = iEmail
        getUserProfileResult()
    }

    fun clearBackendResponse(){
        backendResponse.value.clearResponse()
    }

    companion object {
        fun provideFactory(
            token: String?,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                val sessionManager = application.sessionManager
                ProfileViewModel(
                    repository = repository,
                    token = token,
                    sessionManager = sessionManager
                )
            }
        }
    }

    private fun linksInit(facebookLink: String?, instagramLink: String?, xLink: String?) {
        if (facebookLink != null) {
            userProfile.links.add(LinkData("Facebook", Icons.Default.Facebook, facebookLink))
        }
        if (instagramLink != null) {
            userProfile.links.add(LinkData("Instagram", Icons.Default.Link, instagramLink))
        }
        if (xLink != null) {
            userProfile.links.add(LinkData("X", Icons.Default.Link, xLink))
        }
    }

    fun logout() {
        loadingState = LoadingState.Loading
        viewModelScope.launch {
            delay(250)
            try {
                val result = repository.getLogoutResult(token)
                if ((result?.resultCode == 200) && (result.message != null)) {
                    profileState = ProfileState.LoggedOut
                    backendResponse.value.message= result.message + "\nYou will be redirected to the login screen."
                    backendResponse.value.buildBackendResponse()
                } else if (result?.resultCode == 401 && result.message != null) {
                    profileState = ProfileState.Unauthenticated
                    backendResponse.value.message= result.message + "\nYou will be redirected to the login screen."
                    backendResponse.value.buildBackendResponse()
                } else {
                    profileState = ProfileState.Error
                    backendResponse.value.message= result?.message
                    backendResponse.value.buildBackendResponse()
                }
            } catch (e: Exception) {
                profileState = ProfileState.Error
                backendResponse.value.message= e.message
                backendResponse.value.buildBackendResponse()
            }
            delay(250)
            loadingState = LoadingState.Loaded
        }
    }

    private fun getUserProfileResult() {
        loadingState = LoadingState.Loading
        viewModelScope.launch {
            delay(250)
            try {
                val token = sessionManager.getToken()
                val result = repository.getUserProfileResult(token, userProfile.id!!)
                if (result != null) {
                    if (result.resultCode == 200 && result.id != null && result.name != null) {
                        userProfile = UserProfileData(
                            username = result.name,
                            id = result.id,
                            bio = result.bio,
                            email = userProfile.email,
                            tripsCount = result.tripsCount ?: 0,
                            plannedCount = result.plannedTripsCount ?: 0,
                            travelPreferences = result.travelPreferences
                        )
                        linksInit(result.facebookLink, result.instagramLink, result.xLink)
                        profileState = ProfileState.Authenticated
                    } else if (result.resultCode == 401) {
                        profileState = ProfileState.Unauthenticated
                        backendResponse.value.message= result.message + "\nYou will be redirected to the login screen."
                        backendResponse.value.buildBackendResponse()
                    } else {
                        profileState = ProfileState.Error
                        backendResponse.value.message= result.message
                        backendResponse.value.buildBackendResponse()
                    }
                }
            } catch (e: Exception) {
                profileState = ProfileState.Error
                backendResponse.value.message= e.message
                backendResponse.value.buildBackendResponse()
            }
            delay(250)
            loadingState = LoadingState.Loaded
        }
    }

    fun logoutProcess(navController: NavController) {
        isBackendResponseVisible = false
        sessionManager.clearToken()
        navController.popBackStack()
        navController.navigate(Screen.LoginScreen.destination)
    }

    fun homeReturnProcess(navController: NavController) {
        isBackendResponseVisible = false
        navController.popBackStack()
        navController.navigate(Screen.MainPageScreen.destination)
    }

    fun handleProfileState() {
        when (profileState) {
            ProfileState.Error ->
                isBackendResponseVisible = true

            ProfileState.Unauthenticated ->
                isBackendResponseVisible = true

            ProfileState.Authenticated ->
                isBackendResponseVisible = false

            ProfileState.LoggedOut ->
                isBackendResponseVisible = true

            ProfileState.Idle -> {}
        }
    }

    fun handleLoadingState() {
        isProgressIndicatorVisible =
            loadingState == LoadingState.Loading
    }
}