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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.triplog.travel.components.PlacesListSection
import com.example.triplog.travel.components.TravelCardSection
import com.example.triplog.travel.data.TravelData
import com.example.triplog.travel.data.TravelFormTabs
import com.example.triplog.travel.presentation.travelForm.sections.TravelFormTabRow
import kotlinx.coroutines.launch

@Composable
fun TravelOverviewSection(
    innerPadding: PaddingValues,
    travel: TravelData,
    isOptionsVisible: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSeeMapClick: () -> Unit,
    updateFavoriteStatus: (Boolean) -> Unit
) {
    val tabs = TravelFormTabs()
    var currentTab by remember { mutableIntStateOf(0) }
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
                    seeMapClick = { onSeeMapClick() },
                    onCheckedChange = { checked ->
                        updateFavoriteStatus(checked)
                    }
                )

                1 -> PlacesListSection(travel.places)
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        currentTab = pagerState.currentPage
    }
}