package com.example.triplog.profile.presentation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.main.navigation.EditProfileBottomBar
import com.example.triplog.main.navigation.EditProfileTopBar
import com.example.triplog.main.navigation.TopApplicationBar
import com.example.triplog.profile.data.ErrorData
import com.example.triplog.profile.data.ErrorType
import com.example.triplog.profile.presentation.sections.EditBiographySection
import com.example.triplog.profile.presentation.sections.EditProfileSection
import com.example.triplog.profile.presentation.sections.EditTravelPreferencesSection

@Composable
fun EditProfileScreen(navController: NavController) {
    val viewModel: EditProfileViewModel = viewModel(factory = EditProfileViewModel.Factory)

    when (viewModel.section) {
        EditProfileSection.Main -> {
            Scaffold(
                topBar = {
                    TopApplicationBar(stringResource(R.string.editProfile), navController)
                },
                bottomBar = {
                    EditProfileBottomBar(R.string.saveChanges) { viewModel.isSaveChangesDialogVisible = true }
                }
            ) { innerpadding ->
                EditProfileSection(innerpadding, viewModel, navController)
            }
        }

        EditProfileSection.EditTravelPreferences -> {
            Scaffold(
                topBar = {
                    EditProfileTopBar(
                        stringResource(R.string.editPreferences),
                        backToEditProfile = {
                            viewModel.section = EditProfileSection.Main
                            viewModel.travelPreferencesList.forEach { item ->
                                if (viewModel.selectedTravelPreferences.contains(item.name)) {
                                    item.selected = true
                                } else item.selected = false
                            }
                        })
                },
                bottomBar = {
                    EditProfileBottomBar(R.string.saveTravelPreferences) {
                        var counter = 0
                        viewModel.travelPreferencesList.forEach { item ->
                            if (item.selected)
                                counter++
                        }
                        if (counter < 10) {
                            viewModel.selectedTravelPreferences.clear()
                            viewModel.travelPreferencesList.forEach { item ->
                                if (item.selected) {
                                    viewModel.selectedTravelPreferences.add(item.name)
                                }
                            }
                            viewModel.section = EditProfileSection.Main
                            counter = 0
                        } else {
                            viewModel.errorMessage =
                                ErrorData(true, ErrorType.TravelPreferences, "")
                            counter = 0
                        }
                    }
                }
            ) { innerpadding ->
                EditTravelPreferencesSection(innerpadding, viewModel)
            }
        }

        EditProfileSection.EditBiography -> {
            Scaffold(
                topBar = {
                    EditProfileTopBar(
                        stringResource(R.string.editBiography),
                        backToEditProfile = {
                            viewModel.bioTemp = ""
                            viewModel.section = EditProfileSection.Main
                        }
                    )
                },
                bottomBar = {
                    EditProfileBottomBar(R.string.saveBiography) {
                        if (viewModel.bioTemp.length < 385) {
                            viewModel.bio = viewModel.bioTemp
                            viewModel.bioTemp = ""
                            viewModel.section = EditProfileSection.Main
                        } else {
                            viewModel.errorMessage = ErrorData(true, ErrorType.Biography, "")
                        }
                    }
                }
            ) { innerpadding ->
                EditBiographySection(innerpadding, viewModel)
            }
        }
    }
}