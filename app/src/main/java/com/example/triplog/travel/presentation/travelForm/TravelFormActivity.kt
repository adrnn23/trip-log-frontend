package com.example.triplog.travel.presentation.travelForm

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
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

@Composable
fun TravelFormScreen(
    navController: NavController,
    sharedTravelViewModel: SharedTravelViewModel
) {
    val viewModel: TravelFormViewModel = viewModel(factory = TravelFormViewModel.Factory)

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

    LaunchedEffect(viewModel.createTravelState) {
        viewModel.handleCreateState()
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
            TravelFormMainSection(innerpadding, viewModel, navController, sharedTravelViewModel)
        }

        TravelFormSection.PlaceForm -> {
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
                if (viewModel.place.name != null && viewModel.place.name != ""
                    && viewModel.place.description != null && viewModel.place.description != ""
                    && viewModel.place.point != null
                ) {
                    val newPlace = PlaceData(
                        viewModel.place.name,
                        viewModel.place.description,
                        viewModel.placeImage,
                        viewModel.place.category,
                        viewModel.place.point
                    )

                    viewModel.editedPlaceIndex?.let { index ->
                        val updatedList = viewModel.travelPlaces.toMutableList()
                        updatedList.removeAt(index)
                        viewModel.travelPlaces = updatedList
                    }

                    val updatedList = viewModel.travelPlaces.toMutableList()
                    updatedList.add(newPlace)
                    viewModel.travelPlaces = updatedList

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
                viewModel.travelNameTemp = ""
                viewModel.travelDescriptionTemp = ""
                viewModel.section = TravelFormSection.Main
            }
        }

        TravelFormSection.PlaceForm -> {
            val topText =
                if (viewModel.editedPlaceIndex != null) R.string.editPlace else R.string.newPlace
            TopApplicationBar(title = { Text(stringResource(topText)) }) {
                viewModel.place = PlaceData()
                viewModel.placeNameTemp = ""
                viewModel.placeDescriptionTemp = ""
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