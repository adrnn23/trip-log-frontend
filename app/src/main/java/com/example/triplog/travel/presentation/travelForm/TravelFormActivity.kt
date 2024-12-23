package com.example.triplog.travel.presentation.travelForm

import EditLocalizationSection
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.main.navigation.ButtonBottomBar
import com.example.triplog.main.navigation.TopApplicationBar
import com.example.triplog.profile.presentation.FullScreenLoadingIndicator
import com.example.triplog.travel.components.LocalizationSearchBar
import com.example.triplog.travel.data.PlaceData
import com.example.triplog.travel.data.TravelData
import com.example.triplog.travel.presentation.SharedTravelViewModel
import com.example.triplog.travel.presentation.travelForm.sections.TravelFormMainSection
import com.example.triplog.travel.presentation.travelForm.sections.EditPlaceInformationSection
import com.example.triplog.travel.presentation.travelForm.sections.EditTravelInformationSection
import com.example.triplog.travel.presentation.travelForm.sections.PlaceFormSection
import com.mapbox.geojson.Point

@Composable
fun TravelFormScreen(
    navController: NavController,
    sharedTravelViewModel: SharedTravelViewModel
) {
    val viewModel: TravelFormViewModel = viewModel(factory = TravelFormViewModel.Factory)

    LaunchedEffect(key1 = Unit) {
        if (sharedTravelViewModel.tempTravelDataToEdit != TravelData()) {
            viewModel.editScreen = sharedTravelViewModel.isTravelToEdit
            viewModel.setTravelToEdit(sharedTravelViewModel.tempTravelDataToEdit)
            sharedTravelViewModel.clearTempTravelDataEdit()
        }
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
            PlaceFormSection(innerpadding, viewModel)
        }

        TravelFormSection.EditTravelInformation -> {
            EditTravelInformationSection(innerpadding, viewModel)
        }

        TravelFormSection.EditTravelLocalization -> {
            EditLocalizationSection(
                innerpadding,
                pointTemp = viewModel.travelPointTemp,
                onNewPoint = { point: Point ->
                    viewModel.travelPointTemp = point
                }
            )
        }

        TravelFormSection.EditPlaceInformation -> {
            EditPlaceInformationSection(innerpadding, viewModel)
        }

        TravelFormSection.EditPlaceLocalization -> {
            EditLocalizationSection(
                innerpadding,
                pointTemp = viewModel.placePointTemp,
                onNewPoint = { point: Point ->
                    viewModel.placePointTemp = point
                }
            )
        }
    }
}

@Composable
fun TravelFormBottomBar(viewModel: TravelFormViewModel) {
    when (viewModel.section) {
        TravelFormSection.Main -> {
            val buttonLabel =
                if (!viewModel.editScreen) R.string.addNewTravel else R.string.editTravel
            ButtonBottomBar(buttonLabel) {
                viewModel.isCreateEditTravelDialogVisible = true
            }
        }

        TravelFormSection.EditTravelLocalization -> {
            ButtonBottomBar(R.string.saveLocalization) {
                if (viewModel.travelPointTemp != null) {
                    viewModel.travel.point = viewModel.travelPointTemp
                    viewModel.travelPointTemp = null
                } else {
                    viewModel.travel.point = null
                }
                viewModel.section = TravelFormSection.Main
            }
        }

        TravelFormSection.PlaceForm -> {
            val textId =
                if (viewModel.editedPlaceIndex != null) R.string.editPlaceAndSave else R.string.addNewPlace
            ButtonBottomBar(textId) {
                if (viewModel.editedPlaceIndex != null) {
                    viewModel.travelPlaces.removeAt(viewModel.editedPlaceIndex!!)
                    viewModel.editedPlaceIndex = null
                }
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
                    viewModel.place = PlaceData()
                    viewModel.placeNameTemp = ""
                    viewModel.placeDescriptionTemp = ""
                    viewModel.placeImage = null

                    viewModel.travelPlaces.add(newPlace)
                    viewModel.section = TravelFormSection.Main
                }
            }
        }

        TravelFormSection.EditPlaceLocalization -> {
            ButtonBottomBar(R.string.saveLocalization) {
                if (viewModel.placePointTemp != null) {
                    viewModel.place.point = viewModel.placePointTemp
                    viewModel.placePointTemp = null
                } else {
                    viewModel.place.point = null
                }
                viewModel.section = TravelFormSection.PlaceForm
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
                sharedTravelViewModel.clearTempTravelDataEdit()
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

        TravelFormSection.EditTravelLocalization -> {
            TopApplicationBar(title = {
                LocalizationSearchBar(
                    stringResource(
                        R.string.mapbox_access_token
                    ),
                    viewModel, "Travel"
                )
            }) {
                viewModel.travelPointTemp = null
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

        TravelFormSection.EditPlaceLocalization -> {
            TopApplicationBar(title = {
                LocalizationSearchBar(
                    stringResource(
                        R.string.mapbox_access_token
                    ),
                    viewModel, "Place"
                )
            }) {
                viewModel.placePointTemp = null
                viewModel.section = TravelFormSection.PlaceForm
            }
        }
    }
}
