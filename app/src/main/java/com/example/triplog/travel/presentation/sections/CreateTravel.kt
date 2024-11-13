package com.example.triplog.travel.presentation.sections

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplog.R
import com.example.triplog.profile.components.TitleComponent
import com.example.triplog.travel.components.TravelInformationComponent
import com.example.triplog.travel.components.TravelPhotoComponent
import com.example.triplog.travel.components.TravelPlacesComponent
import com.example.triplog.travel.presentation.CreateTravelSection
import com.example.triplog.travel.presentation.CreateTravelViewModel

@Composable
fun CreateTravelMainSection(innerpadding: PaddingValues, viewModel: CreateTravelViewModel) {
    val alpha = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true) {
        alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 200))
    }
    LazyColumn(
        modifier = Modifier
            .alpha(alpha.value)
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = innerpadding,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        item {
            TitleComponent(
                R.string.addNewTravel,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                Modifier
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            TravelInformationComponent(viewModel, onClick = {
                viewModel.section =
                    CreateTravelSection.EditTravelInformation
                viewModel.travelNameTemp = viewModel.travel.name ?: ""
            })
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            TravelPhotoComponent(viewModel)
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            TravelPlacesComponent(
                viewModel,
                onClick = { viewModel.section = CreateTravelSection.AddPlaceMain })
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
