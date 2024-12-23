package com.example.triplog.travel.presentation.travelGallery.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.triplog.main.navigation.Screen
import com.example.triplog.travel.components.PlacesListSection
import com.example.triplog.travel.components.TravelCardSection
import com.example.triplog.travel.data.TravelData
import com.example.triplog.travel.data.TravelFormTabs
import com.example.triplog.travel.presentation.travelForm.sections.TravelFormTabRow
import com.google.gson.Gson
import kotlinx.coroutines.launch

@Composable
fun TravelOverviewSection(
    navController:NavController,
    innerPadding: PaddingValues,
    travel: TravelData,
    isOptionsVisible: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val tabs = TravelFormTabs()
    var currentTab by remember { mutableStateOf(0) }
    val pagerState =
        rememberPagerState(initialPage = currentTab, pageCount = { tabs.travelFormTabs.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
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
                0 -> TravelCardSection(travel = travel,
                    isOptionsVisible = isOptionsVisible,
                    onEditClick = { onEditClick() },
                    onDeleteClick = { onDeleteClick() },
                    seeMapClick = {
                        val points = mutableListOf<Pair<Double, Double>>()
                        if(travel.point!=null){
                            points.add(Pair(travel.point!!.longitude(), travel.point!!.latitude()))
                        }
                        points.addAll(
                            travel.places.mapNotNull { place ->
                                place?.point?.let { Pair(it.longitude(), it.latitude()) }
                            }
                        )
                        val pointsString = Gson().toJson(points)
                        navController.navigate("${Screen.MapScreen.destination}/$pointsString")
                    }

                )

                1 -> PlacesListSection(travel.places, navController)
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        currentTab = pagerState.currentPage
    }
}