package com.example.triplog.profile.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.main.navigation.ButtonBottomBar
import com.example.triplog.main.navigation.TopApplicationBar
import com.example.triplog.profile.data.ErrorData
import com.example.triplog.profile.data.ErrorType
import com.example.triplog.profile.presentation.sections.EditBasicInformationSection
import com.example.triplog.profile.presentation.sections.EditProfileSection
import com.example.triplog.profile.presentation.sections.EditTravelPreferencesSection
import com.example.triplog.profile.presentation.sections.UpdatePasswordSection

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun EditProfileScreen(navController: NavController) {
    val viewModel: EditProfileViewModel =
        viewModel(factory = EditProfileViewModel.provideFactory())

    LaunchedEffect(key1 = Unit) {
        viewModel.initParams()
    }

    LaunchedEffect(viewModel.loadingState, viewModel.editProfileState) {
        viewModel.handleLoadingState()
        viewModel.handleEditProfileState()
    }

    Scaffold(
        topBar = { EditProfileTopBar(navController, viewModel) },
        bottomBar = { EditProfileBottomBar(viewModel) },
    ) { innerpadding ->
        if (viewModel.isProgressIndicatorVisible) {
            FullScreenLoadingIndicator()
        } else {
            EditProfileContent(innerpadding, viewModel, navController)
        }
    }

    if (viewModel.isBackendResponseVisible) {
        AlertDialog(
            title = { Text(stringResource(R.string.operationResult)) },
            text = {
                Column {
                    viewModel.responseHandler.message.value.let {
                        Text(
                            text = it ?: "Operation without message from server",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    viewModel.responseHandler.errors.value?.let { errors ->
                        errors.forEach { error ->
                            Text(
                                text = "- $error",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            },
            icon = {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            onDismissRequest = {
                viewModel.handleProcesses(navController)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.handleProcesses(navController)
                    },
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.ok),
                    )
                }
            }
        )
    }
}

@Composable
fun EditProfileContent(
    innerpadding: PaddingValues,
    viewModel: EditProfileViewModel,
    navController: NavController
) {
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

        EditUserProfileSection.UpdatePassword -> {
            UpdatePasswordSection(innerpadding, viewModel)
        }
    }
}

@Composable
fun EditProfileBottomBar(viewModel: EditProfileViewModel) {
    when (viewModel.section) {
        EditUserProfileSection.Main -> {
            ButtonBottomBar(R.string.saveChanges) {
                viewModel.isSaveChangesDialogVisible = true
            }
        }

        EditUserProfileSection.EditTravelPreferences -> {
            ButtonBottomBar(R.string.saveTravelPreferences) {
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

        EditUserProfileSection.EditBasicInformation -> {

        }

        EditUserProfileSection.UpdatePassword -> {
            ButtonBottomBar(R.string.updatePassword) {
                viewModel.updatePassword()
            }
        }
    }
}

@Composable
fun EditProfileTopBar(navController: NavController, viewModel: EditProfileViewModel) {
    when (viewModel.section) {
        EditUserProfileSection.Main -> {
            TopApplicationBar(title = { Text(stringResource(R.string.editProfile)) }) { navController.popBackStack() }
        }

        EditUserProfileSection.EditTravelPreferences -> {
            TopApplicationBar(
                title = { Text(stringResource(R.string.editPreferences)) }
            ) {
                viewModel.tempTravelPreferencesList.clear()
                viewModel.section = EditUserProfileSection.Main
            }
        }

        EditUserProfileSection.EditBasicInformation -> {
            TopApplicationBar(
                title = { Text(stringResource(R.string.editBasicInformation)) },
            ) {
                viewModel.editProfile.bio = viewModel.bioTemp
                viewModel.bioTemp = ""
                viewModel.usernameTemp = ""
                viewModel.emailTemp = ""
                viewModel.section = EditUserProfileSection.Main
            }
        }

        EditUserProfileSection.UpdatePassword -> {
            TopApplicationBar(
                title = { Text(stringResource(R.string.updatePassword)) }
            ) {
                viewModel.currentPassword = ""
                viewModel.newPassword = ""
                viewModel.repeatedNewPassword = ""
                viewModel.section = EditUserProfileSection.Main
            }
        }
    }
}
