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
import com.example.triplog.profile.data.ErrorData
import com.example.triplog.profile.data.profile.EditUserProfileRequest
import com.example.triplog.profile.data.profile.EditUserProfileResult
import com.example.triplog.profile.data.profile.TravelPreferencesResult
import com.example.triplog.profile.data.profile.UserProfileResult.TravelPreference
import com.example.triplog.profile.data.updatePassword.UpdatePasswordRequest
import com.example.triplog.profile.data.updatePassword.UpdatePasswordResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class EditProfileState {
    data object NotUpdated : EditProfileState()
    data object Updated : EditProfileState()
    data object ProfileLoadingError : EditProfileState()
    data object Unauthenticated : EditProfileState()
    data object Authenticated : EditProfileState()
    data object LoggedOut : EditProfileState()
    data object ValidationError : EditProfileState()
    data object Error : EditProfileState()
}

sealed class EditUserProfileSection {
    data object Main : EditUserProfileSection()
    data object EditTravelPreferences : EditUserProfileSection()
    data object EditBiography : EditUserProfileSection()
    data object UpdatePassword : EditUserProfileSection()
    data object EditBasicInformation : EditUserProfileSection()
}

sealed class UpdatePasswordState {
    data object NotUpdated : UpdatePasswordState()
    data object Updated : UpdatePasswordState()
    data object Error : UpdatePasswordState()
    data object Unauthenticated : UpdatePasswordState()
}

data class EditProfileData(
    var avatar: String? = "",
    var bio: String? = "",
    var email: String? = "",
    var name: String? = "",
    var id: Int? = null,
    var facebookLink: String? = "",
    var instagramLink: String? = "",
    var xLink: String? = ""
)

