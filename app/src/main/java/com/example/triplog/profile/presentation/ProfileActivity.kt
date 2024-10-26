package com.example.triplog.profile.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.triplog.authorization.login.components.LinearIndicator
import com.example.triplog.main.navigation.BottomApplicationBar
import com.example.triplog.main.navigation.TopApplicationBar
import com.example.triplog.profile.components.LinksComponent
import com.example.triplog.profile.components.AboutMeComponent
import com.example.triplog.profile.components.TravelNavigation
import com.example.triplog.profile.components.ProfileMainInfoComponent

@Composable
fun ProfileScreen(navController: NavController, token: String?) {
    val alpha = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true) {
        alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 200))
    }

    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.provideFactory(token))
    Scaffold(
        topBar = {
            TopApplicationBar("", navController)
        },
        bottomBar = {
            BottomApplicationBar()
        }
    ) { innerpadding ->
        LaunchedEffect(viewModel.loadingState) {
            viewModel.handleLoadingState()
        }
        if (viewModel.isProgressIndicatorVisible) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .alpha(alpha.value),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                LinearIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .alpha(alpha.value),
                contentPadding = innerpadding,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                item {
                    ProfileMainInfoComponent(
                        viewModel,
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
}