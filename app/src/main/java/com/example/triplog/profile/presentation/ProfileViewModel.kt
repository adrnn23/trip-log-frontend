package com.example.triplog.profile.presentation

import android.annotation.SuppressLint
import android.content.Context
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
import com.example.triplog.R
import com.example.triplog.main.BackendResponse
import com.example.triplog.main.LoadingState
import com.example.triplog.main.ResponseHandler
import com.example.triplog.main.SessionManager
import com.example.triplog.main.TripLogApplication
import com.example.triplog.main.navigation.Screen
import com.example.triplog.network.InterfaceRepository
import com.example.triplog.profile.components.showToast
import com.example.triplog.profile.data.LinkData
import com.example.triplog.profile.data.profile.GetFriendsListResult
import com.example.triplog.profile.data.profile.GetFriendsRequestsResult
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

sealed class UserProfileSection {
    data object Main : UserProfileSection()
    data object FriendsList : UserProfileSection()
    data object FriendsRequests : UserProfileSection()
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

@SuppressLint("MutableCollectionMutableState")
class ProfileViewModel(
    private val repository: InterfaceRepository,
    val token: String?,
    val sessionManager: SessionManager,
    val responseHandler: ResponseHandler
) :
    ViewModel() {

    var profileState by mutableStateOf<ProfileState>(ProfileState.Idle)
    var profileSection by mutableStateOf<UserProfileSection>(UserProfileSection.Main)
    var loadingState: LoadingState by mutableStateOf(LoadingState.NotLoaded)
    var userProfile by mutableStateOf(UserProfileData())

    var friendsList by mutableStateOf(mutableListOf<GetFriendsListResult.Data?>())
    var friendsRequests by mutableStateOf(mutableListOf<GetFriendsRequestsResult.Data?>())

    /**
     * Flags which are used by UI to display individual parts of the UI such as dialogues, loading effect.
     */
    var isLogoutDialogVisible by mutableStateOf(false)
    var isDeleteFriendDialogVisible by mutableStateOf(false)
    var isProgressIndicatorVisible by mutableStateOf(false)
    var isBackendResponseVisible by mutableStateOf(false)
    var isOwnProfile by mutableStateOf(false)

    fun initParams(id: Int?) {
        getUserProfileResult()
        profileSection = UserProfileSection.Main
        userProfile.id = id
        isOwnProfile = id == sessionManager.getUserId()
        if(isOwnProfile){
            getFriendsListResult()
            getFriendsRequests()
        }
    }

    companion object {
        fun provideFactory(
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                val sessionManager = application.sessionManager
                val responseHandler = ResponseHandler()
                ProfileViewModel(
                    repository = repository,
                    token = sessionManager.getToken(),
                    sessionManager = sessionManager,
                    responseHandler = responseHandler,
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

    /**
     * Process state functions are responsible for:
     * - setting the message from server in responseHandler,
     * - setting the profile state in viewModel.
     */
    private fun processUnauthenticatedState(backendResponse: BackendResponse) {
        profileState = ProfileState.Unauthenticated
        responseHandler.showMessage(backendResponse)
    }

    private fun processErrorState(backendResponse: BackendResponse) {
        profileState = ProfileState.Error
        responseHandler.showMessage(backendResponse)
    }

    private fun processLoggedOutState(backendResponse: BackendResponse) {
        profileState = ProfileState.LoggedOut
        responseHandler.showMessage(backendResponse)
    }

    private fun processAuthenticatedState(backendResponse: BackendResponse) {
        profileState = ProfileState.Authenticated
        responseHandler.showMessage(backendResponse)
    }

    /**
     * Function handleProfileState() sets the viewModel's flags based on the viewModel's profileState.
     */
    fun handleProfileState() {
        isBackendResponseVisible = when (profileState) {
            ProfileState.Error -> true
            ProfileState.Authenticated -> false
            ProfileState.Unauthenticated -> true
            ProfileState.LoggedOut -> true
            else -> false
        }
    }

    fun handleProcesses(navController: NavController) {
        when (profileState) {
            ProfileState.Error -> {
                isBackendResponseVisible = false
                responseHandler.clearMessage()
            }

            ProfileState.Authenticated -> {
                isBackendResponseVisible = false
                responseHandler.clearMessage()
            }

            ProfileState.Unauthenticated -> {
                isBackendResponseVisible = false
                responseHandler.clearMessage()
                logoutProcess(navController = navController)
            }

            ProfileState.LoggedOut -> {
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

    /**
     * Function handleLoadingState() sets the flag isProgressIndicatorVisible on true if viewModel uses a network operation,
     * such as retrieving data from a server.
     */
    fun handleLoadingState() {
        isProgressIndicatorVisible =
            loadingState == LoadingState.Loading
    }

    fun logout() {
        viewModelScope.launch {
            try {
                val result = repository.getLogoutResult(sessionManager.getToken())
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

    private fun getUserProfileResult() {
        loadingState = LoadingState.Loading
        val token = sessionManager.getToken()
        viewModelScope.launch {
            delay(250)
            try {
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
                        val backendResponse = BackendResponse()
                        processAuthenticatedState(backendResponse)
                        isOwnProfile = userProfile.id == sessionManager.getUserId()
                    } else if (result.resultCode == 401) {
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

    private fun getFriendsListResult() {
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result = repository.getFriendsList(token)
                if (result != null) {
                    if (result.resultCode == 200 && result.data != null) {
                        friendsList = result.data.toMutableList()
                        val backendResponse = BackendResponse()
                        processAuthenticatedState(backendResponse)
                    } else if (result.resultCode == 401) {
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
        }
    }

    private fun getFriendsRequests() {
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result = repository.getFriendsRequests(token)
                if (result != null) {
                    if (result.resultCode == 200 && result.data != null) {
                        friendsRequests = result.data.toMutableList()
                        val backendResponse = BackendResponse()
                        processAuthenticatedState(backendResponse)
                    } else if (result.resultCode == 401) {
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
        }
    }

    private fun refreshFriendsRequests() {
        getFriendsRequests()
    }

    fun refreshFriendsList() {
        getFriendsListResult()
    }


    fun acceptFriendRequest(requestId: Int, context: Context) {
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result = repository.acceptFriendRequest(token, requestId)
                when (result?.resultCode) {
                    200 -> {
                        val backendResponse = BackendResponse()
                        processAuthenticatedState(backendResponse)
                        refreshFriendsRequests()
                        refreshFriendsList()
                        showToast(context, R.string.userAddedToFriends)
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
                        refreshFriendsRequests()
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
                processUnauthenticatedState(backendResponse)
            }
        }
    }

    fun deleteFriend(friendId: Int, context: Context) {
        loadingState = LoadingState.Loading
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result = repository.deleteFriend(token, friendId)
                when (result?.resultCode) {
                    200 -> {
                        val backendResponse = BackendResponse()
                        processAuthenticatedState(backendResponse)
                        refreshFriendsList()
                        showToast(context, R.string.userDeleted)
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
                processUnauthenticatedState(backendResponse)
            }
            loadingState = LoadingState.Loaded
        }
    }
}