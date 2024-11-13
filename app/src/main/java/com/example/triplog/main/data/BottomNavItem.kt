package com.example.triplog.main.data

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    var name:String,
    var icon :ImageVector,
    var selectedIcon:ImageVector,
    var function: () -> Unit
)