package com.example.triplog.profile.presentation

import android.util.Log
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
import com.example.triplog.main.TripLogApplication
import com.example.triplog.network.InterfaceRepository
import com.example.triplog.profile.data.LinkData
import com.example.triplog.profile.data.ErrorData
import com.example.triplog.profile.data.profile.EditUserProfileRequest
import com.example.triplog.profile.data.profile.TravelPreferencesResult
import com.example.triplog.profile.data.profile.UserProfileResult.TravelPreference
import com.example.triplog.profile.data.updatePassword.UpdatePasswordRequest
import com.example.triplog.profile.data.updatePassword.UpdatePasswordResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class EditProfileSection {
    Main, EditTravelPreferences, EditBiography, UpdatePassword, EditBasicInformation
}

enum class UpdatePasswordState {
    NotUpdated, Updated, Error, ValidationError
}

enum class EditProfileState {
    NotUpdated, Updated, Error, ValidationError
}

class EditProfileViewModel(private val repository: InterfaceRepository, var token: String?) :
    ViewModel() {

    fun initParams(iId: Int?, iEmail: String?) {
        id = iId
        email = iEmail
        getUserProfileResult()
    }

    var isProgressIndicatorVisible by mutableStateOf(false)

    var updatePasswordResult by mutableStateOf<UpdatePasswordResult?>(null)
    var updatePasswordState by mutableStateOf(UpdatePasswordState.NotUpdated)

    var loadingState: LoadingState by mutableStateOf(LoadingState.NotLoaded)
    var section by mutableStateOf(EditProfileSection.Main)

    var username by mutableStateOf<String>("")
    var id by mutableStateOf<Int?>(null)
    var bio by mutableStateOf<String>("")
    var email by mutableStateOf<String?>("")


    var links by mutableStateOf(
        mutableListOf(
            LinkData("Facebook", Icons.Default.Facebook, ""),
            LinkData("Instagram", Icons.Default.Link, ""),
            LinkData("X", Icons.Default.Link, "")
        )
    )
    var facebookLink by mutableStateOf<String?>("")
    var instagramLink by mutableStateOf<String?>("")
    var xLink by mutableStateOf<String?>("")

    // Travel preferences result from backend server
    var travelPreferencesResult by mutableStateOf<TravelPreferencesResult?>(null)

    // User's travel preferences
    var userTravelPreferences: List<TravelPreference?>? = listOf()

    // Travel preferences lists used for operations in app
    var travelPreferencesList by mutableStateOf(mutableListOf<TravelPreference?>())
    var tempTravelPreferencesList by mutableStateOf(mutableListOf<TravelPreference?>())

    var usernameTemp by mutableStateOf("")
    var emailTemp by mutableStateOf("")
    var bioTemp by mutableStateOf("")

    var currentPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var repeatedNewPassword by mutableStateOf("")

    var isUsernameDialogVisible by mutableStateOf(false)
    var isEmailDialogVisible by mutableStateOf(false)
    var isSaveChangesDialogVisible by mutableStateOf(false)
    var isDeleteLinkDialogVisible by mutableStateOf(false)
    var isAddLinkDialogVisible by mutableStateOf(false)

    var errorMessage by mutableStateOf(ErrorData(false, null, ""))

    companion object {
        fun provideFactory(
            token: String?,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                EditProfileViewModel(repository = repository, token = token)
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
            val result = repository.getUserProfileResult(token, id!!)
            if (result != null) {
                if (result.resultCode == 200 && (result.id != null) && (result.name != null) && (result.bio != null)) {
                    username = result.name
                    id = result.id
                    bio = result.bio
                    userTravelPreferences = result.travelPreferences ?: emptyList()
                    getTravelPreferences()
                    facebookLink = result.facebookLink
                    instagramLink = result.instagramLink
                    xLink = result.xLink

                    linksInit(result.facebookLink, result.instagramLink, result.xLink)
                } else if (result.resultCode == 401 && result.message != null) {

                } else if (result.resultCode == 404 && result.message != null) {

                } else {

                }
            }
            delay(200)
            loadingState = LoadingState.Loaded
        }
    }

    fun updatePassword() {
        viewModelScope.launch {
            try {
                val request =
                    UpdatePasswordRequest(currentPassword, newPassword, repeatedNewPassword)
                val result = repository.updatePassword(token, request)
                updatePasswordResult = result
                if ((updatePasswordResult?.resultCode == 200) && (updatePasswordResult?.message != null)) {
                    currentPassword = ""
                    newPassword = ""
                    repeatedNewPassword = ""
                    updatePasswordState = UpdatePasswordState.Updated
                } else if (updatePasswordResult?.resultCode == 400 && updatePasswordResult?.message != null) {
                    updatePasswordState = UpdatePasswordState.Error
                } else if (updatePasswordResult?.resultCode == 422 && updatePasswordResult?.message != null) {
                    updatePasswordState = UpdatePasswordState.ValidationError
                } else {
                    updatePasswordState = UpdatePasswordState.NotUpdated
                }
            } catch (e: Exception) {
                updatePasswordState = UpdatePasswordState.Error
            }
        }
    }

    fun getTravelPreferences() {
        viewModelScope.launch {
            val result = repository.getTravelPreferences(token)
            travelPreferencesResult = result
            when (result?.resultCode) {
                200 -> {
                    travelPreferencesResult?.travelPreference?.forEach { item ->
                        val travelPreference = TravelPreference(id = item?.id, name = item?.name,
                            isSelected = userTravelPreferences?.any { it?.id == item?.id } ?: false
                        )
                        travelPreferencesList.add(travelPreference)
                    }
                }

                401 -> {

                }

                else -> {

                }
            }
        }
    }

    fun editUserProfile() {
        loadingState = LoadingState.Loading
        viewModelScope.launch {
            val travelPreferences = prepareTravelPrefToSend()
            val request = EditUserProfileRequest(
                avatar = "",
                bio = bio,
                email = email,
                name = username,
                facebookLink = links[0].link,
                instagramLink = links[1].link,
                xLink = links[2].link,
                travelPreferences = travelPreferences
            )
            val result = repository.editUserProfile(token, id!!, request)
            if (result != null) {
                if (result.resultCode == 200 && (result.id != null) && (result.name != null) && (result.bio != null)) {
                    username = result.name
                    bio = result.bio
                    userTravelPreferences =
                        (result.travelPreferences ?: emptyList()) as List<TravelPreference?>?
                    linksInit(result.facebookLink, result.instagramLink, result.xLink)
                } else if (result.resultCode == 401 && result.message != null) {

                } else if (result.resultCode == 422 && result.message != null) {

                } else {

                }
            }
            delay(200)
            loadingState = LoadingState.Loaded
        }
    }

    fun handleLoadingState() {
        isProgressIndicatorVisible =
            loadingState == LoadingState.Loading
    }
}