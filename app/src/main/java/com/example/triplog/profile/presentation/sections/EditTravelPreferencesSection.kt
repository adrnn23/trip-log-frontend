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
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplog.R
import com.example.triplog.authorization.login.components.InformationDialog
import com.example.triplog.profile.components.ContentDivider
import com.example.triplog.profile.data.ErrorData
import com.example.triplog.profile.data.ErrorType
import com.example.triplog.profile.presentation.EditProfileViewModel

@Composable
fun EditTravelPreferencesSection(innerpadding: PaddingValues, viewModel: EditProfileViewModel) {
    val alpha = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true) {
        alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 200))
    }

    if (viewModel.errorMessage.isError && viewModel.errorMessage.type==ErrorType.TravelPreferences) {
        InformationDialog(
            R.string.validationError,
            text = {
                Text(
                    stringResource(R.string.travelPreferencesNumber),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            },
            containerColor = MaterialTheme.colorScheme.errorContainer,
            onDismiss = { viewModel.errorMessage = ErrorData(false, null, "") },
            onConfirmClick = { viewModel.errorMessage = ErrorData(false, null, "") }
        )
    }

    Column(
        modifier = Modifier
            .alpha(alpha.value)
            .padding(innerpadding)
            .fillMaxSize()
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        LazyColumn {
            items(viewModel.travelPreferencesList.size) { i ->
                Column {
                    Row(horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                viewModel.travelPreferencesList = viewModel.travelPreferencesList
                                    .mapIndexed { j, item ->
                                        if (i == j) {
                                            item.copy(selected = !item.selected)
                                        } else item
                                    }
                                    .toMutableList()
                            }
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = viewModel.travelPreferencesList[i].name, fontSize = 18.sp)
                        if (viewModel.travelPreferencesList[i].selected) {
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