class EditProfileViewModel(
    private val repository: InterfaceRepository,
    val sessionManager: SessionManager
) :
    ViewModel() {

    fun initParams(iId: Int?, iEmail: String?) {
        editProfile.id = iId
        editProfile.email = iEmail
        getUserProfileResult()
    }

    var isProgressIndicatorVisible by mutableStateOf(false)

    var editProfileResult by mutableStateOf<EditUserProfileResult?>(null)
    var updatePasswordResult by mutableStateOf<UpdatePasswordResult?>(null)
    var editProfile by mutableStateOf(EditProfileData())

    var editProfileState by mutableStateOf<EditProfileState>(EditProfileState.NotUpdated)
    var updatePasswordState by mutableStateOf<UpdatePasswordState>(UpdatePasswordState.NotUpdated)
    var loadingState: LoadingState by mutableStateOf(LoadingState.NotLoaded)
    var section by mutableStateOf<EditUserProfileSection>(EditUserProfileSection.Main)

    var currentPassword by mutableStateOf<String?>("")
    var newPassword by mutableStateOf<String?>("")
    var repeatedNewPassword by mutableStateOf<String?>("")

    var backendResponse = mutableStateOf(BackendResponse())
    var isBackendResponseVisible by mutableStateOf(false)

    var links by mutableStateOf(
        mutableListOf(
            LinkData("Facebook", Icons.Default.Facebook, ""),
            LinkData("Instagram", Icons.Default.Link, ""),
            LinkData("X", Icons.Default.Link, "")
        )
    )

    private var facebookLink by mutableStateOf<String?>("")
    private var instagramLink by mutableStateOf<String?>("")
    private var xLink by mutableStateOf<String?>("")

    // Travel preferences result from backend server
    private var travelPreferencesResult by mutableStateOf<TravelPreferencesResult?>(null)

    // User's travel preferences
    private var userTravelPreferences: List<TravelPreference?>? = listOf()

    // Travel preferences lists used for operations in app
    var travelPreferencesList by mutableStateOf(mutableListOf<TravelPreference?>())
    var tempTravelPreferencesList by mutableStateOf(mutableListOf<TravelPreference?>())

    var usernameTemp by mutableStateOf("")
    var emailTemp by mutableStateOf("")
    var bioTemp by mutableStateOf("")


    var isUsernameDialogVisible by mutableStateOf(false)
    var isEmailDialogVisible by mutableStateOf(false)
    var isSaveChangesDialogVisible by mutableStateOf(false)
    var isDeleteLinkDialogVisible by mutableStateOf(false)
    var isAddLinkDialogVisible by mutableStateOf(false)

    var errorMessage by mutableStateOf(ErrorData(false, null, ""))

    fun clearBackendResponse() {
        backendResponse.value.clearResponse()
    }

    companion object {
        fun provideFactory(
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                val sessionManager = application.sessionManager
                EditProfileViewModel(repository = repository, sessionManager = sessionManager)
            }
        }
    }

    fun addNewLink(
        site: String,
        link: String,
        onShowDialogChange: (Boolean) -> Unit,
        onClearInputs: () -> Unit,
        onErrorValidation: () -> Unit,
    ) {
        if (site.isNotEmpty() && link.isNotEmpty()) {
            var formattedLink = link.lowercase()
            if (!formattedLink.startsWith("https://")) {
                formattedLink = "https://$formattedLink"
            }
            when (site) {
                "Facebook" -> links[0] = LinkData(site, Icons.Default.Facebook, formattedLink)
                "Instagram" -> links[1] = LinkData(site, Icons.Default.Link, formattedLink)
                "X" -> links[2] = LinkData(site, Icons.Default.Link, formattedLink)
                else -> onErrorValidation()
            }
            onClearInputs()
            onShowDialogChange(false)
        } else {
            onShowDialogChange(false)
            onErrorValidation()
        }
    }

    fun linksInit(facebookLink: String?, instagramLink: String?, xLink: String?) {
        links[0] = LinkData("Facebook", Icons.Default.Facebook, facebookLink ?: "")
        links[1] = LinkData("Instagram", Icons.Default.Link, instagramLink ?: "")
        links[2] = LinkData("X", Icons.Default.Link, xLink ?: "")
    }

    private fun prepareTravelPrefToSend(): List<String?> {
        val travelPrefIndexes = mutableListOf<String?>()
        travelPreferencesList.forEach { item ->
            if (item != null) {
                if (item.isSelected)
                    travelPrefIndexes.add(item.id.toString())
            }
        }
        return travelPrefIndexes
    }

    private fun getUserProfileResult() {
        loadingState = LoadingState.Loading
        viewModelScope.launch {
            delay(250)
            try {
                val token = sessionManager.getToken()
                val result = repository.getUserProfileResult(token, editProfile.id!!)
                if (result != null) {
                    if (result.resultCode == 200 && (result.id != null) && (result.name != null)) {
                        editProfile.name = result.name
                        editProfile.id = result.id
                        editProfile.bio = result.bio
                        userTravelPreferences = result.travelPreferences ?: emptyList()
                        getTravelPreferences()
                        facebookLink = result.facebookLink
                        instagramLink = result.instagramLink
                        xLink = result.xLink
                        linksInit(result.facebookLink, result.instagramLink, result.xLink)
                        editProfileState = EditProfileState.Authenticated
                    } else if (result.resultCode == 401) {
                        editProfileState = EditProfileState.Unauthenticated
                        backendResponse.value.message =
                            result.message + "\nYou will be redirected to the login screen."
                        backendResponse.value.buildBackendResponse()
                    } else {
                        editProfileState = EditProfileState.ProfileLoadingError
                        backendResponse.value.message = result.message
                        backendResponse.value.buildBackendResponse()
                    }
                }
            } catch (e: Exception) {
                editProfileState = EditProfileState.ProfileLoadingError
                backendResponse.value.message = e.message
                backendResponse.value.buildBackendResponse()
            }
            delay(250)
            loadingState = LoadingState.Loaded
        }
    }

    private fun getTravelPreferences() {
        viewModelScope.launch {
            try {
                val token = sessionManager.getToken()
                val result = repository.getTravelPreferences(token)
                travelPreferencesResult = result
                if (result?.resultCode == 200 && result.travelPreference != null) {
                    travelPreferencesResult?.travelPreference?.forEach { item ->
                        val travelPreference =
                            TravelPreference(id = item?.id, name = item?.name,
                                isSelected = userTravelPreferences?.any { it?.id == item?.id }
                                    ?: false
                            )
                        travelPreferencesList.add(travelPreference)
                    }
                } else if (result?.resultCode == 401 && result.message != null) {
                    editProfileState = EditProfileState.Unauthenticated
                    backendResponse.value.message =
                        result.message + "\nYou will be redirected to the login screen."
                    backendResponse.value.buildBackendResponse()
                } else {
                    editProfileState = EditProfileState.ProfileLoadingError
                    backendResponse.value.message = result?.message
                    backendResponse.value.buildBackendResponse()
                }
            } catch (e: Exception) {
                editProfileState = EditProfileState.ProfileLoadingError
                backendResponse.value.message = e.message
                backendResponse.value.buildBackendResponse()
            }
        }
    }

    fun updatePassword() {
        viewModelScope.launch {
            try {
                val request =
                    UpdatePasswordRequest(
                        currentPassword!!,
                        newPassword!!,
                        repeatedNewPassword!!
                    )
                val token = sessionManager.getToken()
                val result = repository.updatePassword(token, request)
                updatePasswordResult = result
                if ((updatePasswordResult?.resultCode == 200) && (updatePasswordResult?.message != null)) {
                    currentPassword = ""
                    newPassword = ""
                    repeatedNewPassword = ""
                    updatePasswordState = UpdatePasswordState.Updated
                } else if (updatePasswordResult?.resultCode == 401 && updatePasswordResult?.message != null) {
                    updatePasswordState = UpdatePasswordState.Unauthenticated
                    backendResponse.value.message =
                        result?.message + "\nYou will be redirected to the login screen."
                    backendResponse.value.buildBackendResponse()
                } else if (updatePasswordResult?.resultCode == 422 && updatePasswordResult?.message != null) {
                    updatePasswordState = UpdatePasswordState.Error
                    backendResponse.value.message = result?.message
                    result?.errors?.password?.forEach { item ->
                        backendResponse.value.addError(item)
                    }
                    result?.errors?.oldPassword?.forEach { item ->
                        backendResponse.value.addError(item)
                    }
                    backendResponse.value.buildBackendResponse()

                } else {
                    updatePasswordState = UpdatePasswordState.Error
                    backendResponse.value.message = result?.message
                    backendResponse.value.buildBackendResponse()
                }
            } catch (e: Exception) {
                updatePasswordState = UpdatePasswordState.Error
                backendResponse.value.message = e.message
                backendResponse.value.buildBackendResponse()
            }
        }
    }

    fun editUserProfile() {
        loadingState = LoadingState.Loading
        viewModelScope.launch {
            delay(250)
            try {
                val token = sessionManager.getToken()
                val travelPreferences = prepareTravelPrefToSend()
                val request = EditUserProfileRequest(
                    avatar = "",
                    bio = editProfile.bio,
                    email = editProfile.email,
                    name = editProfile.name,
                    facebookLink = links[0].link,
                    instagramLink = links[1].link,
                    xLink = links[2].link,
                    travelPreferences = travelPreferences
                )
                val result = repository.editUserProfile(token, editProfile.id!!, request)
                if (result != null) {
                    editProfileResult = result
                    if (result.resultCode == 200 && (result.id != null) && (result.name != null)) {
                        editProfile.name = result.name
                        editProfile.bio = result.bio
                        editProfile.id = result.id
                        userTravelPreferences =
                            (result.travelPreferences ?: emptyList()) as List<TravelPreference?>?
                        linksInit(result.facebookLink, result.instagramLink, result.xLink)
                        editProfileState = EditProfileState.Updated
                    } else if (result.resultCode == 401 && result.message != null) {
                        editProfileState = EditProfileState.Unauthenticated
                        backendResponse.value.message =
                            result.message + "\nYou will be redirected to the login screen."
                        backendResponse.value.buildBackendResponse()
                    } else if (result.resultCode == 422 && result.message != null) {
                        editProfileState = EditProfileState.ValidationError
                        backendResponse.value.message = result.message
                        result.errors?.name?.forEach { item ->
                            backendResponse.value.addError(item)
                        }
                        result.errors?.email?.forEach { item ->
                            backendResponse.value.addError(item)
                        }
                        result.errors?.avatar?.forEach { item ->
                            backendResponse.value.addError(item)
                        }
                        backendResponse.value.buildBackendResponse()
                    } else {
                        editProfileState = EditProfileState.Error
                        backendResponse.value.message = result.message
                        backendResponse.value.buildBackendResponse()
                    }
                }
            } catch (e: Exception) {
                editProfileState = EditProfileState.Error
                backendResponse.value.message = e.message
                backendResponse.value.buildBackendResponse()
            }
            delay(250)
            loadingState = LoadingState.Loaded
        }
    }

    fun updatedProfileProcess(navController: NavController) {
        navController.popBackStack()
        navController.navigate("${Screen.ProfileScreen.destination}/${sessionManager.getToken()}/${editProfile.email}/${editProfile.id}")
    }

    fun homeReturnProcess(navController: NavController) {
        navController.popBackStack()
        navController.navigate(Screen.MainPageScreen.destination)
    }

    fun logoutProcess(navController: NavController) {
        sessionManager.clearToken()
        navController.popBackStack()
        navController.navigate(Screen.LoginScreen.destination)
    }

    fun validationErrorProcess() {
        isBackendResponseVisible = false
        clearBackendResponse()
        editProfileState = EditProfileState.NotUpdated
    }

    fun handleEditProfileState(navController: NavController) {
        when (editProfileState) {
            EditProfileState.Error -> {
                isBackendResponseVisible = true
            }

            EditProfileState.ValidationError -> {
                isBackendResponseVisible = true
            }

            EditProfileState.Unauthenticated -> {
                isBackendResponseVisible = true
            }

            EditProfileState.Authenticated -> {}

            EditProfileState.LoggedOut -> {
                isBackendResponseVisible = true
            }

            EditProfileState.NotUpdated -> {}

            EditProfileState.Updated -> {
                isBackendResponseVisible = true
            }

            EditProfileState.ProfileLoadingError -> {
                isBackendResponseVisible = true
            }
        }
    }

    fun handleLoadingState() {
        isProgressIndicatorVisible =
            loadingState == LoadingState.Loading
    }
}