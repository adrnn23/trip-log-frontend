package com.example.triplog.profile.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.triplog.main.TripLogApplication
import com.example.triplog.network.InterfaceRepository
import com.example.triplog.profile.data.LinkData
import com.example.triplog.profile.data.ErrorData
import com.example.triplog.profile.data.TravelPreferenceData

enum class EditProfileSection {
    Main, EditTravelPreferences, EditBiography
}

class EditProfileViewModel(private val repository: InterfaceRepository) : ViewModel() {
    var section by mutableStateOf(EditProfileSection.Main)

    var username by mutableStateOf("username123")
    var usernameTemp by mutableStateOf("")

    var email by mutableStateOf("username123@example.com")
    var emailTemp by mutableStateOf("")

    var bio by mutableStateOf("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam enim enim, hendrerit in mauris id, molestie blandit metus. Donec ultricies neque et dolor auctor, in condimentum eros consequat. Maecenas aliquet ornare dui, sit amet finibus velit molestie eu. Phasellus interdum eros quis ultricies semper. Donec nec risus sed dui pharetra rutrum non id sem. Donec congue dictum nunc, ac vulputate mi mattis nec. Nam sed magna sodales augue semper finibus et vitae tortor. Nullam tristique dui risus.")
    var bioTemp by mutableStateOf("")

    var selectedTravelPreferences = mutableListOf<String?>()
    var travelPreferencesList by mutableStateOf(
        mutableListOf(
            TravelPreferenceData("Solo travel", false),
            TravelPreferenceData("Family travel", false),
            TravelPreferenceData("Group travel", false),
            TravelPreferenceData("Last minute travel", false),
            TravelPreferenceData("Train travel", false),
            TravelPreferenceData("Bike travel", false),
            TravelPreferenceData("Car travel", false),
            TravelPreferenceData("Air travel", false),
            TravelPreferenceData("Hiking", false),
            TravelPreferenceData("Leisure travel", false),
            TravelPreferenceData("Cultural travel", false),
            TravelPreferenceData("Sea travel", false),
            TravelPreferenceData("Mountains", false),
            TravelPreferenceData("Lakes", false),
            TravelPreferenceData("Seas", false),
            TravelPreferenceData("National Parks", false),
            TravelPreferenceData("Deserts", false),
            TravelPreferenceData("Cities", false),
            TravelPreferenceData("Islands", false),
            TravelPreferenceData("Natural areas", false),
            TravelPreferenceData("Villages", false)
        )
    )

    var links = mutableListOf<LinkData?>()

    var isUsernameDialogVisible by mutableStateOf(false)
    var isEmailDialogVisible by mutableStateOf(false)
    var isSaveChangesDialogVisible by mutableStateOf(false)
    var isDeleteLinkDialogVisible by mutableStateOf(false)
    var isAddLinkDialogVisible by mutableStateOf(false)

    var errorMessage by mutableStateOf(ErrorData(false, null, ""))

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                EditProfileViewModel(repository = repository)
            }
        }
    }

    fun travelPreferencesListInitialization(
        selectedTravelPreferences: MutableList<String?>,
        travelPreferencesList: MutableList<TravelPreferenceData>
    ): MutableList<TravelPreferenceData> {
        travelPreferencesList.forEach { item ->
            if (selectedTravelPreferences.contains(item.name)) {
                item.selected = true
            }
        }
        return travelPreferencesList
    }

    fun addNewLink(
        site: String,
        link: String,
        sites: List<String>,
        onShowDialogChange: (Boolean) -> Unit,
        onClearInputs: () -> Unit,
        onErrorValidation: () -> Unit,
    ) {
        if (site.isNotEmpty() && link.isNotEmpty()) {
            var formattedLink = link.lowercase()
            when (site) {
                sites[0] -> {
                    if (formattedLink.contains("facebook.com")) {
                        if (!formattedLink.startsWith("https://"))
                            formattedLink = "https://$formattedLink"

                        links.add(LinkData(site, Icons.Default.Facebook, formattedLink))
                        errorMessage = ErrorData(false, null, "")
                        onClearInputs()
                        onShowDialogChange(false)
                    } else {
                        onShowDialogChange(false)
                        onErrorValidation()
                    }
                }

                sites[1] -> {
                    if (formattedLink.contains("instagram.com")) {
                        if (!formattedLink.startsWith("https://"))
                            formattedLink = "https://$formattedLink"

                        links.add(LinkData(site, Icons.Default.Link, formattedLink))
                        errorMessage = ErrorData(false, null, "")
                        onClearInputs()
                        onShowDialogChange(false)
                    } else {
                        onShowDialogChange(false)
                        onErrorValidation()
                    }
                }

                else -> {
                    if (formattedLink.contains("x.com")) {
                        if (!formattedLink.startsWith("https://"))
                            formattedLink = "https://$formattedLink"

                        links.add(LinkData(site, Icons.Default.Link, formattedLink))
                        errorMessage = ErrorData(false, null, "")
                        onClearInputs()
                        onShowDialogChange(false)
                    } else {
                        onShowDialogChange(false)
                        onErrorValidation()
                    }
                }
            }
        }
        else{
            onShowDialogChange(false)
            onErrorValidation()
        }
    }

    fun editUserProfile() {}
}