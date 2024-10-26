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
import com.example.triplog.authorization.login.presentation.LoadingState
import com.example.triplog.authorization.login.presentation.LoginState
import com.example.triplog.main.TripLogApplication
import com.example.triplog.network.InterfaceRepository
import com.example.triplog.profile.data.LinkData
import com.example.triplog.profile.data.profile.UserProfileResult.TravelPreference
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: InterfaceRepository, val token: String?) :
    ViewModel() {

    init {
        getAuthenticatedUserProfileData()
    }

    var loginState: LoginState by mutableStateOf(LoginState.NotLogged)
    var loadingState: LoadingState by mutableStateOf(LoadingState.NotLoaded)
    var isProgressIndicatorVisible by mutableStateOf(false)


    var username by mutableStateOf<String?>(null)
    var id by mutableStateOf<Int?>(null)
    var bio by mutableStateOf<String?>(null)
    var tripsCount by mutableStateOf<Int?>(null)
    var plannedCount by mutableStateOf<Int?>(null)
    var favoriteCount by mutableStateOf<Int?>(null)
    var travelPreferences: List<TravelPreference?>? = listOf()
    var links = mutableListOf<LinkData?>(null)
    var email by mutableStateOf<String?>(null)

    companion object {
        fun provideFactory(
            token: String?,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                ProfileViewModel(repository = repository, token = token)
            }
        }
    }

    private fun getAuthenticatedUserProfileData() {
        viewModelScope.launch {
            try {
                val result = repository.getAuthenticatedUserProfileResult(token)
                if ((result?.resultCode == 200) && (result.id != null) && (result.name != null) && (result.email != null)) {
                    username = result.name
                    email = result.email
                    id = result.id
                    loginState = LoginState.Logged
                    getUserProfileResult()
                } else if (result?.resultCode == 401 && result.message != null) {
                    loginState = LoginState.Unauthorized
                } else {
                    loginState = LoginState.NotLogged
                }
            } catch (e: Exception) {
                loginState = LoginState.Error
            }
        }
    }

    fun linksInit(facebookLink: String?, instagramLink: String?, xLink: String?) {
        if (facebookLink != null) {
            links.add(LinkData("Facebook", Icons.Default.Facebook, facebookLink))
        }
        if (instagramLink != null) {
            links.add(LinkData("Instagram", Icons.Default.Link, instagramLink))
        }
        if (xLink != null) {
            links.add(LinkData("X", Icons.Default.Link, xLink))
        }
    }
    private fun getUserProfileResult() {
        loadingState = LoadingState.Loading
        viewModelScope.launch {
            try {
                val result = repository.getUserProfileResult(token, id!!)
                if (result != null) {
                    if (result.resultCode == 200 && (result.id != null) && (result.name != null)) {
                        username = result.name
                        id = result.id
                        bio = result.bio
                        travelPreferences = result.travelPreferences
                        tripsCount = result.tripsCount!!
                        plannedCount = result.plannedTripsCount!!
                        favoriteCount = 0
                        linksInit(result.facebookLink, result.instagramLink, result.xLink)
                        loginState = LoginState.Logged
                    } else if (result.resultCode == 401 && result.message != null) {

                    } else if (result.resultCode == 404 && result.message != null) {

                    } else {

                    }
                }
            } catch (e: Exception) {
                loginState = LoginState.Error
            } finally {
                delay(200)
                loadingState = LoadingState.Loaded
            }
        }
    }

    fun handleLoadingState() {
        isProgressIndicatorVisible =
            loadingState == LoadingState.Loading
    }
}