package com.example.triplog.travel.presentation.travelForm

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.main.navigation.ButtonBottomBar
import com.example.triplog.main.navigation.TopApplicationBar
import com.example.triplog.profile.components.showToast
import com.example.triplog.profile.presentation.FullScreenLoadingIndicator
import com.example.triplog.travel.data.PlaceData
import com.example.triplog.travel.data.TravelData
import com.example.triplog.travel.presentation.PointType
import com.example.triplog.travel.presentation.SharedTravelViewModel
import com.example.triplog.travel.presentation.travelForm.sections.TravelFormMainSection
import com.example.triplog.travel.presentation.travelForm.sections.EditPlaceInformationSection
import com.example.triplog.travel.presentation.travelForm.sections.EditTravelInformationSection
import com.example.triplog.travel.presentation.travelForm.sections.PlaceFormSection

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TravelFormScreen(
    viewModel: TravelFormViewModel,
    navController: NavController,
    sharedTravelViewModel: SharedTravelViewModel
) {
    LaunchedEffect(key1 = Unit) {
        if (sharedTravelViewModel.isTravelToEdit && sharedTravelViewModel.tempTravelData != TravelData()) {
            viewModel.editScreen = sharedTravelViewModel.isTravelToEdit
            viewModel.setTravelToEdit(sharedTravelViewModel.tempTravelData)
        }
        if (sharedTravelViewModel.tempPointType != PointType.None) {
            viewModel.setTravelToEdit(sharedTravelViewModel.tempTravelData)
            viewModel.setPlaceToEdit(sharedTravelViewModel.tempPlaceData)
            if (sharedTravelViewModel.tempPointType == PointType.Place) {
                viewModel.editedPlaceIndex = sharedTravelViewModel.editedPlaceIndex
                viewModel.section = TravelFormSection.PlaceForm
            }
            sharedTravelViewModel.clearPlaceData()
            sharedTravelViewModel.clearPointType()
        }
        sharedTravelViewModel.clearTravelData()
        viewModel.getTravelCategories()
    }

    LaunchedEffect(viewModel.travelFormState) {
        viewModel.handleTravelFormState()
    }

    Scaffold(
        topBar = { TravelFormTopBar(navController, viewModel, sharedTravelViewModel) },
        bottomBar = { TravelFormBottomBar(viewModel) },
    ) { innerpadding ->
        if (viewModel.isProgressIndicatorVisible) {
            FullScreenLoadingIndicator()
        } else {
            TravelFormContent(innerpadding, viewModel, navController, sharedTravelViewModel)
        }
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
fun TravelFormContent(
    innerpadding: PaddingValues,
    viewModel: TravelFormViewModel,
    navController: NavController,
    sharedTravelViewModel: SharedTravelViewModel
) {

    when (viewModel.section) {
        TravelFormSection.Main -> {
            viewModel.getStaticMap("Travel", stringResource(R.string.mapbox_access_token))
            TravelFormMainSection(innerpadding, viewModel, navController, sharedTravelViewModel)
        }

        TravelFormSection.PlaceForm -> {
            viewModel.getStaticMap("Place", stringResource(R.string.mapbox_access_token))
            PlaceFormSection(innerpadding, viewModel, sharedTravelViewModel, navController)
        }

        TravelFormSection.EditTravelInformation -> {
            EditTravelInformationSection(innerpadding, viewModel)
        }

        TravelFormSection.EditPlaceInformation -> {
            EditPlaceInformationSection(innerpadding, viewModel)
        }
    }
}

@Composable
fun TravelFormBottomBar(viewModel: TravelFormViewModel) {
    val context = LocalContext.current

    when (viewModel.section) {
        TravelFormSection.Main -> {
            val buttonLabel =
                if (!viewModel.editScreen) R.string.addNewTravel else R.string.editTravel
            ButtonBottomBar(buttonLabel) {
                viewModel.isCreateEditTravelDialogVisible = true
            }
        }

        TravelFormSection.PlaceForm -> {
            val textId =
                if (viewModel.editedPlaceIndex != null) R.string.editPlaceAndSave else R.string.addNewPlace
            ButtonBottomBar(textId) {
                if (viewModel.place.name.isNullOrEmpty().not() &&
                    viewModel.place.description.isNullOrEmpty().not() &&
                    viewModel.place.point != null
                ) {
                    val newPlace = PlaceData(
                        id = viewModel.place.id,
                        viewModel.place.name,
                        viewModel.place.description,
                        viewModel.placeImage,
                        viewModel.place.category,
                        viewModel.place.point,
                        viewModel.place.imagePart
                    )

                    if (viewModel.editedPlaceIndex != null) {
                        viewModel.editedPlaceIndex?.let { index ->
                            viewModel.travelPlaces[index] = newPlace
                        }
                    } else {
                        viewModel.travelPlaces.add(newPlace)
                    }

                    viewModel.place = PlaceData()
                    viewModel.placeNameTemp = ""
                    viewModel.placeDescriptionTemp = ""
                    viewModel.placeImage = null
                    viewModel.editedPlaceIndex = null

                    showToast(context, R.string.placeAddedToTravel)
                    viewModel.section = TravelFormSection.Main
                }
            }

        }

        else -> {}
    }
}

@Composable
fun TravelFormTopBar(
    navController: NavController,
    viewModel: TravelFormViewModel,
    sharedTravelViewModel: SharedTravelViewModel
) {
    when (viewModel.section) {
        TravelFormSection.Main -> {
            val topText =
                if (!viewModel.editScreen) R.string.createNewTravel else R.string.editTravel
            TopApplicationBar(title = { Text(stringResource(topText)) }) {
                navController.popBackStack()
                sharedTravelViewModel.clearTravelData()
                sharedTravelViewModel.setTravelEdit(false)
            }
        }

        TravelFormSection.EditTravelInformation -> {
            TopApplicationBar(title = { Text(stringResource(R.string.editTravelInformation)) }) {
                viewModel.travel.name = viewModel.travelNameTemp
                viewModel.travel.description = viewModel.travelDescriptionTemp
                viewModel.travelNameTemp = null
                viewModel.travelDescriptionTemp = null
                viewModel.section = TravelFormSection.Main
            }
        }

        TravelFormSection.PlaceForm -> {
            val topText =
                if (viewModel.editedPlaceIndex != null) R.string.editPlace else R.string.newPlace
            TopApplicationBar(title = { Text(stringResource(topText)) }) {
                viewModel.place = PlaceData()
                viewModel.placeNameTemp = null
                viewModel.placeDescriptionTemp = null
                viewModel.placeImage = null
                viewModel.editedPlaceIndex = null
                viewModel.section = TravelFormSection.Main
            }
        }

        TravelFormSection.EditPlaceInformation -> {
            TopApplicationBar(title = { Text(stringResource(R.string.editPlaceInformation)) }) {
                viewModel.place.name = viewModel.placeNameTemp
                viewModel.place.description = viewModel.placeDescriptionTemp
                viewModel.placeNameTemp = ""
                viewModel.placeDescriptionTemp = ""
                viewModel.section = TravelFormSection.PlaceForm
            }
        }
    }
}