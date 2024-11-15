package com.example.triplog.main.presentation.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.triplog.main.components.SearchBar
import com.example.triplog.main.components.SearchResultsSection
import com.example.triplog.main.presentation.MainPageViewModel

@Composable
fun SearchSection(navController: NavController, viewModel: MainPageViewModel) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        SearchBar(viewModel = viewModel)
        Spacer(modifier = Modifier.height(12.dp))
        SearchResultsSection(viewModel = viewModel, navController = navController)
    }
}
