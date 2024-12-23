package com.example.triplog.profile.presentation.sections

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.triplog.profile.components.AboutMeComponent
import com.example.triplog.profile.components.LinksComponent
import com.example.triplog.profile.components.ProfileMainInfoComponent
import com.example.triplog.profile.components.TravelNavigation
import com.example.triplog.profile.presentation.ProfileViewModel

@Composable
fun ProfileSection(
    innerpadding: PaddingValues,
    viewModel: ProfileViewModel,
    navController: NavController
) {
    val alpha = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true) {
        alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 400))
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = innerpadding,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            ProfileMainInfoComponent(
                viewModel,
                navController
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            AboutMeComponent(viewModel.userProfile.bio)
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            LinksComponent(
                viewModel.userProfile.links, Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            TravelNavigation(navController)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}