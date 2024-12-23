package com.example.triplog.travel.presentation.travelForm.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplog.R
import com.example.triplog.profile.components.TitleComponent
import com.example.triplog.travel.components.EditTravelDateComponent
import com.example.triplog.travel.components.EditTravelDescriptionComponent
import com.example.triplog.travel.components.EditTravelNameComponent
import com.example.triplog.travel.presentation.travelForm.TravelFormViewModel

@Composable
fun EditTravelInformationSection(
    innerpadding: PaddingValues,
    viewModel: TravelFormViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp),
        contentPadding = innerpadding,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            TitleComponent(
                R.string.editTravelInformation,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )
        }
        item {
            EditTravelNameComponent(viewModel)
        }
        item {
            EditTravelDescriptionComponent(viewModel)
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            EditTravelDateComponent(viewModel)
        }
    }
}