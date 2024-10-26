package com.example.triplog.profile.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.authorization.login.components.LinearIndicator
import com.example.triplog.main.navigation.EditProfileBottomBar
import com.example.triplog.main.navigation.EditProfileTopBar
import com.example.triplog.main.navigation.TopApplicationBar
import com.example.triplog.profile.data.ErrorData
import com.example.triplog.profile.data.ErrorType
import com.example.triplog.profile.presentation.sections.EditBasicInformationSection
import com.example.triplog.profile.presentation.sections.EditBiographySection
import com.example.triplog.profile.presentation.sections.EditProfileSection
import com.example.triplog.profile.presentation.sections.EditTravelPreferencesSection
import com.example.triplog.profile.presentation.sections.UpdatePasswordSection

@Composable
fun EditProfileScreen(token: String?, id: Int?, email: String?, navController: NavController) {
    val viewModel: EditProfileViewModel =
        viewModel(factory = EditProfileViewModel.provideFactory(token))

    LaunchedEffect(key1 = Unit) {
        viewModel.initParams(id, email)
    }

    val alpha = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true) {
        alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 200))
    }
    if (viewModel.isProgressIndicatorVisible) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .alpha(alpha.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            LinearIndicator()
        }
    } else {
        Box(modifier = Modifier.alpha(alpha.value)) {
            when (viewModel.section) {
                EditProfileSection.Main -> {
                    Scaffold(
                        topBar = {
                            TopApplicationBar(stringResource(R.string.editProfile), navController)
                        },
                        bottomBar = {
                            EditProfileBottomBar(R.string.saveChanges) {
                                viewModel.isSaveChangesDialogVisible = true
                            }
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
                                    viewModel.tempTravelPreferencesList.clear()
                                    viewModel.section = EditProfileSection.Main
                                })
                        },
                        bottomBar = {
                            EditProfileBottomBar(R.string.saveTravelPreferences) {
                                var counter = 0
                                viewModel.tempTravelPreferencesList.forEach { item ->
                                    if (item?.isSelected == true)
                                        counter++
                                }
                                if (counter > 9) {
                                    viewModel.errorMessage = ErrorData(true, ErrorType.TravelPreferences, "")
                                    counter = 0
                                } else {
                                    viewModel.travelPreferencesList = viewModel.tempTravelPreferencesList.toMutableList()
                                    viewModel.tempTravelPreferencesList.clear()
                                    viewModel.section = EditProfileSection.Main
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
                                    viewModel.section = EditProfileSection.EditBasicInformation
                                }
                            )
                        },
                        bottomBar = {
                            EditProfileBottomBar(R.string.saveBiography) {
                                if (viewModel.bioTemp.length < 385) {
                                    viewModel.bio = viewModel.bioTemp
                                    viewModel.bioTemp = ""
                                    viewModel.section = EditProfileSection.EditBasicInformation
                                } else {
                                    viewModel.errorMessage =
                                        ErrorData(true, ErrorType.Biography, "")
                                }
                            }
                        }
                    ) { innerpadding ->
                        EditBiographySection(innerpadding, viewModel)
                    }
                }

                EditProfileSection.UpdatePassword -> {
                    Scaffold(
                        topBar = {
                            EditProfileTopBar(
                                stringResource(R.string.updatePassword),
                                backToEditProfile = {
                                    viewModel.currentPassword = ""
                                    viewModel.newPassword = ""
                                    viewModel.repeatedNewPassword = ""
                                    viewModel.section = EditProfileSection.Main
                                }
                            )
                        },
                        bottomBar = {
                            EditProfileBottomBar(R.string.updatePassword) {
                                viewModel.updatePassword()
                            }
                        }
                    ) { innerpadding ->
                        UpdatePasswordSection(innerpadding, viewModel)
                    }
                }

                EditProfileSection.EditBasicInformation -> {
                    Scaffold(
                        topBar = {
                            EditProfileTopBar(
                                stringResource(R.string.editBasicInformation),
                                backToEditProfile = {
                                    viewModel.bioTemp = ""
                                    viewModel.usernameTemp = ""
                                    viewModel.emailTemp = ""
                                    viewModel.section = EditProfileSection.Main
                                }
                            )
                        },
                        bottomBar = {
                            EditProfileBottomBar(R.string.saveChanges) {
                                viewModel.section = EditProfileSection.Main
                            }
                        }
                    ) { innerpadding ->
                        EditBasicInformationSection(innerpadding, viewModel)
                    }
                }
            }
        }
    }
}