package com.example.triplog.travel.presentation

import EditPlaceLocalizationSection
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.main.navigation.CreateTravelBottomBar
import com.example.triplog.main.navigation.CreateTravelTopBar
import com.example.triplog.travel.presentation.sections.AddPlaceMainSection
import com.example.triplog.travel.presentation.sections.CreateTravelMainSection
import com.example.triplog.travel.presentation.sections.EditPlaceDescriptionSection
import com.example.triplog.travel.presentation.sections.EditPlaceInformationSection
import com.example.triplog.travel.presentation.sections.EditTravelDescriptionSection
import com.example.triplog.travel.presentation.sections.EditTravelInformationSection

@Composable
fun CreateTravelScreen(
    navController: NavController
) {
    val viewModel: CreateTravelViewModel = viewModel(factory = CreateTravelViewModel.Factory)

    /*if (viewModel.isSaveChangesDialogVisible) {
        EditProfileDialog(
            icon = { Icon(Icons.Default.Save, contentDescription = null) },
            title = R.string.saveChanges,
            text = { Text(stringResource(R.string.wouldYouLikeToSaveChanges), fontSize = 14.sp) },
            onDismiss = { viewModel.isSaveChangesDialogVisible = false },
            onConfirmClick = {
                viewModel.isSaveChangesDialogVisible = false
                viewModel.editUserProfile()
            },
            onDismissClick = {
                viewModel.isSaveChangesDialogVisible = false
                navController.navigate("${Screen.ProfileScreen.destination}/${viewModel.sessionManager.getToken()}/${viewModel.editProfile.email}/${viewModel.editProfile.id}")
            })
    }*/

    Scaffold(
        topBar = {
            when (viewModel.section) {
                CreateTravelSection.Main -> {
                    CreateTravelTopBar(stringResource(R.string.createNewTravel)) { navController.popBackStack() }
                }

                CreateTravelSection.EditTravelInformation -> {
                    CreateTravelTopBar(stringResource(R.string.editTravelInformation)) {
                        viewModel.travel.name = viewModel.travelNameTemp
                        viewModel.travelNameTemp = ""
                        viewModel.travelDescriptionTemp = ""
                        viewModel.section = CreateTravelSection.Main
                    }
                }

                CreateTravelSection.EditTravelPlaces -> {}
                CreateTravelSection.EditTravelDescription -> {
                    CreateTravelTopBar(stringResource(R.string.editTravelDescription)) {
                        viewModel.travelDescriptionTemp = ""
                        viewModel.section = CreateTravelSection.EditTravelInformation
                    }
                }

                CreateTravelSection.AddPlaceMain -> {
                    CreateTravelTopBar(stringResource(R.string.addNewPlace)) {
                        viewModel.place = PlaceData()
                        viewModel.placeNameTemp = ""
                        viewModel.placeDescriptionTemp = ""
                        viewModel.placeImage = null
                        viewModel.section = CreateTravelSection.Main
                    }
                }

                CreateTravelSection.EditPlaceInformation -> {
                    CreateTravelTopBar(stringResource(R.string.editPlaceInformation)) {
                        viewModel.place.name = viewModel.placeNameTemp
                        viewModel.placeNameTemp = ""
                        viewModel.placeDescriptionTemp = ""
                        viewModel.section = CreateTravelSection.AddPlaceMain
                    }
                }

                CreateTravelSection.EditPlaceDescription -> {
                    CreateTravelTopBar(stringResource(R.string.editPlaceDescription)) {
                        viewModel.placeDescriptionTemp = ""
                        viewModel.section = CreateTravelSection.EditPlaceInformation
                    }
                }

                CreateTravelSection.EditPlaceLocalization -> {
                    CreateTravelTopBar(stringResource(R.string.editPlaceLocalization)) {
                        if(viewModel.pointTemp!=null){
                            viewModel.place.point = viewModel.pointTemp
                            viewModel.pointTemp = null
                            viewModel.section = CreateTravelSection.AddPlaceMain
                        }
                        else{
                            viewModel.place.point = null
                            viewModel.section = CreateTravelSection.AddPlaceMain
                        }
                    }
                }
            }
        },
        bottomBar = {
            when (viewModel.section) {
                CreateTravelSection.Main -> {
                    CreateTravelBottomBar(R.string.addNewTravel) {
                        viewModel.isCreateNewTravelDialogVisible = true
                    }
                }

                CreateTravelSection.EditTravelInformation -> {}

                CreateTravelSection.EditTravelPlaces -> {}

                CreateTravelSection.EditTravelDescription -> {
                    CreateTravelBottomBar(R.string.saveTravelDescription) {
                        viewModel.travel.description = viewModel.travelDescriptionTemp
                        viewModel.travelDescriptionTemp = ""
                        viewModel.section = CreateTravelSection.EditTravelInformation
                    }
                }

                CreateTravelSection.AddPlaceMain -> {
                    CreateTravelBottomBar(R.string.addNewPlace) {
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

                CreateTravelSection.EditPlaceInformation -> {}

                CreateTravelSection.EditPlaceDescription -> {
                    CreateTravelBottomBar(R.string.savePlaceDescription) {
                        viewModel.place.description = viewModel.placeDescriptionTemp
                        viewModel.placeDescriptionTemp = ""
                        viewModel.section = CreateTravelSection.EditPlaceInformation
                    }
                }

                CreateTravelSection.EditPlaceLocalization -> {}
            }
        }
    ) { innerpadding ->
        when (viewModel.section) {
            CreateTravelSection.Main -> {
                CreateTravelMainSection(innerpadding, viewModel)
            }

            CreateTravelSection.EditTravelInformation -> {
                EditTravelInformationSection(innerpadding, viewModel)
            }

            CreateTravelSection.EditTravelPlaces -> {

            }
            CreateTravelSection.EditTravelDescription -> {
                EditTravelDescriptionSection(innerpadding, viewModel)
            }

            CreateTravelSection.AddPlaceMain -> {
                AddPlaceMainSection(innerpadding, viewModel)
            }

            CreateTravelSection.EditPlaceInformation -> {
                EditPlaceInformationSection(innerpadding, viewModel)
            }

            CreateTravelSection.EditPlaceDescription -> {
                EditPlaceDescriptionSection(innerpadding, viewModel)
            }

            CreateTravelSection.EditPlaceLocalization -> {
                EditPlaceLocalizationSection(
                    innerpadding,
                    viewModel
                )
            }
        }
    }
}