package com.example.triplog.main.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.triplog.authorization.login.components.InformationDialog
import com.example.triplog.authorization.login.components.LinearIndicator
import com.example.triplog.main.components.MainPageScreenComponent
import com.example.triplog.main.navigation.ApplicationBottomBar
import com.example.triplog.main.navigation.ApplicationTopBar
import com.example.triplog.main.navigation.Screen
import com.example.triplog.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainPageScreen(navController: NavController) {
    val viewModel: MainPageViewModel = viewModel(factory = MainPageViewModel.factory)

    LaunchedEffect(Unit) {
        viewModel.getAuthenticatedUserProfileData()
    }

    LaunchedEffect(viewModel.loadingState) {
        viewModel.handleLoadingState()
    }

    LaunchedEffect(viewModel.mainPageState) {
        viewModel.handleMainPageState(navController)
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
        when (viewModel.mainPageState) {
            MainPageState.Idle -> {}

            MainPageState.Authenticated -> {
                Scaffold(
                    topBar = { ApplicationTopBar("Main page") { viewModel.logout() } },
                    bottomBar = {
                        ApplicationBottomBar(
                            block = viewModel.isProgressIndicatorVisible,
                            index = 0,
                            goToMainPage = {},
                            goToProfile = {
                                navController.navigate("${Screen.ProfileScreen.destination}/${viewModel.sessionManager.getToken()}/${viewModel.authenticatedUserProfile.email}/${viewModel.authenticatedUserProfile.id}")
                            },
                            goToCreateTravel = {
                                navController.navigate(Screen.CreateTravelScreen.destination)
                            }
                        )
                    }
                ) {
                    MainPageScreenComponent()
                }
            }

            MainPageState.LoggedOut -> {
                if (viewModel.isBackendResponseVisible) {
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
            }

            else -> {
                if (viewModel.isBackendResponseVisible) {
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
            }
        }
    }
}