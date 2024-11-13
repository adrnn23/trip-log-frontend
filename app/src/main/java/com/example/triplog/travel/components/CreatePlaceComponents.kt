package com.example.triplog.travel.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.drawable.toBitmap
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.triplog.R
import com.example.triplog.profile.components.ChangeButton
import com.example.triplog.profile.components.TitleComponent
import com.example.triplog.travel.presentation.CreateTravelSection
import com.example.triplog.travel.presentation.CreateTravelViewModel
import com.example.triplog.travel.presentation.PlaceData
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

@Composable
fun PlaceInformationComponent(
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
            R.string.placeName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Text(text = viewModel.place.name ?: "", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))

        TitleComponent(
            R.string.placeDescription,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Text(text = viewModel.place.description ?: "", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))

        TitleComponent(
            R.string.placeCategory,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Text(text = viewModel.place.category ?: "", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))
        ButtonComponent(R.string.editBasicInformation, modifier = Modifier, onClick = { onClick() })
    }
}

@Composable
fun EditPlaceNameComponent(viewModel: CreateTravelViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        TravelPlaceNameInput(
            label = R.string.placeName,
            value = viewModel.placeNameTemp,
            onValueChanged = { viewModel.placeNameTemp = it },
            imageVector = Icons.Default.Place,
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        )
    }
}

@Composable
fun EditPlaceDescriptionComponent(viewModel: CreateTravelViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        TravelPlaceDescriptionInput(
            R.string.placeDescription,
            value = viewModel.place.description ?: "",
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
                viewModel.placeDescriptionTemp = viewModel.place.description ?: ""
                viewModel.section = CreateTravelSection.EditPlaceDescription
            })
    }
}

