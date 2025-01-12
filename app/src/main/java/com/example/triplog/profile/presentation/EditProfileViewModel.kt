package com.example.triplog.profile.presentation

import android.annotation.SuppressLint
import android.net.Uri
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
import com.example.triplog.main.BackendResponse
import com.example.triplog.main.ErrorState
import com.example.triplog.main.ErrorType
import com.example.triplog.main.LoadingState
import com.example.triplog.main.ResponseHandler
import com.example.triplog.main.SessionManager
import com.example.triplog.main.TripLogApplication
import com.example.triplog.main.navigation.Screen
import com.example.triplog.network.InterfaceRepository
import com.example.triplog.profile.data.LinkData
import com.example.triplog.profile.data.profile.EditUserProfileResult
import com.example.triplog.profile.data.profile.TravelPreferencesResult
import com.example.triplog.profile.data.profile.UserProfileResult.TravelPreference
import com.example.triplog.profile.data.updatePassword.UpdatePasswordRequest
import com.example.triplog.profile.data.updatePassword.UpdatePasswordResult
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

sealed class EditProfileState {
    data object NotUpdated : EditProfileState()
    data object Updated : EditProfileState()
    data object ProfileLoadingError : EditProfileState()
    data object Unauthenticated : EditProfileState()
    data object Authenticated : EditProfileState()
    data object Error : EditProfileState()
}

sealed class EditUserProfileSection {
    data object Main : EditUserProfileSection()
    data object EditTravelPreferences : EditUserProfileSection()
    data object UpdatePassword : EditUserProfileSection()
    data object EditBasicInformation : EditUserProfileSection()
}

data class EditProfileData(
    var bio: String? = "",
    var email: String? = "",
    var name: String? = "",
    var id: Int? = null,
    var facebookLink: String? = "",
    var instagramLink: String? = "",
    var xLink: String? = "",
    var avatarUrl: String? = null
)

