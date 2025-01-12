package com.example.triplog.travel.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.ui.graphics.vector.ImageVector

data class TravelGalleryTab(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val text: String
)

data class TravelGalleryTabs(
    val travelGalleryTabs: List<TravelGalleryTab> = listOf(
        TravelGalleryTab(
            Icons.Filled.TravelExplore,
            Icons.Outlined.TravelExplore,
            "Travels"
        ),
        TravelGalleryTab(
            Icons.Filled.Favorite,
            Icons.Outlined.Favorite,
            "Favorites"
        ),
        TravelGalleryTab(
            Icons.Filled.CalendarMonth,
            Icons.Outlined.CalendarMonth,
            "Planned"
        )
    )
)