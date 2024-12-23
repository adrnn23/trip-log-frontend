package com.example.triplog.travel.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.ui.graphics.vector.ImageVector

data class TravelFormTab(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val text: String
)

data class TravelFormTabs(
    val travelFormTabs: List<TravelFormTab> = listOf(
        TravelFormTab(
            Icons.Filled.TravelExplore,
            Icons.Outlined.TravelExplore,
            "Travel"
        ),
        TravelFormTab(
            Icons.Filled.Place,
            Icons.Outlined.Place,
            "Places"
        )
    )
)