@SuppressLint("MutableCollectionMutableState")
class EditProfileViewModel(
    private val repository: InterfaceRepository,
    val sessionManager: SessionManager,
    val responseHandler: ResponseHandler
) :
    ViewModel() {
    var editProfile by mutableStateOf(EditProfileData())
    var editProfileState by mutableStateOf<EditProfileState>(EditProfileState.NotUpdated)
    var loadingState: LoadingState by mutableStateOf(LoadingState.NotLoaded)
    var section by mutableStateOf<EditUserProfileSection>(EditUserProfileSection.Main)

    /**
     * Flags which are used by UI to display individual parts of the UI such as dialogues, loading effect.
     */
    var isProgressIndicatorVisible by mutableStateOf(false)
    var isBackendResponseVisible by mutableStateOf(false)
    var isUsernameDialogVisible by mutableStateOf(false)
    var isEmailDialogVisible by mutableStateOf(false)
    var isSaveChangesDialogVisible by mutableStateOf(false)
    var isDeleteLinkDialogVisible by mutableStateOf(false)
    var isAddLinkDialogVisible by mutableStateOf(false)

    init {
        getUserProfileResult()
    }

    private var editProfileResult by mutableStateOf<EditUserProfileResult?>(null)
    private var updatePasswordResult by mutableStateOf<UpdatePasswordResult?>(null)


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
    var avatar by mutableStateOf<Uri?>(null)
    var avatarPart by mutableStateOf<MultipartBody.Part?>(null)

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

    var currentPassword by mutableStateOf<String?>("")
    var newPassword by mutableStateOf<String?>("")
    var repeatedNewPassword by mutableStateOf<String?>("")

    var errorState by mutableStateOf(ErrorState())
        private set

    private fun setError(type: ErrorType, description: String) {
        errorState = ErrorState(
            isError = true,
            type = type,
            description = description
        )
    }

    fun clearError() {
        errorState = ErrorState()
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
                EditProfileViewModel(
                    repository = repository,
                    sessionManager = sessionManager,
                    responseHandler = responseHandler
                )
            }
        }
    }

    /**
     * Function addNewLink() checks if site and link input are not empty,
     * formats link to lowercase, adds https:// to link and sets the link in correct position in link list.
     */
    fun addNewLink(
        site: String,
        link: String,
        onShowDialogChange: (Boolean) -> Unit,
        onClearInputs: () -> Unit
    ) {
        if (site.isNotEmpty() && link.isNotEmpty()) {
            var formattedLink = link.lowercase()
            if (!formattedLink.startsWith("https://")) {
                formattedLink = "https://$formattedLink"
            }

            val siteKeywordMap = mapOf(
                "Facebook" to "facebook",
                "Instagram" to "instagram",
                "X" to "x"
            )

            val keyword = siteKeywordMap[site]
            if (keyword != null && !formattedLink.contains(keyword)) {
                setError(ErrorType.LINKS, "The link does not match the selected site")
                return
            }

            when (site) {
                "Facebook" -> links[0] = LinkData(site, Icons.Default.Facebook, formattedLink)
                "Instagram" -> links[1] = LinkData(site, Icons.Default.Link, formattedLink)
                "X" -> links[2] = LinkData(site, Icons.Default.Link, formattedLink)
                else -> {
                    setError(ErrorType.LINKS, "Invalid site")
                    return
                }
            }
            onClearInputs()
            onShowDialogChange(false)
        } else {
            setError(ErrorType.VALIDATION, "Site and link cannot be empty")
            onShowDialogChange(false)
        }
    }


    /**
     * linksInit initializes links added by user. Function used by getUserProfileResult() to set links in profile.
     */
    private fun linksInit(facebookLink: String?, instagramLink: String?, xLink: String?) {
        links[0] = LinkData("Facebook", Icons.Default.Facebook, facebookLink ?: "")
        links[1] = LinkData("Instagram", Icons.Default.Link, instagramLink ?: "")
        links[2] = LinkData("X", Icons.Default.Link, xLink ?: "")
    }

    /**
     * prepareTravelPrefToSend() sets the list of travel preference's indexes.
     * List of indexes is send to server. Function used by editUserProfile().
     */
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

    /**
     * Process state functions are responsible for:
     * - setting the message from server in responseHandler,
     * - setting the profile state in viewModel.
     */
    private fun processUnauthenticatedState(backendResponse: BackendResponse) {
        editProfileState = EditProfileState.Unauthenticated
        responseHandler.showMessage(backendResponse)
    }

    private fun processErrorState(backendResponse: BackendResponse) {
        editProfileState = EditProfileState.Error
        responseHandler.showMessage(backendResponse)
    }

    private fun processProfileLoadingErrorState(backendResponse: BackendResponse) {
        editProfileState = EditProfileState.ProfileLoadingError
        responseHandler.showMessage(backendResponse)
    }

    private fun processAuthenticatedState(backendResponse: BackendResponse) {
        editProfileState = EditProfileState.Authenticated
        responseHandler.showMessage(backendResponse)
    }

    private fun processUpdatedState(backendResponse: BackendResponse) {
        editProfileState = EditProfileState.Updated
        responseHandler.showMessage(backendResponse)
    }

    /**
     * Function handleEditProfileState() sets the viewModel's flags based on the viewModel's profileState.
     */
    fun handleEditProfileState() {
        isBackendResponseVisible = when (editProfileState) {
            EditProfileState.Error -> true
            EditProfileState.Authenticated -> false
            EditProfileState.Unauthenticated -> true
            EditProfileState.Updated -> true
            EditProfileState.ProfileLoadingError -> true
            else -> false
        }
    }

    fun handleProcesses(navController: NavController) {
        when (editProfileState) {
            EditProfileState.Error -> {
                clearBackendResponse()
                editProfileState = EditProfileState.NotUpdated
            }

            EditProfileState.Authenticated -> {
                clearBackendResponse()
                editProfileState = EditProfileState.NotUpdated
            }

            EditProfileState.Unauthenticated -> {
                clearBackendResponse()
                logoutProcess(navController = navController)
            }

            EditProfileState.ProfileLoadingError -> {
                clearBackendResponse()
                profileReturnProcess(navController = navController)
            }

            EditProfileState.Updated -> {
                clearBackendResponse()
                profileReturnProcess(navController = navController)
            }

            EditProfileState.NotUpdated -> {}
        }
    }

    private fun clearBackendResponse() {
        isBackendResponseVisible = false
        responseHandler.clearMessage()
    }

    private fun logoutProcess(navController: NavController) {
        sessionManager.clearToken()
        navController.navigate(Screen.LoginScreen.destination)
    }

    private fun profileReturnProcess(navController: NavController) {
        navController.popBackStack()
    }

    fun handleLoadingState() {
        isProgressIndicatorVisible = loadingState == LoadingState.Loading
    }

    private fun getUserProfileResult() {
        loadingState = LoadingState.Loading
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val result = repository.getUserProfileResult(token, sessionManager.getUserId()!!)
                if (result != null) {
                    if (result.resultCode == 200 && (result.id != null) && (result.name != null)) {
                        editProfile.name = result.name
                        editProfile.id = result.id
                        editProfile.avatarUrl = result.avatar
                        editProfile.email = sessionManager.getUserEmail()
                        editProfile.bio = result.bio
                        userTravelPreferences = result.travelPreferences ?: emptyList()
                        getTravelPreferences()
                        facebookLink = result.facebookLink
                        instagramLink = result.instagramLink
                        xLink = result.xLink
                        linksInit(result.facebookLink, result.instagramLink, result.xLink)
                        val backendResponse = BackendResponse()
                        processAuthenticatedState(backendResponse)
                    } else if (result.resultCode == 401) {
                        val backendResponse = BackendResponse(message = result.message)
                        processUnauthenticatedState(backendResponse)
                    } else {
                        val backendResponse = BackendResponse(message = result.message)
                        processProfileLoadingErrorState(backendResponse)
                    }
                }
            } catch (e: Exception) {
                val backendResponse = BackendResponse(message = e.message)
                processProfileLoadingErrorState(backendResponse)
            }
            loadingState = LoadingState.Loaded
        }
    }

    private fun getTravelPreferences() {
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
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

    fun updatePassword() {
        loadingState = LoadingState.Loading
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val request =
                    UpdatePasswordRequest(
                        currentPassword!!,
                        newPassword!!,
                        repeatedNewPassword!!
                    )
                val result = repository.updatePassword(token, request)
                updatePasswordResult = result
                if ((updatePasswordResult?.resultCode == 200) && (updatePasswordResult?.message != null)) {
                    currentPassword = ""
                    newPassword = ""
                    repeatedNewPassword = ""
                    val backendResponse = BackendResponse(message = result?.message)
                    processUpdatedState(backendResponse)
                } else if (updatePasswordResult?.resultCode == 401 && updatePasswordResult?.message != null) {
                    val backendResponse = BackendResponse(message = updatePasswordResult?.message)
                    processUnauthenticatedState(backendResponse)
                } else {
                    val backendResponse =
                        BackendResponse(message = updatePasswordResult?.message,
                            errors = listOfNotNull(
                                updatePasswordResult?.errors?.password?.joinToString(separator = ", "),
                                updatePasswordResult?.errors?.oldPassword?.joinToString(separator = ", "),
                            ).flatMap { it.split(", ") })
                    processErrorState(backendResponse)
                }
            } catch (e: Exception) {
                val backendResponse = BackendResponse(message = e.message)
                processErrorState(backendResponse)
            }
            loadingState = LoadingState.Loaded
        }
    }

    fun editUserProfile() {
        loadingState = LoadingState.Loading
        val token = sessionManager.getToken()
        viewModelScope.launch {
            try {
                val email = editProfile.email!!.toRequestBody("text/plain".toMediaTypeOrNull())
                val name = RequestBody.create("text/plain".toMediaTypeOrNull(), editProfile.name!!)
                val bio = RequestBody.create("text/plain".toMediaTypeOrNull(), editProfile.bio!!)
                val facebookLink =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), links[0].link)
                val instagramLink =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), links[1].link)
                val xLink = RequestBody.create("text/plain".toMediaTypeOrNull(), links[2].link)

                val travelPreferencesMap = prepareTravelPrefToSend().mapIndexed { index, value ->
                    "travel_preferences[$index]" to RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        value.toString()
                    )
                }.toMap()

                val result = repository.editUserProfile(
                    token = token,
                    id = sessionManager.getUserId()!!,
                    email = email,
                    name = name,
                    avatar = avatarPart?.takeIf { it.body.contentLength() > 0 },
                    facebookLink = facebookLink,
                    instagramLink = instagramLink,
                    xLink = xLink,
                    bio = bio,
                    travelPreferences = travelPreferencesMap
                )


                if (result != null) {
                    editProfileResult = result
                    if (result.resultCode == 200 && (result.id != null) && (result.name != null)) {
                        editProfile.name = result.name
                        editProfile.email = sessionManager.getUserEmail()
                        editProfile.bio = result.bio
                        editProfile.id = result.id
                        userTravelPreferences =
                            (result.travelPreferences ?: emptyList()) as List<TravelPreference?>?
                        linksInit(result.facebookLink, result.instagramLink, result.xLink)
                        processUpdatedState(BackendResponse(message = "Profile updated successfully."))
                    } else if (result.resultCode == 401 && result.message != null) {
                        processUnauthenticatedState(BackendResponse(message = result.message))
                    } else {
                        processErrorState(
                            BackendResponse(
                                message = result.message,
                                errors = listOfNotNull(
                                    result.errors?.name?.joinToString(separator = ", "),
                                    result.errors?.email?.joinToString(separator = ", "),
                                    result.errors?.avatar?.joinToString(separator = ", ")
                                ).flatMap { it.split(", ") }
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                processErrorState(BackendResponse(message = e.message))
            }
            loadingState = LoadingState.Loaded
        }
    }
}