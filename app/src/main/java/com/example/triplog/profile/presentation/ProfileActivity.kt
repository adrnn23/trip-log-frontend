package com.example.triplog.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.triplog.authorization.login.components.InformationDialog
import com.example.triplog.authorization.login.components.LinearIndicator
import com.example.triplog.main.navigation.ApplicationBottomBar
import com.example.triplog.main.navigation.Screen
import com.example.triplog.main.navigation.TopBar
import com.example.triplog.profile.presentation.sections.FriendsListSection
import com.example.triplog.profile.presentation.sections.FriendsRequestsSection
import com.example.triplog.profile.presentation.sections.ProfileSection

@Composable
fun ProfileScreen(navController: NavController, id: Int?) {
    val viewModel: ProfileViewModel =
        viewModel(factory = ProfileViewModel.provideFactory())

    LaunchedEffect(key1 = Unit) {
        viewModel.initParams(id)
    }

    LaunchedEffect(viewModel.loadingState) {
        viewModel.handleLoadingState()
    }

    LaunchedEffect(viewModel.profileState) {
        viewModel.handleProfileState()
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
        if (viewModel.profileState != ProfileState.Unauthenticated && viewModel.profileState != ProfileState.Error && viewModel.profileState != ProfileState.Error) {
            Scaffold(
                topBar = {
                    when (viewModel.profileSection) {
                        UserProfileSection.Main -> {
                            TopBar(
                                viewModel.userProfile.username.toString(),
                                navIcon = {},
                                icon = {
                                    if (viewModel.isOwnProfile) {
                                        IconButton(onClick = {
                                            viewModel.profileSection =
                                                UserProfileSection.FriendsRequests
                                        }) {
                                            Icon(
                                                Icons.Filled.PersonAdd,
                                                contentDescription = null,
                                                modifier = Modifier.size(30.dp)
                                            )
                                        }
                                    }
                                }) { viewModel.logout() }
                        }

                        UserProfileSection.FriendsList -> {
                            TopBar(
                                stringResource(R.string.friends),
                                navIcon = {
                                    IconButton(onClick = {
                                        viewModel.profileSection =
                                            UserProfileSection.Main
                                    }) {
                                        Icon(
                                            Icons.Default.ArrowBack,
                                            contentDescription = null,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                },
                                icon = {}) {
                                viewModel.logout()
                            }
                        }

                        UserProfileSection.FriendsRequests -> {
                            TopBar(
                                stringResource(R.string.friendsRequests),
                                navIcon = {
                                    IconButton(onClick = {
                                        viewModel.profileSection =
                                            UserProfileSection.Main
                                    }) {
                                        Icon(
                                            Icons.Default.ArrowBack,
                                            contentDescription = null,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                },
                                icon = {}) {
                                viewModel.logout()
                            }
                        }
                    }
                },
                bottomBar = {
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
                            navController.navigate(Screen.CreateTravelScreen.destination)
                        })
                },
            )
            { innerpadding ->
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

            if (viewModel.profileState == ProfileState.LoggedOut) {
                InformationDialog(
                    R.string.operationResult,
                    text = {
                        Text(
                            text = viewModel.backendResponse.value.errors,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    icon = {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    containerColor = { MaterialTheme.colorScheme.primaryContainer },
                    onDismiss = { viewModel.logoutProcess(navController) },
                    onConfirmClick = { viewModel.logoutProcess(navController) })
            }

            if (viewModel.profileState == ProfileState.Error || viewModel.profileState == ProfileState.LoadingProfileError || viewModel.profileState == ProfileState.Unauthenticated) {
                InformationDialog(
                    R.string.operationResult,
                    text = {
                        Text(
                            text = viewModel.backendResponse.value.errors,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    },
                    icon = {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    },
                    containerColor = { MaterialTheme.colorScheme.errorContainer },
                    onDismiss = {
                        if (viewModel.profileState == ProfileState.LoadingProfileError)
                            viewModel.homeReturnProcess(navController)
                        if(viewModel.profileState ==ProfileState.Unauthenticated)
                            viewModel.logoutProcess(navController)
                        viewModel.isBackendResponseVisible = false
                    },
                    onConfirmClick = {
                        if (viewModel.profileState == ProfileState.LoadingProfileError)
                            viewModel.homeReturnProcess(navController)
                        if(viewModel.profileState ==ProfileState.Unauthenticated)
                            viewModel.logoutProcess(navController)
                        viewModel.isBackendResponseVisible = false
                    })
            }
        }
    }
}