@Composable
fun EditPlaceCategoryComponent(viewModel: CreateTravelViewModel) {
    var isCategoriesVisible by remember { mutableStateOf(false) }

    val height = remember {
        Animatable(0f)
    }
    val alpha = remember {
        Animatable(0f)
    }

    LaunchedEffect(isCategoriesVisible) {
        if (isCategoriesVisible) {
            height.animateTo(targetValue = 256f, animationSpec = tween(durationMillis = 400))
            alpha.animateTo(targetValue = 0f, animationSpec = tween(durationMillis = 400))
        } else {
            height.animateTo(targetValue = 0f, animationSpec = tween(durationMillis = 400))
            alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 400))
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
        Column {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Category,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row {
                        Text(stringResource(R.string.placeCategory), fontSize = 18.sp)
                        Spacer(Modifier.width(12.dp))
                        ChangeButton(
                            modifier = Modifier,
                            icon = Icons.Default.Edit,
                            size = 24,
                            changeAction = {
                                isCategoriesVisible = !isCategoriesVisible
                            }
                        )
                    }
                    Text(
                        text = viewModel.place.category ?: "",
                        fontSize = 16.sp,
                        modifier = Modifier.alpha(alpha.value)
                    )
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(0.dp, height.value.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(viewModel.placeCategoriesData) { i ->
                    Card(
                        colors = CardDefaults.cardColors(if (i.isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    viewModel.placeCategoriesData =
                                        viewModel.placeCategoriesData
                                            .mapIndexed { j, item ->
                                                if (i == item) {
                                                    item.copy(isSelected = true)
                                                } else item.copy(isSelected = false)
                                            }
                                            .toMutableList()
                                    isCategoriesVisible = false
                                    viewModel.place.category = i.category
                                }
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(text = i.category, fontSize = 12.sp)
                            if (i.isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun PlaceLocalizationComponent(
    viewModel: CreateTravelViewModel, onClick: () -> Unit
) {
    val context = LocalContext.current
    val marker = context.getDrawable(R.drawable.red_marker)!!.toBitmap()
    var pointAnnotationManager: PointAnnotationManager? by remember { mutableStateOf(null) }

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
            R.string.placeOnMap,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier.size(300.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AndroidView(
                factory = {
                    MapView(it).also { mapView ->
                        pointAnnotationManager = mapView.annotations.createPointAnnotationManager()
                    }
                },
                update = { mapView ->
                    if (viewModel.place.point != null) {
                        pointAnnotationManager?.let {
                            it.deleteAll()
                            val pointAnnotationOptions = PointAnnotationOptions()
                                .withIconImage(marker)
                                .withPoint(viewModel.place.point!!)
                            it.create(pointAnnotationOptions)
                            mapView.getMapboxMap().flyTo(
                                CameraOptions.Builder().zoom(12.0).center(viewModel.place.point)
                                    .build()
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        ButtonComponent(
            R.string.editPlaceLocalization,
            modifier = Modifier,
            onClick = { onClick() })
    }
}

@Composable
fun SearchLocationBar(
    @StringRes label: Int,
    value: String,
    onValueChanged: (String) -> Unit,
    onButtonClick: () -> Unit
) {
    val travelNameIcon = @Composable {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
    val elementHeight = 56.dp
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            label = { Text(stringResource(label)) },
            value = value,
            onValueChange = onValueChanged,
            leadingIcon = travelNameIcon,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.weight(1f)
        )
        Button(modifier = Modifier.height(elementHeight),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            onClick = { onButtonClick() }) {
            Text(
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                text = stringResource(R.string.search),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(2.dp)
            )
            travelNameIcon()
        }
    }
}

@Composable
fun PlaceCardInit(place: PlaceData?, onClick: () -> Unit) {
    var isInverted by remember { mutableStateOf(false) }

    val alphaFront = if (!isInverted) 1f else 0f
    val alphaBack = if (isInverted) 1f else 0f

    Box {
        AnimatedVisibility(
            visible = !isInverted,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
            exit = fadeOut(animationSpec = tween(durationMillis = 1000))
        ) {
            PlaceCardFront(place, alphaFront) {
                isInverted = !isInverted
            }
        }

        AnimatedVisibility(
            visible = isInverted,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
            exit = fadeOut(animationSpec = tween(durationMillis = 1000))
        ) {
            PlaceCardBack(place, alphaBack, onClick = { isInverted = !isInverted })
        }

    }
}

@Composable
fun PlaceCardFront(place: PlaceData?, alpha: Float, onClick: () -> Unit) {
    var showImagePreview by remember { mutableStateOf(false) }

    if (showImagePreview) {
        Dialog(onDismissRequest = { showImagePreview = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable { showImagePreview = false }
            ) {
                place?.image?.let { imageUrl ->
                    val painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = imageUrl)
                            .build()
                    )
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .alpha(alpha)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.4f)
                        .clickable { showImagePreview = true }
                ) {
                    place?.image?.let { imageUrl ->
                        val painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = imageUrl)
                                .build()
                        )
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
                place?.description?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.6f)
                    )
                }
            }

            Divider(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
            ) {
                Column(modifier = Modifier.weight(1f)) {

                    place?.name?.let {
                        Text(
                            text = it,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    place?.category?.let {
                        PlaceCategoryCard(it)
                    }
                }
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    tint = MaterialTheme.colorScheme.inversePrimary,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onClick() }
                )
            }
        }
    }
}

@Composable
fun PlaceCardBack(place: PlaceData?, alpha: Float, onClick: () -> Unit) {
    val context = LocalContext.current
    val marker = context.getDrawable(R.drawable.red_marker)!!.toBitmap()
    var pointAnnotationManager: PointAnnotationManager? by remember { mutableStateOf(null) }
    var showMapPreview by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .alpha(alpha)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (place?.point != null) {
                    AndroidView(
                        factory = {
                            MapView(it).also { mapView ->
                                pointAnnotationManager =
                                    mapView.annotations.createPointAnnotationManager()
                            }
                        },
                        update = { mapView ->
                            if (place.point != null) {
                                pointAnnotationManager?.let {
                                    it.deleteAll()
                                    val pointAnnotationOptions = PointAnnotationOptions()
                                        .withIconImage(marker)
                                        .withPoint(place.point!!)
                                    it.create(pointAnnotationOptions)
                                    mapView.getMapboxMap().flyTo(
                                        CameraOptions.Builder().zoom(12.0).center(place.point)
                                            .build()
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Divider(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    place?.name?.let {
                        Text(
                            text = it,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    place?.category?.let {
                        PlaceCategoryCard(it)
                    }
                }
                Column(
                    modifier = Modifier.weight(0.2f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        tint = MaterialTheme.colorScheme.inversePrimary,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable { onClick() }
                    )
                    Icon(
                        imageVector = Icons.Default.Map,
                        tint = MaterialTheme.colorScheme.inversePrimary,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable { showMapPreview = true }
                    )
                }
            }
        }
    }

    if (showMapPreview) {
        Dialog(onDismissRequest = { showMapPreview = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable { showMapPreview = false }
            ) {
                if (place?.point != null) {
                    AndroidView(
                        factory = {
                            MapView(it).also { mapView ->
                                pointAnnotationManager =
                                    mapView.annotations.createPointAnnotationManager()
                            }
                        },
                        update = { mapView ->
                            if (place.point != null) {
                                pointAnnotationManager?.let {
                                    it.deleteAll()
                                    val pointAnnotationOptions = PointAnnotationOptions()
                                        .withIconImage(marker)
                                        .withPoint(place.point!!)
                                    it.create(pointAnnotationOptions)
                                    mapView.getMapboxMap().flyTo(
                                        CameraOptions.Builder().zoom(14.0).center(place.point)
                                            .build()
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PlaceCategoryCard(category: String?) {
    Box(
        modifier = Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.tertiaryContainer,
            shape = RoundedCornerShape(5.dp)
        )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .heightIn(32.dp)
                .padding(4.dp)
        ) {
            category?.let {
                Text(
                    text = it,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun PlacePhotoComponent(
    viewModel: CreateTravelViewModel
) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            viewModel.placeImage = uri
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
            R.string.placePhoto,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box {
            if (viewModel.placeImage != null) {
                val painter = rememberAsyncImagePainter(
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(data = viewModel.placeImage)
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
            R.string.changePlacePhoto,
            modifier = Modifier.width(200.dp),
            onClick = {
                launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
            })
    }
}