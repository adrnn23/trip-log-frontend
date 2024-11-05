package com.example.triplog.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.navDeepLink
import com.example.triplog.R
import com.example.triplog.authorization.login.components.InformationDialog
import com.example.triplog.authorization.login.components.LinearIndicator
import com.example.triplog.main.navigation.ApplicationBottomBar
import com.example.triplog.main.navigation.ApplicationTopBar
import com.example.triplog.main.navigation.Screen
import com.example.triplog.profile.components.LinksComponent
import com.example.triplog.profile.components.AboutMeComponent
import com.example.triplog.profile.components.TravelNavigation
import com.example.triplog.profile.components.ProfileMainInfoComponent

@Composable
fun ProfileScreen(navController: NavController, token: String?, id: Int?, email: String?) {
    val viewModel: ProfileViewModel =
        viewModel(factory = ProfileViewModel.provideFactory(token))

    LaunchedEffect(key1 = Unit) {
        viewModel.initParams(id, email)
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
        when (viewModel.profileState) {
            ProfileState.Idle -> {}

            ProfileState.Authenticated -> {
                Scaffold(
                    topBar = {
                        ApplicationTopBar(viewModel.userProfile.username.toString()) { viewModel.logout() }
                    },
                    bottomBar = {
                        ApplicationBottomBar(
                            block = viewModel.isProgressIndicatorVisible,
                            goToProfile = { },
                            goToMainPage = {
                                navController.popBackStack()
                                navController.navigate(Screen.MainPageScreen.destination)
                            })
                    }
                ) { innerpadding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentPadding = innerpadding,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        item {
                            ProfileMainInfoComponent(
                                viewModel,
                                navController
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        item { AboutMeComponent(viewModel.userProfile.bio) }

                        item {
                            LinksComponent(
                                viewModel.userProfile.links, Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        item {
                            TravelNavigation()
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }

            ProfileState.LoggedOut -> {
                InformationDialog(
                    R.string.operationResult,
                    text = {
                        Text(
                            text = viewModel.backendResponse.value.errors, fontSize = 14.sp,
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

            else -> {
                InformationDialog(
                    R.string.operationResult,
                    text = {
                        Text(
                            text = viewModel.backendResponse.value.errors, fontSize = 14.sp,
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
                        if(viewModel.profileState==ProfileState.Error)
                            viewModel.homeReturnProcess(navController)
                        else
                            viewModel.logoutProcess(navController) },
                    onConfirmClick = {
                        if(viewModel.profileState==ProfileState.Error)
                            viewModel.homeReturnProcess(navController)
                        else
                            viewModel.logoutProcess(navController)
                    })
            }
        }
    }
}