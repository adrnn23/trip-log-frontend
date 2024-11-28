package com.example.triplog.travel.presentation.create

import EditPlaceLocalizationSection
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
import com.example.triplog.travel.presentation.create.sections.AddPlaceMainSection
import com.example.triplog.travel.presentation.create.sections.CreateTravelMainSection
import com.example.triplog.travel.presentation.create.sections.EditPlaceInformationSection
import com.example.triplog.travel.presentation.create.sections.EditTravelInformationSection

@Composable
fun CreateTravelScreen(
    navController: NavController
) {
    val viewModel: CreateTravelViewModel = viewModel(factory = CreateTravelViewModel.Factory)

    LaunchedEffect(key1 = Unit) {
        viewModel.getTravelCategories()
    }

    LaunchedEffect(viewModel.createTravelState) {
        viewModel.handleCreateState()
    }

    Scaffold(
        topBar = { CreateTravelTopBar(navController, viewModel) },
        bottomBar = { CreateTravelBottomBar(viewModel) },
    ) { innerpadding ->
        if (viewModel.isProgressIndicatorVisible) {
            FullScreenLoadingIndicator()
        } else {
            CreateTravelContent(innerpadding, viewModel)
        }
    }
}

@Composable
fun CreateTravelContent(
    innerpadding: PaddingValues,
    viewModel: CreateTravelViewModel
) {
    when (viewModel.section) {
        CreateTravelSection.Main -> {
            CreateTravelMainSection(innerpadding, viewModel)
        }

        CreateTravelSection.EditTravelInformation -> {
            EditTravelInformationSection(innerpadding, viewModel)
        }

        CreateTravelSection.EditTravelPlaces -> {

        }

        CreateTravelSection.AddPlaceMain -> {
            AddPlaceMainSection(innerpadding, viewModel)
        }

        CreateTravelSection.EditPlaceInformation -> {
            EditPlaceInformationSection(innerpadding, viewModel)
        }

        CreateTravelSection.EditPlaceLocalization -> {
            EditPlaceLocalizationSection(
                innerpadding,
                viewModel
            )
        }
    }
}

@Composable
fun CreateTravelBottomBar(viewModel: CreateTravelViewModel) {
    when (viewModel.section) {
        CreateTravelSection.Main -> {
            ButtonBottomBar(R.string.addNewTravel) {
                viewModel.isCreateNewTravelDialogVisible = true
            }
        }

        CreateTravelSection.EditTravelInformation -> {}

        CreateTravelSection.EditTravelPlaces -> {}

        CreateTravelSection.AddPlaceMain -> {
            ButtonBottomBar(R.string.addNewPlace) {
                if (viewModel.place.name != null && viewModel.place.name != ""
                    && viewModel.place.description != null && viewModel.place.description != ""
                    && viewModel.place.point != null
                ) {
                    val newPlace = PlaceData(
                        viewModel.place.name,
                        viewModel.place.description,
                        viewModel.place.category,
                        viewModel.placeImage,
                        viewModel.place.point
                    )
                    viewModel.place = PlaceData()

                    viewModel.placeNameTemp = ""
                    viewModel.placeDescriptionTemp = ""
                    viewModel.placeImage = null
                    viewModel.travelPlaces.add(newPlace)
                    viewModel.section = CreateTravelSection.Main
                }
            }
        }

        CreateTravelSection.EditPlaceLocalization -> {
            ButtonBottomBar(R.string.addNewPlace) {
                if (viewModel.pointTemp != null) {
                    viewModel.place.point = viewModel.pointTemp
                    viewModel.pointTemp = null
                } else {
                    viewModel.place.point = null
                }
                viewModel.section = CreateTravelSection.AddPlaceMain
            }
        }

        else -> {}
    }
}

@Composable
fun CreateTravelTopBar(navController: NavController, viewModel: CreateTravelViewModel) {
    when (viewModel.section) {
        CreateTravelSection.Main -> {
            TopApplicationBar(title = { Text(stringResource(R.string.createNewTravel)) }) { navController.popBackStack() }
        }

        CreateTravelSection.EditTravelInformation -> {
            TopApplicationBar(title = { Text(stringResource(R.string.editTravelInformation)) }) {
                viewModel.travel.name = viewModel.travelNameTemp
                viewModel.travel.description = viewModel.travelDescriptionTemp
                viewModel.travelNameTemp = ""
                viewModel.travelDescriptionTemp = ""
                viewModel.section = CreateTravelSection.Main
            }
        }

        CreateTravelSection.EditTravelPlaces -> {}

        CreateTravelSection.AddPlaceMain -> {
            TopApplicationBar(title = { Text(stringResource(R.string.addNewPlace)) }) {
                viewModel.place = PlaceData()
                viewModel.placeNameTemp = ""
                viewModel.placeDescriptionTemp = ""
                viewModel.placeImage = null
                viewModel.section = CreateTravelSection.Main
            }
        }

        CreateTravelSection.EditPlaceInformation -> {
            TopApplicationBar(title = { Text(stringResource(R.string.editPlaceInformation)) }) {
                viewModel.place.name = viewModel.placeNameTemp
                viewModel.place.description = viewModel.placeDescriptionTemp
                viewModel.placeNameTemp = ""
                viewModel.placeDescriptionTemp = ""
                viewModel.section = CreateTravelSection.AddPlaceMain
            }
        }

        CreateTravelSection.EditPlaceLocalization -> {
            TopApplicationBar(title = {
                LocalizationSearchBar(
                    stringResource(
                        R.string.mapbox_access_token
                    ),
                    viewModel
                )
            }) {
                viewModel.pointTemp = null
                viewModel.section = CreateTravelSection.AddPlaceMain
            }
        }
    }
}
