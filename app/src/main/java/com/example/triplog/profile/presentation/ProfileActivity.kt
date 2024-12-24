package com.example.triplog.profile.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.main.navigation.ApplicationBottomBar
import com.example.triplog.main.navigation.MainTopBar
import com.example.triplog.main.navigation.Screen
import com.example.triplog.main.navigation.TopApplicationBar
import com.example.triplog.profile.presentation.sections.FriendsListSection
import com.example.triplog.profile.presentation.sections.FriendsRequestsSection
import com.example.triplog.profile.presentation.sections.ProfileSection

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ProfileScreen(
    navController: NavController,
    id: Int?
) {
    val viewModel: ProfileViewModel =
        viewModel(factory = ProfileViewModel.provideFactory())

    LaunchedEffect(key1 = Unit) {
        viewModel.initParams(id)
    }

    LaunchedEffect(viewModel.loadingState, viewModel.profileState) {
        viewModel.handleLoadingState()
        viewModel.handleProfileState()
    }

    Scaffold(
        topBar = { ProfileTopBar(viewModel) },
        bottomBar = { ProfileBottomBar(navController, viewModel) },
    ) { innerpadding ->
        if (viewModel.isProgressIndicatorVisible) {
            FullScreenLoadingIndicator()
        } else {
            ProfileContent(innerpadding, viewModel, navController)
        }
    }

    if (viewModel.isLogoutDialogVisible) {
        AlertDialog(
            title = { Text(stringResource(R.string.doYouWantToLogout)) },
            icon = {
                Icon(
                    Icons.Filled.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            },
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            onDismissRequest = {
                viewModel.isLogoutDialogVisible = false
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.isLogoutDialogVisible = false
                    },
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.isLogoutDialogVisible = false
                        viewModel.logout()
                    },
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.logout),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
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
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    viewModel.responseHandler.errors.value?.let { errors ->
                        errors.forEach { error ->
                            Text(
                                text = "- ${error}+\n",
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(viewModel: ProfileViewModel) {
    when (viewModel.profileSection) {
        UserProfileSection.Main -> {
            MainTopBar(
                viewModel.userProfile.username.toString(),
                navIcon = {},
                icon = {
                    if (viewModel.isOwnProfile) {
                        Box(
                            contentAlignment = Alignment.TopEnd,
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(onClick = {
                                viewModel.profileSection = UserProfileSection.FriendsRequests
                            }) {
                                Icon(
                                    Icons.Filled.PersonAdd,
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            if (viewModel.friendsRequests.isNotEmpty()) {
                                Badge(
                                    modifier = Modifier.align(Alignment.BottomStart),
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ) {
                                    Text(
                                        text = viewModel.friendsRequests.size.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }) { viewModel.isLogoutDialogVisible = true }

        }

        UserProfileSection.FriendsList -> {
            TopApplicationBar(
                title = { Text(stringResource(R.string.friends)) },
                onClick = {
                    viewModel.profileSection =
                        UserProfileSection.Main
                })
        }

        UserProfileSection.FriendsRequests -> {
            TopApplicationBar(
                title = { Text(stringResource(R.string.friendsRequests)) },
                onClick = {
                    viewModel.profileSection =
                        UserProfileSection.Main
                }
            )
        }
    }
}

@Composable
fun ProfileBottomBar(navController: NavController, viewModel: ProfileViewModel) {
    ApplicationBottomBar(
        block = viewModel.isProgressIndicatorVisible,
        index = 2,
        goToProfile = {
            navController.navigate("${Screen.ProfileScreen.destination}/${viewModel.sessionManager.getUserId()}")
        },
        goToMainPage = {
            navController.popBackStack()
            navController.navigate(Screen.MainPageScreen.destination)
        },
        goToCreateTravel = {
            navController.navigate(Screen.TravelFormScreen.destination)
        })
}

@Composable
fun ProfileContent(
    innerpadding: PaddingValues,
    viewModel: ProfileViewModel,
    navController: NavController
) {
    when (viewModel.profileSection) {
        UserProfileSection.Main -> {
            ProfileSection(innerpadding, viewModel, navController)
        }

        UserProfileSection.FriendsList -> {
            FriendsListSection(innerpadding, viewModel, navController)
        }

        UserProfileSection.FriendsRequests -> {
            FriendsRequestsSection(innerpadding, viewModel, navController)
        }
    }
}

@Composable
fun FullScreenLoadingIndicator() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
    }
}