package com.example.triplog.profile.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.authorization.login.components.InformationDialog
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
fun EditProfileScreen(navController: NavController) {
    val viewModel: EditProfileViewModel =
        viewModel(factory = EditProfileViewModel.provideFactory())

    LaunchedEffect(key1 = Unit) {
        viewModel.initParams()
    }

    LaunchedEffect(key1 = viewModel.loadingState) {
        viewModel.handleLoadingState()
    }

    LaunchedEffect(key1 = viewModel.editProfileState) {
        viewModel.handleEditProfileState(navController)
    }

    val alpha = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true) {
        alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 400))
    }

    if (viewModel.isProgressIndicatorVisible) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            LinearIndicator()
        }
    } else {
        if (viewModel.editProfileState != EditProfileState.Unauthenticated && viewModel.editProfileState != EditProfileState.ProfileLoadingError && viewModel.editProfileState != EditProfileState.Error) {
            Scaffold(
                topBar = {
                    when (viewModel.section) {
                        EditUserProfileSection.Main -> {
                            TopApplicationBar(stringResource(R.string.editProfile)) { navController.popBackStack() }
                        }

                        EditUserProfileSection.EditTravelPreferences -> {
                            EditProfileTopBar(
                                stringResource(R.string.editPreferences),
                                backToEditProfile = {
                                    viewModel.tempTravelPreferencesList.clear()
                                    viewModel.section = EditUserProfileSection.Main
                                })
                        }

                        EditUserProfileSection.EditBasicInformation -> {
                            EditProfileTopBar(
                                stringResource(R.string.editBasicInformation),
                                backToEditProfile = {
                                    viewModel.bioTemp = ""
                                    viewModel.usernameTemp = ""
                                    viewModel.emailTemp = ""
                                    viewModel.section = EditUserProfileSection.Main
                                }
                            )
                        }

                        EditUserProfileSection.EditBiography -> {
                            EditProfileTopBar(
                                stringResource(R.string.editBiography),
                                backToEditProfile = {
                                    viewModel.bioTemp = ""
                                    viewModel.section = EditUserProfileSection.EditBasicInformation
                                }
                            )
                        }

                        EditUserProfileSection.UpdatePassword -> {
                            EditProfileTopBar(
                                stringResource(R.string.updatePassword),
                                backToEditProfile = {
                                    viewModel.currentPassword = ""
                                    viewModel.newPassword = ""
                                    viewModel.repeatedNewPassword = ""
                                    viewModel.section = EditUserProfileSection.Main
                                }
                            )
                        }
                    }
                },
                bottomBar = {
                    when (viewModel.section) {
                        EditUserProfileSection.Main -> {
                            EditProfileBottomBar(R.string.saveChanges) {
                                viewModel.isSaveChangesDialogVisible = true
                            }
                        }

                        EditUserProfileSection.EditTravelPreferences -> {
                            EditProfileBottomBar(R.string.saveTravelPreferences) {
                                var counter = 0
                                viewModel.tempTravelPreferencesList.forEach { item ->
                                    if (item?.isSelected == true)
                                        counter++
                                }
                                if (counter > 9) {
                                    viewModel.errorMessage =
                                        ErrorData(true, ErrorType.TravelPreferences, "")
                                    counter = 0
                                } else {
                                    viewModel.travelPreferencesList =
                                        viewModel.tempTravelPreferencesList.toMutableList()
                                    viewModel.tempTravelPreferencesList.clear()
                                    viewModel.section = EditUserProfileSection.Main
                                    counter = 0
                                }
                            }
                        }

                        EditUserProfileSection.EditBasicInformation -> {}

                        EditUserProfileSection.EditBiography -> {
                            EditProfileBottomBar(R.string.saveBiography) {
                                viewModel.editProfile.bio = viewModel.bioTemp
                                viewModel.bioTemp = ""
                                viewModel.section = EditUserProfileSection.EditBasicInformation
                            }
                        }

                        EditUserProfileSection.UpdatePassword -> {
                            EditProfileBottomBar(R.string.updatePassword) {
                                viewModel.updatePassword()
                            }
                        }
                    }
                }
            ) { innerpadding ->
                when (viewModel.section) {
                    EditUserProfileSection.Main -> {
                        EditProfileSection(innerpadding, viewModel, navController)
                    }

                    EditUserProfileSection.EditTravelPreferences -> {
                        EditTravelPreferencesSection(innerpadding, viewModel)
                    }

                    EditUserProfileSection.EditBasicInformation -> {
                        EditBasicInformationSection(innerpadding, viewModel)
                    }

                    EditUserProfileSection.EditBiography -> {
                        EditBiographySection(innerpadding, viewModel)
                    }

                    EditUserProfileSection.UpdatePassword -> {
                        UpdatePasswordSection(innerpadding, viewModel)
                    }
                }
            }
        }
    }

    if (viewModel.editProfileState == EditProfileState.Updated) {
        InformationDialog(
            R.string.editProfileInformation,
            text = {
                Text(
                    text = stringResource(R.string.editProfileSuccess),
                    fontSize = 14.sp, color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            },
            containerColor = { MaterialTheme.colorScheme.tertiaryContainer },
            onDismiss = { viewModel.updatedProfileProcess(navController) },
            onConfirmClick = { viewModel.updatedProfileProcess(navController) }
        )
    }

    if (viewModel.editProfileState == EditProfileState.ValidationError) {
        InformationDialog(
            R.string.editProfileInformation,
            text = {
                Text(
                    text = viewModel.backendResponse.value.errors,
                    fontSize = 14.sp, color = MaterialTheme.colorScheme.onErrorContainer,
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer
                )
            },
            containerColor = { MaterialTheme.colorScheme.errorContainer },
            onDismiss = { viewModel.validationErrorProcess() },
            onConfirmClick = { viewModel.validationErrorProcess() }
        )
    }

    if (viewModel.editProfileState == EditProfileState.Error || viewModel.editProfileState == EditProfileState.ProfileLoadingError)
        if (viewModel.isBackendResponseVisible) {
            InformationDialog(
                R.string.operationResult,
                text = {
                    Text(
                        text = viewModel.backendResponse.value.errors,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                },
                containerColor = { MaterialTheme.colorScheme.errorContainer },
                onDismiss = {
                    viewModel.homeReturnProcess(navController)
                },
                onConfirmClick = {
                    viewModel.homeReturnProcess(navController)
                }
            )
        }

    if (viewModel.editProfileState == EditProfileState.Unauthenticated || viewModel.editProfileState == EditProfileState.LoggedOut) {
        if (viewModel.isBackendResponseVisible) {
            InformationDialog(
                R.string.operationResult,
                text = {
                    Text(
                        text = viewModel.backendResponse.value.errors,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                },
                containerColor = { MaterialTheme.colorScheme.errorContainer },
                onDismiss = { viewModel.logoutProcess(navController) },
                onConfirmClick = { viewModel.logoutProcess(navController) }
            )
        }
    }
}