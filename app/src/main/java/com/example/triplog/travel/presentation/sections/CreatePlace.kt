package com.example.triplog.travel.presentation.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.triplog.R
import com.example.triplog.travel.components.PlaceInformationComponent
import com.example.triplog.travel.components.PlaceLocalizationComponent
import com.example.triplog.travel.components.PlacePhotoComponent
import com.example.triplog.travel.presentation.CreateTravelSection
import com.example.triplog.travel.presentation.CreateTravelViewModel

@Composable
fun AddPlaceMainSection(innerpadding: PaddingValues, viewModel: CreateTravelViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = innerpadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        item {
            Text(
                stringResource(R.string.addNewPlace),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                stringResource(R.string.completePlaceInformation),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 1.dp)
        }
        item {
            PlaceInformationComponent(
                viewModel,
                onClick = {
                    viewModel.section = CreateTravelSection.EditPlaceInformation
                    viewModel.placeNameTemp = viewModel.place.name ?: ""
                    viewModel.placeDescriptionTemp = viewModel.place.description ?: ""
                })
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            PlaceLocalizationComponent(
                viewModel,
                onClick = {
                    viewModel.section = CreateTravelSection.EditPlaceLocalization
                    viewModel.pointTemp = viewModel.place.point
                })
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            PlacePhotoComponent(viewModel)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}