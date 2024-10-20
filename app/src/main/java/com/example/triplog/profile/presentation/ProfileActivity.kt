package com.example.triplog.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.triplog.main.navigation.BottomApplicationBar
import com.example.triplog.main.navigation.TopApplicationBar
import com.example.triplog.profile.components.LinksComponent
import com.example.triplog.profile.components.AboutMeComponent
import com.example.triplog.profile.components.TravelNavigation
import com.example.triplog.profile.components.ProfileMainInfoComponent

@Composable
fun ProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
    Scaffold(
        topBar = {
            TopApplicationBar("", navController)
        },
        bottomBar = {
            BottomApplicationBar()
        }
    ) { innerpadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            contentPadding = innerpadding,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                ProfileMainInfoComponent(
                    count1 = viewModel.tripsCount,
                    count2 = viewModel.plannedCount,
                    viewModel.travelPreferences,
                    navController
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            item { AboutMeComponent(viewModel.bio) }

            item {
                LinksComponent(
                    viewModel.links, Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            item {
                TravelNavigation()
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}