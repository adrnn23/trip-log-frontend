package com.example.triplog.travel.presentation.travelGallery.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.triplog.travel.components.FavoritesPage
import com.example.triplog.travel.components.PlannedPage
import com.example.triplog.travel.components.TravelsPage
import com.example.triplog.travel.data.TravelGalleryTabs
import com.example.triplog.travel.presentation.travelGallery.TravelGalleryViewModel
import kotlinx.coroutines.launch

@Composable
fun TravelGallerySection(
    innerpadding: PaddingValues,
    viewModel: TravelGalleryViewModel
) {
    val tabs = TravelGalleryTabs()
    var currentTab by remember { mutableStateOf(0) }
    val pagerState =
        rememberPagerState(initialPage = currentTab, pageCount = { tabs.travelGalleryTabs.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerpadding)
    ) {
        TabRow(
            selectedTabIndex = currentTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.travelGalleryTabs.forEachIndexed { index, tab ->
                Tab(
                    selected = currentTab == index,
                    onClick = {
                        currentTab = index
                        scope.launch { pagerState.animateScrollToPage(index) }
                    },
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

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> TravelsPage(viewModel = viewModel)
                1 -> FavoritesPage(viewModel = viewModel)
                2 -> PlannedPage(viewModel = viewModel)
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        currentTab = pagerState.currentPage
    }
}