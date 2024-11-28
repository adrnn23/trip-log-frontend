package com.example.triplog.travel.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.drawable.toBitmap
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.triplog.R
import com.example.triplog.profile.components.ChangeButton
import com.example.triplog.travel.presentation.create.CreateTravelViewModel
import com.example.triplog.travel.presentation.create.PlaceData
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
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            stringResource(R.string.basicInformation),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 1.dp)
        Text(
            stringResource(R.string.placeName),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = viewModel.place.name ?: "-----", style = MaterialTheme.typography.bodyMedium)

        Text(
            stringResource(R.string.placeDescription),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = viewModel.place.description ?: "-----",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            stringResource(R.string.placeCategory),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = viewModel.place.category ?: "-----",
            style = MaterialTheme.typography.bodyMedium
        )

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
    var enabled by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        TravelPlaceDescriptionInput(
            R.string.travelDescription,
            imageVector = Icons.Default.Description,
            viewModel.placeDescriptionTemp,
            enabled = enabled,
            characterLimit = 256,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(6.dp),
            onValueChanged = { viewModel.placeDescriptionTemp = it },
        )
        ChangeButton(
            modifier = Modifier,
            icon = Icons.Default.Edit,
            changeAction = {
                enabled = !enabled
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
                items(viewModel.placeCategoriesList) { i ->
                    if (i != null) {
                        Card(
                            colors = CardDefaults.cardColors(if (i.isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.placeCategoriesList =
                                            viewModel.placeCategoriesList
                                                .mapIndexed { j, item ->
                                                    if (i == item) {
                                                        item.copy(isSelected = true)
                                                    } else item?.copy(isSelected = false)
                                                }
                                                .toMutableList()
                                        isCategoriesVisible = false
                                        viewModel.place.category = i.category
                                    }
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                i.category?.let { Text(text = it, fontSize = 12.sp) }
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
}

@Composable
fun PlaceLocalizationComponent(
    viewModel: CreateTravelViewModel, onClick: () -> Unit
) {
    val context = LocalContext.current
    val marker = context.getDrawable(R.drawable.red_marker)!!.toBitmap()
    var pointAnnotationManager: PointAnnotationManager? by remember { mutableStateOf(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.placeOnMap),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 1.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
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
        ButtonComponent(
            R.string.editPlaceLocalization,
            modifier = Modifier,
            onClick = { onClick() })
    }
}

@Composable
fun PlaceCardInit(place: PlaceData?, onClick: () -> Unit) {
    var isInverted by remember { mutableStateOf(false) }

    Box {
        AnimatedVisibility(
            visible = !isInverted,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
            exit = fadeOut(animationSpec = tween(durationMillis = 1000))
        ) {
            PlaceCardFront(place) {
                isInverted = !isInverted
            }
        }

        AnimatedVisibility(
            visible = isInverted,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
            exit = fadeOut(animationSpec = tween(durationMillis = 1000))
        ) {
            PlaceCardBack(place, onClick = { isInverted = !isInverted })
        }
    }
}

@Composable
fun PlaceCardFront(place: PlaceData?, onClick: () -> Unit) {
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
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
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
                            .clickable { showImagePreview = true },
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                startY = 100f
                            )
                        )
                )
                place?.name?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(8.dp)) {
            place?.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                place?.category?.let {
                    PlaceCategoryCard(it)
                }

                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = null,
                    modifier = Modifier.clickable { onClick() }
                )
            }
        }
    }
}


@Composable
fun PlaceCardBack(place: PlaceData?, onClick: () -> Unit) {
    val context = LocalContext.current
    val marker = context.getDrawable(R.drawable.red_marker)!!.toBitmap()
    var pointAnnotationManager: PointAnnotationManager? by remember { mutableStateOf(null) }
    var showMapPreview by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
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
            Column(modifier = Modifier.padding(8.dp)) {
                place?.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    place?.category?.let {
                        PlaceCategoryCard(it)
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = null,
                        modifier = Modifier.clickable { onClick() }
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

    val showPhoto = remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.placePhoto),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 1.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPhoto.value = true }
                .height(200.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (viewModel.placeImage != null) {
                val painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = viewModel.placeImage)
                        .build()
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = stringResource(R.string.noPhotoSelected),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        ButtonComponent(
            R.string.selectPlacePhoto,
            modifier = Modifier.fillMaxWidth(0.7f),
            onClick = {
                launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        )
    }
    if (showPhoto.value) {
        AlertDialog(
            onDismissRequest = { showPhoto.value = false },
            title = {
                Text(text = stringResource(R.string.placePhoto))
            },
            text = {
                if (viewModel.placeImage != null) {
                    val painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = viewModel.placeImage)
                            .build()
                    )
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(text = stringResource(R.string.noPhotoSelected))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showPhoto.value = false }
                ) {
                    Text(text = stringResource(R.string.close))
                }
            }
        )
    }
}

@Composable
fun LocalizationSearchBar(mapboxAccessToken: String, viewModel: CreateTravelViewModel) {
    var query by remember { mutableStateOf("") }
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(horizontal = 8.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
            },
            label = { Text("Search") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    modifier = Modifier.size(20.dp)
                )
            },
            shape = RoundedCornerShape(16.dp),
            textStyle = TextStyle(fontSize = 16.sp),
            modifier = Modifier
                .weight(1f)
                .height(60.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedButton(
            onClick = { viewModel.searchPlace(query, mapboxAccessToken) },
            modifier = Modifier.height(42.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .size(20.dp)
            )
        }
    }
}