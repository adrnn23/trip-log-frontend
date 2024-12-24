package com.example.triplog.profile.presentation.sections

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplog.profile.components.ContentDivider
import com.example.triplog.profile.presentation.EditProfileViewModel

@Composable
fun EditTravelPreferencesSection(innerPadding: PaddingValues, viewModel: EditProfileViewModel) {
    val alpha = remember { Animatable(0f) }
    val maxSelectedItems = 9

    LaunchedEffect(key1 = true) {
        alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 200))
    }

    val selectedCount = viewModel.tempTravelPreferencesList.count { it?.isSelected == true }

    Column(
        modifier = Modifier
            .alpha(alpha.value)
            .padding(innerPadding)
            .fillMaxSize()
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        LazyColumn {
            items(viewModel.tempTravelPreferencesList.size) { i ->
                val isSelected = viewModel.tempTravelPreferencesList[i]?.isSelected == true
                val isSelectable = isSelected || selectedCount < maxSelectedItems

                Column {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(enabled = isSelectable) {
                                viewModel.tempTravelPreferencesList = viewModel.tempTravelPreferencesList
                                    .mapIndexed { j, item ->
                                        if (i == j) {
                                            item?.copy(isSelected = !item.isSelected)
                                        } else item
                                    }
                                    .toMutableList()
                            }
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = viewModel.tempTravelPreferencesList[i]?.name ?: "",
                            fontSize = 18.sp,
                            color = if (isSelectable) Color.Unspecified else Color.Gray
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                    ContentDivider()
                }
            }
        }
    }
}