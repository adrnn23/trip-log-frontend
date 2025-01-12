package com.example.triplog.travel.presentation.travelGallery

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.main.navigation.Screen
import com.example.triplog.main.navigation.TopApplicationBar
import com.example.triplog.profile.presentation.FullScreenLoadingIndicator
import com.example.triplog.travel.data.TravelData
import com.example.triplog.travel.presentation.SharedTravelViewModel
import com.example.triplog.travel.presentation.travelGallery.sections.TravelGallerySection
import com.example.triplog.travel.presentation.travelGallery.sections.TravelOverviewSection

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TravelGalleryScreen(
    viewModel: TravelGalleryViewModel,
    navController: NavController,
    sharedTravelViewModel: SharedTravelViewModel
) {
    LaunchedEffect(viewModel.travelGalleryState) {
        viewModel.handleTravelGalleryState()
    }
    Scaffold(
        topBar = { TravelGalleryTopBar(viewModel, navController) }
    ) { innerpadding ->
        if (viewModel.isProgressIndicatorVisible) {
            FullScreenLoadingIndicator()
        } else {
            TravelGalleryContent(innerpadding, viewModel, navController, sharedTravelViewModel)
        }
    }

    if (viewModel.isDeleteTravelDialogVisible) {
        AlertDialog(
            title = { Text(stringResource(R.string.delete)) },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.deleteTravel),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            },
            containerColor = MaterialTheme.colorScheme.errorContainer,
            onDismissRequest = { viewModel.setDeleteDialogVisibility(false) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setDeleteDialogVisibility(false)
                        viewModel.deleteTravel()
                    },
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.delete),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.setDeleteDialogVisibility(false) },
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        color = MaterialTheme.colorScheme.onErrorContainer
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
                            text = it ?: stringResource(R.string.operationWithoutMessage),
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
            onDismissRequest = { viewModel.handleProcesses(navController) },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.handleProcesses(navController) },
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
fun TravelGalleryTopBar(viewModel: TravelGalleryViewModel, navController: NavController) {
    when (viewModel.section) {
        TravelGallerySection.Main -> {
            TopApplicationBar(title = { Text(stringResource(R.string.travelGallery)) }) { navController.popBackStack() }
        }

        TravelGallerySection.TravelOverview -> {
            TopApplicationBar(title = { Text(stringResource(R.string.travelOverview)) }) {
                viewModel.section = TravelGallerySection.Main
                viewModel.travelOverview = TravelData()
            }
        }
    }
}

@Composable
fun TravelGalleryContent(
    innerpadding: PaddingValues,
    viewModel: TravelGalleryViewModel,
    navController: NavController,
    sharedTravelViewModel: SharedTravelViewModel
) {
    when (viewModel.section) {
        TravelGallerySection.Main -> {
            TravelGallerySection(innerpadding, viewModel)
        }

        TravelGallerySection.TravelOverview -> {
            TravelOverviewSection(
                innerpadding, viewModel.travelOverview,
                isOptionsVisible = true,
                onEditClick = {
                    val travel = viewModel.prepareTempTravelDataToSharedVM()
                    sharedTravelViewModel.setTravelData(travel)
                    sharedTravelViewModel.setTravelEdit(true)
                    viewModel.navigateToEditTravel(navController)
                },
                onDeleteClick = {
                    viewModel.setDeleteDialogVisibility(true)
                },
                onSeeMapClick = {
                    val travel = viewModel.prepareTempTravelDataToSharedVM()
                    sharedTravelViewModel.setTravelData(travel)
                    navController.navigate(Screen.MapScreen.destination)
                },
                updateFavoriteStatus = { isFavorite ->
                    viewModel.updateFavoriteStatus(isFavorite)
                }
            )
        }
    }
}