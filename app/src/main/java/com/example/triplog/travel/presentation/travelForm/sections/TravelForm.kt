package com.example.triplog.travel.presentation.travelForm.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.main.navigation.Screen
import com.example.triplog.travel.components.PlaceInformationComponent
import com.example.triplog.travel.components.PlacePhotoComponent
import com.example.triplog.travel.components.TravelInformationComponent
import com.example.triplog.travel.components.TravelPhotoComponent
import com.example.triplog.travel.components.TravelPlaceLocalizationComponent
import com.example.triplog.travel.components.AddPlacesComponent
import com.example.triplog.travel.components.TravelPlacesList
import com.example.triplog.travel.data.TravelFormTabs
import com.example.triplog.travel.presentation.PointType
import com.example.triplog.travel.presentation.SharedTravelViewModel
import com.example.triplog.travel.presentation.travelForm.TravelFormSection
import com.example.triplog.travel.presentation.travelForm.TravelFormViewModel
import kotlinx.coroutines.launch

@Composable
fun TravelFormMainSection(
    innerpadding: PaddingValues,
    viewModel: TravelFormViewModel,
    navController: NavController,
    sharedTravelViewModel: SharedTravelViewModel
) {
    val tabs = TravelFormTabs()
    var currentTab by remember { mutableStateOf(0) }
    val pagerState =
        rememberPagerState(initialPage = currentTab, pageCount = { tabs.travelFormTabs.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerpadding)
    ) {
        TravelFormTabRow(
            currentTab = currentTab,
            onTabSelected = { tabIndex ->
                currentTab = tabIndex
                scope.launch {
                    pagerState.animateScrollToPage(tabIndex)
                }
            },
            tabs = tabs
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> TravelFormSection(
                    viewModel = viewModel,
                    navController = navController,
                    sharedTravelViewModel = sharedTravelViewModel
                )

                1 -> TravelPlacesList(viewModel = viewModel)
            }
        }

    }

    LaunchedEffect(pagerState.currentPage) {
        currentTab = pagerState.currentPage
    }

    if (viewModel.isCreateEditTravelDialogVisible) {
        AlertDialog(
            title = { Text(stringResource(if (!viewModel.editScreen) R.string.createNewTravel else R.string.editTravel)) },
            text = {
                val textId =
                    if (!viewModel.editScreen) R.string.addNewTravelQuestion else R.string.editTravelQuestion
                Text(
                    text = stringResource(textId),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            icon = { Icon(Icons.Default.Save, contentDescription = null) },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            onDismissRequest = { viewModel.isCreateEditTravelDialogVisible = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.isCreateEditTravelDialogVisible = false
                        if (viewModel.editScreen)
                            viewModel.editTravel()
                        else
                            viewModel.addTravel()
                    },
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.ok),
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.isCreateEditTravelDialogVisible = false },
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                    )
                }
            }
        )
    }
}

@Composable
fun TravelFormSection(
    viewModel: TravelFormViewModel,
    sharedTravelViewModel: SharedTravelViewModel,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                stringResource(R.string.completeTravelInformation),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 1.dp)
        }
        item {
            TravelInformationComponent(viewModel, onClick = {
                viewModel.section =
                    TravelFormSection.EditTravelInformation
                viewModel.travelNameTemp = viewModel.travel.name
                viewModel.travelDescriptionTemp = viewModel.travel.description
            })
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            AddPlacesComponent(
                viewModel,
                onAddPlaceClick = { viewModel.section = TravelFormSection.PlaceForm })
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            TravelPlaceLocalizationComponent(
                mapUrl = viewModel.travelStaticMapUrl,
                onClick = {
                    val travel = viewModel.prepareTempTravelDataToSharedVM()
                    val place = viewModel.prepareTempPlaceDataToSharedVM()
                    sharedTravelViewModel.setTravelData(travel)
                    sharedTravelViewModel.setPlaceData(place)
                    sharedTravelViewModel.tempPointType = PointType.Travel
                    navController.navigate(Screen.SearchMapScreen.destination)
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            TravelPhotoComponent(viewModel)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun PlaceFormSection(
    innerpadding: PaddingValues,
    viewModel: TravelFormViewModel,
    sharedTravelViewModel: SharedTravelViewModel,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = innerpadding,
        horizontalAlignment = Alignment.Start
    ) {
        item {
            Text(
                stringResource(R.string.completePlaceInformation),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 1.dp)
        }
        item {
            PlaceInformationComponent(
                viewModel,
                onClick = {
                    viewModel.section = TravelFormSection.EditPlaceInformation
                    viewModel.placeNameTemp = viewModel.place.name
                    viewModel.placeDescriptionTemp = viewModel.place.description
                })
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            TravelPlaceLocalizationComponent(
                mapUrl = viewModel.placeStaticMapUrl,
                onClick = {
                    val travel = viewModel.prepareTempTravelDataToSharedVM()
                    val place = viewModel.prepareTempPlaceDataToSharedVM()
                    sharedTravelViewModel.setTravelData(travel)
                    sharedTravelViewModel.setPlaceData(place)
                    sharedTravelViewModel.tempPointType = PointType.Place
                    sharedTravelViewModel.editedPlaceIndex = viewModel.editedPlaceIndex
                    navController.navigate(Screen.SearchMapScreen.destination)
                })
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            PlacePhotoComponent(viewModel)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun TravelFormTabRow(
    currentTab: Int,
    onTabSelected: (Int) -> Unit,
    tabs: TravelFormTabs
) {
    TabRow(
        selectedTabIndex = currentTab,
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        tabs.travelFormTabs.forEachIndexed { index, tab ->
            Tab(
                selected = currentTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (currentTab == index) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.text
                    )
                },
                text = { Text(text = tab.text) }
            )
        }
    }
}
