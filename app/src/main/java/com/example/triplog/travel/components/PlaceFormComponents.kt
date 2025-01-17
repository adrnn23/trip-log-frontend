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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.triplog.R
import com.example.triplog.profile.components.ChangeButton
import com.example.triplog.profile.components.uriToMultipart
import com.example.triplog.travel.data.PlaceData
import com.example.triplog.travel.presentation.SearchMapViewModel
import com.example.triplog.travel.presentation.travelForm.TravelFormViewModel

@Composable
fun PlaceInformationComponent(
    viewModel: TravelFormViewModel, onClick: () -> Unit
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
        Text(
            text = viewModel.place.name ?: stringResource(R.string.notSet),
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            stringResource(R.string.placeDescription),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = viewModel.place.description ?: stringResource(R.string.notSet),
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            stringResource(R.string.placeCategory),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = viewModel.place.category ?: stringResource(R.string.notSet),
            style = MaterialTheme.typography.bodyMedium
        )

        ButtonComponent(
            R.string.editBasicInformation,
            modifier = Modifier,
            enabled = true,
            onClick = { onClick() })
    }
}

@Composable
fun EditPlaceNameComponent(viewModel: TravelFormViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        TravelPlaceNameInput(
            label = R.string.placeName,
            value = viewModel.placeNameTemp ?: "",
            onValueChanged = { viewModel.placeNameTemp = it },
            imageVector = Icons.Default.Place,
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        )
    }
}

@Composable
fun EditPlaceDescriptionComponent(viewModel: TravelFormViewModel) {
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
            viewModel.placeDescriptionTemp ?: "",
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
fun EditPlaceCategoryComponent(viewModel: TravelFormViewModel) {
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
fun TravelPlaceLocalizationComponent(
    mapUrl: String?,
    onClick: () -> Unit
) {
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (mapUrl != null) {
                StaticMapView(mapUrl = mapUrl)
            } else {
                Text(
                    text = stringResource(R.string.noLocationSet),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { onClick() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 6.dp, bottom = 6.dp)
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.editLocalization),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}


@Composable
fun PlaceCardInit(
    place: PlaceData?,
    isOptionsVisible: Boolean,
    onRemove: () -> Unit,
    onEdit: () -> Unit
) {
    var isInverted by remember { mutableStateOf(false) }
    Box {
        AnimatedVisibility(
            visible = !isInverted,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
            exit = fadeOut(animationSpec = tween(durationMillis = 1000))
        ) {
            PlaceCardFront(
                place,
                onRemove = onRemove,
                onEdit = onEdit,
                isOptionsVisible = isOptionsVisible,
                onClick = { isInverted = !isInverted })
        }

        AnimatedVisibility(
            visible = isInverted,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
            exit = fadeOut(animationSpec = tween(durationMillis = 1000))
        ) {
            PlaceCardBack(
                place,
                onClick = { isInverted = !isInverted })
        }
    }
}

@Composable
fun PlaceCardFront(
    place: PlaceData?,
    onClick: () -> Unit,
    isOptionsVisible: Boolean,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
    var showImagePreview by remember { mutableStateOf(false) }
    if (showImagePreview) {
        Dialog(onDismissRequest = { showImagePreview = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 1f))
            ) {
                if (place?.imageUrl?.isNotEmpty() == true) {
                    AsyncImage(
                        model = place.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = stringResource(R.string.noPhotoAvailable),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }

    Card(
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
            ) {
                if (place?.imageUrl?.isNotEmpty() == true) {
                    AsyncImage(
                        model = place.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { showImagePreview = true },
                        )
                } else {
                    Text(
                        text = stringResource(R.string.noPhotoAvailable),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
                if (isOptionsVisible) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                    ) {
                        place?.name?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .padding(8.dp)
                            )
                        }
                        TravelPlaceActionsMenu(
                            onEditClick = { onEdit() },
                            onDeleteClick = { onRemove() }
                        )
                    }
                } else {
                    place?.name?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.BottomStart)
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                place?.category?.let { PlaceCategoryCard(it) }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Map,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = null,
                    modifier = Modifier.clickable { onClick() }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(8.dp))
            place?.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}


@Composable
fun PlaceCardBack(
    place: PlaceData?,
    onClick: () -> Unit
) {
    val mapboxAccessToken = stringResource(R.string.mapbox_access_token)
    val darkMarker = "pin-s+555555(${place?.point?.coordinates()?.get(0)},${
        place?.point?.coordinates()?.get(1)
    })"

    Card(
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
            ) {
                if (place?.point != null) {
                    PlaceCardStaticMapView(
                        longitude = place.point?.longitude(),
                        latitude = place.point?.latitude(),
                        marker = darkMarker,
                        accessToken = mapboxAccessToken
                    )
                } else {
                    Text(
                        stringResource(R.string.noLocationAvailable),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    place?.category?.let { PlaceCategoryCard(it) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Photo,
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = null,
                        modifier = Modifier.clickable { onClick() }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Divider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(8.dp))
                place?.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun PlacePhotoComponent(
    viewModel: TravelFormViewModel
) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                viewModel.placeImage = uri
                viewModel.place.imagePart = uriToMultipart(context, uri, "image")
            }
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
                .height(200.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { showPhoto.value = true }
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
                    modifier = Modifier
                        .fillMaxSize()
                )
            } else if (viewModel.place.imageUrl != null) {
                AsyncImage(
                    model = viewModel.place.imageUrl,
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
            IconButton(
                onClick = { launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 6.dp, bottom = 6.dp)
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.editLocalization),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalizationSearchBar(
    mapboxAccessToken: String,
    viewModel: SearchMapViewModel,
    modifier: Modifier
) {
    var isActive by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    SearchBar(
        query = query,
        onQueryChange = { query = it },
        onSearch = {
            viewModel.searchPlace(query, mapboxAccessToken)
            isActive = false
        },
        active = isActive,
        onActiveChange = { isActive = false },
        placeholder = { Text("Search") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { query = "" }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Query"
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text("Start typing to search...")
    }
}

@Composable
fun PlaceCategoryCard(category: String) {
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                        )
                    )
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}