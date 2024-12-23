package com.example.triplog.main.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.triplog.main.components.MainPageScreenComponent
import com.example.triplog.main.navigation.ApplicationBottomBar
import com.example.triplog.main.navigation.Screen
import com.example.triplog.R
import com.example.triplog.main.components.SearchBar
import com.example.triplog.main.navigation.MainTopBar
import com.example.triplog.main.navigation.SearchBottomBar
import com.example.triplog.main.navigation.TopApplicationBar
import com.example.triplog.main.presentation.sections.SearchSection
import com.example.triplog.profile.presentation.FullScreenLoadingIndicator
import com.example.triplog.travel.data.TravelData
import com.example.triplog.travel.presentation.travelGallery.sections.TravelOverviewSection

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MainPageScreen(navController: NavController) {
    val viewModel: MainPageViewModel = viewModel(factory = MainPageViewModel.factory)

    LaunchedEffect(key1 = Unit) {
        viewModel.getAuthenticatedUserProfileData()
    }

    LaunchedEffect(viewModel.loadingState, viewModel.mainPageState) {
        viewModel.handleLoadingState()
        viewModel.handleMainPageState()
    }

    Scaffold(
        topBar = { MainPageTopBar(viewModel) },
        bottomBar = { MainPageBottomBar(navController, viewModel) },
    ) { innerpadding ->
        if (viewModel.isProgressIndicatorVisible) {
            FullScreenLoadingIndicator()
        } else {
            MainPageContent(innerpadding, viewModel, navController)
        }
    }

    if (viewModel.isLogoutDialogVisible) {
        AlertDialog(title = { Text(stringResource(R.string.doYouWantToLogout)) }, icon = {
            Icon(
                Icons.Filled.Logout,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }, containerColor = MaterialTheme.colorScheme.tertiaryContainer, onDismissRequest = {
            viewModel.isLogoutDialogVisible = false
        }, dismissButton = {
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
        }, confirmButton = {
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
        })
    }

    if (viewModel.isBackendResponseVisible) {
        AlertDialog(title = { Text(stringResource(R.string.operationResult)) }, text = {
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
        }, icon = {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }, containerColor = MaterialTheme.colorScheme.primaryContainer, onDismissRequest = {
            viewModel.handleProcesses(navController)
        }, confirmButton = {
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
        })
    }
}

@Composable
fun MainPageContent(
    innerpadding: PaddingValues, viewModel: MainPageViewModel, navController: NavController
) {

    when (viewModel.mainPageSection) {
        MainPageSection.Main -> {
            Column(
                modifier = Modifier
                    .padding(innerpadding)
                    .fillMaxSize()
            ) {
                MainPageScreenComponent(viewModel)
            }
        }

        MainPageSection.SearchSection -> {
            Column(
                modifier = Modifier
                    .padding(innerpadding)
                    .fillMaxSize()
            ) {
                SearchSection(navController, viewModel)
            }
        }

        MainPageSection.TravelPostOverviewSection -> {
            TravelOverviewSection(
                navController,
                innerpadding, viewModel.travelOverview,
                isOptionsVisible = false,
                onEditClick = {},
                onDeleteClick = {},
            )
        }
    }
}

@Composable
fun MainPageBottomBar(navController: NavController, viewModel: MainPageViewModel) {
    when (viewModel.mainPageSection) {
        MainPageSection.Main -> {
            ApplicationBottomBar(block = viewModel.isProgressIndicatorVisible,
                index = 0,
                goToMainPage = {},
                goToProfile = {
                    navController.navigate("${Screen.ProfileScreen.destination}/${viewModel.sessionManager.getUserId()}")
                },
                goToCreateTravel = {
                    navController.navigate(Screen.CreateTravelScreen.destination)
                })
        }

        MainPageSection.SearchSection -> {
            if (viewModel.totalPages > 0) {
                SearchBottomBar(viewModel)
            }
        }

        else -> {}
    }
}


@Composable
fun MainPageTopBar(viewModel: MainPageViewModel) {
    when (viewModel.mainPageSection) {
        MainPageSection.Main -> {
            MainTopBar(title = "Main page", navIcon = {}, icon = {
                IconButton(onClick = {
                    viewModel.mainPageSection = MainPageSection.SearchSection
                }) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }) { viewModel.isLogoutDialogVisible = true }
        }

        MainPageSection.SearchSection -> {
            TopApplicationBar(title = { SearchBar(viewModel) }, onClick = {
                viewModel.mainPageSection = MainPageSection.Main
            })
        }

        MainPageSection.TravelPostOverviewSection -> {
            TopApplicationBar(title = { Text(stringResource(R.string.travelOverview)) }) {
                viewModel.mainPageSection = MainPageSection.Main
                viewModel.travelOverview = TravelData()
            }
        }
    }
}
