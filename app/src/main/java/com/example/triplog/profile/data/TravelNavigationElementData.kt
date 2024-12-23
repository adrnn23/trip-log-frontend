package com.example.triplog.profile.data

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class TravelNavigationElementData(
    var icon: ImageVector,
    @StringRes val label: Int,
    var navigate: () -> Unit
)