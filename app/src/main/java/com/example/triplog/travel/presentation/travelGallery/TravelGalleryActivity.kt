package com.example.triplog.travel.presentation.travelGallery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.main.navigation.Screen
import com.example.triplog.main.navigation.TopApplicationBar
import com.example.triplog.travel.data.TravelData
import com.example.triplog.travel.presentation.SharedTravelViewModel
import com.example.triplog.travel.presentation.travelGallery.sections.TravelGallerySection
import com.example.triplog.travel.presentation.travelGallery.sections.TravelOverviewSection

@Composable
fun TravelGalleryScreen(
    navController: NavController,
    sharedTravelViewModel: SharedTravelViewModel
) {
    val viewModel: TravelGalleryViewModel = viewModel(factory = TravelGalleryViewModel.Factory)

    Scaffold(
        topBar = { TravelGalleryTopBar(viewModel, navController) }
    ) { innerpadding ->
        TravelGalleryContent(innerpadding, viewModel, navController, sharedTravelViewModel)
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
            onDismissRequest = {
                viewModel.setDeleteDialogVisibility(false)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setDeleteDialogVisibility(false)
                        viewModel.section = TravelGallerySection.Main
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
                    onClick = {
                        viewModel.setDeleteDialogVisibility(false)
                    },
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
                navController,
                innerpadding, viewModel.travelOverview,
                isOptionsVisible = true,
                onEditClick = {
                    val travel = viewModel.prepareTempTravelDataToSharedVM()
                    sharedTravelViewModel.setTempTravelDataEdit(travel)
                    sharedTravelViewModel.setTravelEdit(true)
                    viewModel.navigateToEditTravel(navController)
                },
                onDeleteClick = {
                    viewModel.setDeleteDialogVisibility(true)
                },
                onSeeMapClick = {
                    val travel = viewModel.prepareTempTravelDataToSharedVM()
                    sharedTravelViewModel.setTempTravelDataEdit(travel)
                    navController.navigate(Screen.MapScreen.destination)
                }
            )
        }
    }
}