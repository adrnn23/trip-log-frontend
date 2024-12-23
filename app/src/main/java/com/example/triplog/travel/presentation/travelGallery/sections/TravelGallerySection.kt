package com.example.triplog.travel.presentation.travelGallery.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.triplog.travel.components.TravelGalleryItem
import com.example.triplog.travel.data.TravelData
import com.example.triplog.travel.presentation.travelGallery.TravelGallerySection
import com.example.triplog.travel.presentation.travelGallery.TravelGalleryViewModel

@Composable
fun TravelGallerySection(
    innerpadding: PaddingValues,
    viewModel: TravelGalleryViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerpadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        viewModel.travelList.forEach { travel ->
            TravelGalleryItem(travel) {
                val travelOverview = TravelData(
                    travel.name,
                    travel.description,
                    travel.image,
                    travel.startDate,
                    travel.endDate,
                    travel.point,
                    travel.favourite,
                    travel.places
                )
                viewModel.travelOverview = travelOverview
                viewModel.section = TravelGallerySection.TravelOverview
            }
        }
    }
}