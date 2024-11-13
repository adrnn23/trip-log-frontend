package com.example.triplog.travel.presentation.sections

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplog.travel.presentation.CreateTravelViewModel

@Composable
fun EditPlaceDescriptionSection(innerpadding: PaddingValues, viewModel: CreateTravelViewModel) {
    val alpha = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true) {
        alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 200))
    }

    Box(
        modifier = Modifier
            .alpha(alpha.value)
            .padding(innerpadding)
            .fillMaxSize()
            .padding(2.dp)
    ) {
        BasicTextField(
            value = viewModel.placeDescriptionTemp,
            onValueChange = { viewModel.placeDescriptionTemp = it },
            textStyle = TextStyle(fontSize = 24.sp),
            modifier = Modifier.fillMaxSize()
        )
    }
}