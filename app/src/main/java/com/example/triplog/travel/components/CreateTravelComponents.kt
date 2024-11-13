package com.example.triplog.travel.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.triplog.R
import com.example.triplog.authorization.login.components.LinearIndicator
import com.example.triplog.profile.components.ChangeButton
import com.example.triplog.profile.components.TitleComponent
import com.example.triplog.travel.presentation.CreateTravelSection
import com.example.triplog.travel.presentation.CreateTravelViewModel
import com.mapbox.maps.extension.style.expressions.dsl.generated.mod
import kotlinx.coroutines.coroutineScope
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ButtonComponent(
    @StringRes text: Int,
    modifier: Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        shape = RoundedCornerShape(10.dp),
        onClick = {
            onClick()
        },
        modifier = modifier
    ) {
        Text(
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            text = stringResource(text),
            fontSize = 14.sp,
            modifier = Modifier
                .padding(2.dp)
        )
    }
}


@Composable
fun TravelInformationComponent(
    viewModel: CreateTravelViewModel, onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
    ) {
        TitleComponent(
            R.string.basicInformation,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(8.dp))
        TitleComponent(
            R.string.travelName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Text(text = viewModel.travel.name ?: "", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))

        TitleComponent(
            R.string.travelDescription,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Text(text = viewModel.travel.description ?: "", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))

        TitleComponent(
            R.string.travelDate,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = viewModel.travel.startDate ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.width(4.dp))
            Divider(modifier = Modifier.width(20.dp))
            Spacer(Modifier.width(4.dp))

            Text(
                text = viewModel.travel.endDate ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        ButtonComponent(R.string.editBasicInformation, modifier = Modifier, onClick = { onClick() })
    }
}


@Composable
fun TravelPhotoComponent(
    viewModel: CreateTravelViewModel
) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            viewModel.travelImage = uri
        }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
    ) {
        TitleComponent(
            R.string.travelPhoto,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box {
            if (viewModel.travelImage != null) {
                val painter = rememberAsyncImagePainter(
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(data =  viewModel.travelImage)
                        .build()
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(140.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        ButtonComponent(
            R.string.changeTravelPhoto,
            modifier = Modifier.width(200.dp),
            onClick = {
                launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
            })
    }
}


@Composable
fun TravelPlacesComponent(
    viewModel: CreateTravelViewModel,
    onClick: () -> Unit
) {
    val height = remember { Animatable(0f) }

    LaunchedEffect(viewModel.isPlacesVisible) {
        if (viewModel.isPlacesVisible) {
            height.animateTo(targetValue = 512f, animationSpec = tween(durationMillis = 400))
        } else {
            height.animateTo(targetValue = 0f, animationSpec = tween(durationMillis = 400))
        }
    }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
    ) {
        TitleComponent(
            R.string.travelPlaces,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            ButtonComponent(
                R.string.addNewPlace,
                modifier = Modifier.width(200.dp),
                onClick = { onClick() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            ButtonComponent(
                R.string.editPlaces,
                modifier = Modifier.width(200.dp),
                onClick = {
                    if (viewModel.travelPlaces.size > 0) {
                        viewModel.isPlacesVisible = !viewModel.isPlacesVisible
                    }
                })
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (viewModel.isDeleting) {
            LinearIndicator()
        } else if (viewModel.isPlacesVisible) {
            Column(modifier = Modifier.height(height.value.dp)) {
                viewModel.travelPlaces.forEach { place ->
                    PlaceCardInit(place) {
                        viewModel.removePlaceWithLoading(place)
                        viewModel.isPlacesVisible = false
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun EditTravelNameComponent(viewModel: CreateTravelViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        TravelPlaceNameInput(
            label = R.string.travelName,
            value = viewModel.travelNameTemp,
            onValueChanged = { viewModel.travelNameTemp = it },
            imageVector = Icons.Default.TravelExplore,
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        )
    }
}

@Composable
fun EditTravelDescriptionComponent(viewModel: CreateTravelViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        TravelPlaceDescriptionInput(
            R.string.travelDescription,
            value = viewModel.travel.description ?: "",
            imageVector = Icons.Default.Description,
            enabled = false,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(6.dp),
            onValueChanged = { }
        )
        ChangeButton(
            modifier = Modifier,
            icon = Icons.Default.Edit,
            changeAction = {
                viewModel.travelDescriptionTemp = viewModel.travel.description ?: ""
                viewModel.section = CreateTravelSection.EditTravelDescription
            })
    }
}

@Composable
fun TravelPlaceDescriptionInput(
    @StringRes label: Int,
    imageVector: ImageVector,
    value: String,
    enabled: Boolean,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val travelDescriptionIcon = @Composable {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
    OutlinedTextField(
        label = { Text(stringResource(label)) },
        value = value,
        onValueChange = onValueChanged,
        leadingIcon = travelDescriptionIcon,
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        ),
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTravelDateComponent(viewModel: CreateTravelViewModel) {

    var startDatePickerDialogVisible = remember { mutableStateOf(false) }
    var endDatePickerDialogVisible = remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    if (startDatePickerDialogVisible.value) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            confirmButton = {
                TextButton(onClick = {
                    startDatePickerDialogVisible.value = false
                    val startDateLong = datePickerState.selectedDateMillis ?: 0
                    startDate = if (startDateLong != 0L) {
                        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        dateFormat.format(startDateLong)
                    } else ""
                    viewModel.travel.startDate = startDate
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            onDismissRequest = {
                startDatePickerDialogVisible.value = false

            },
            dismissButton = {
                TextButton(onClick = {
                    startDatePickerDialogVisible.value = false
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (endDatePickerDialogVisible.value) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            confirmButton = {
                TextButton(onClick = {
                    endDatePickerDialogVisible.value = false
                    val endDateLong = datePickerState.selectedDateMillis ?: 0
                    endDate = if (endDateLong != 0L) {
                        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        dateFormat.format(endDateLong)
                    } else ""
                    viewModel.travel.endDate = endDate
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            onDismissRequest = {
                endDatePickerDialogVisible.value = false

            },
            dismissButton = {
                TextButton(onClick = {
                    endDatePickerDialogVisible.value = false
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }


    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth(0.96f)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(10.dp)
    ) {
        Icon(
            Icons.Default.EditCalendar,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(stringResource(R.string.travelDate), fontSize = 18.sp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ChangeButton(
                    modifier = Modifier,
                    icon = if (viewModel.travel.startDate != "") Icons.Default.Edit else Icons.Default.Add,
                    size = 24,
                    changeAction = {
                        startDatePickerDialogVisible.value = true
                    }
                )
                Text(
                    text = stringResource(R.string.startDate),
                    fontSize = 16.sp
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = viewModel.travel.startDate ?: "",
                    fontSize = 16.sp
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ChangeButton(
                    modifier = Modifier,
                    icon = if (viewModel.travel.endDate != "") Icons.Default.Edit else Icons.Default.Add,
                    size = 24,
                    changeAction = {
                        endDatePickerDialogVisible.value = true
                    }
                )
                Text(
                    text = stringResource(R.string.endDate),
                    fontSize = 16.sp
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = viewModel.travel.endDate ?: "",
                    fontSize = 16.sp
                )
            }

        }
    }
}


@Composable
fun TravelPlaceNameInput(
    @StringRes label: Int,
    imageVector: ImageVector,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val travelNameIcon = @Composable {
        Icon(
            imageVector,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
    OutlinedTextField(
        label = { Text(stringResource(label)) },
        value = value,
        onValueChange = onValueChanged,
        leadingIcon = travelNameIcon,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        ),
        modifier = modifier
    )
}