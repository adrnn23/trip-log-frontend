package com.example.triplog.main.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.triplog.main.navigation.Screen
import com.example.triplog.R
import com.example.triplog.main.navigation.TopBar
import com.example.triplog.main.presentation.sections.SearchSection

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
        Scaffold(
            topBar = {
                when (viewModel.mainPageSection) {
                    MainPageSection.Main -> {
                        TopBar(
                            title = "Main page",
                            navIcon = {},
                            icon = {
                                IconButton(onClick = {
                                    viewModel.mainPageSection = MainPageSection.SearchSection
                                }) {
                                    Icon(
                                        Icons.Filled.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                        ) { viewModel.logout() }
                    }

                    MainPageSection.SearchSection -> {
                        TopBar(
                            title = "Search",
                            navIcon = {
                                IconButton(onClick = {
                                    viewModel.mainPageSection = MainPageSection.Main
                                }) {
                                    Icon(
                                        Icons.Filled.ArrowBack,
                                        contentDescription = null,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            },
                            icon = {}
                        ) { viewModel.logout() }
                    }
                }
            },
            bottomBar = {
                ApplicationBottomBar(
                    block = viewModel.isProgressIndicatorVisible,
                    index = 0,
                    goToMainPage = {},
                    goToProfile = {
                        navController.navigate("${Screen.ProfileScreen.destination}/${viewModel.sessionManager.getUserId()}")
                    },
                    goToCreateTravel = {
                        navController.navigate(Screen.CreateTravelScreen.destination)
                    }
                )
            },
            content = { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    when (viewModel.mainPageSection) {
                        MainPageSection.Main -> {
                            MainPageScreenComponent()
                        }

                        MainPageSection.SearchSection -> {
                            SearchSection(navController, viewModel)
                        }
                    }
                }
            }
        )
        if (viewModel.isBackendResponseVisible && viewModel.mainPageState in listOf(
                MainPageState.Unauthenticated,
                MainPageState.AuthenticationError,
                MainPageState.LoggedOut, MainPageState.Error
            )
        ) {
            InformationDialog(
                title = R.string.operationResult,
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
                onDismiss = {
                    viewModel.isBackendResponseVisible = false
                    if (viewModel.mainPageState in listOf(
                            MainPageState.Unauthenticated,
                            MainPageState.AuthenticationError,
                            MainPageState.LoggedOut
                        )
                    ) {
                        viewModel.logoutProcess(navController)
                    }
                },
                onConfirmClick = {
                    viewModel.isBackendResponseVisible = false
                    if (viewModel.mainPageState in listOf(
                            MainPageState.Unauthenticated,
                            MainPageState.AuthenticationError,
                            MainPageState.LoggedOut
                        )
                    ) {
                        viewModel.logoutProcess(navController)
                    }
                }
            )
        }
    }
}