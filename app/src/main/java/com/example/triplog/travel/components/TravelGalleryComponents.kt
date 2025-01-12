package com.example.triplog.travel.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.triplog.R
import com.example.triplog.network.MapboxClient
import com.example.triplog.travel.data.PlaceData
import com.example.triplog.travel.data.TravelData
import com.example.triplog.travel.data.UserTravelsResult
import com.example.triplog.travel.presentation.travelGallery.TravelGalleryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun calculateTravelDays(startDate: String?, endDate: String?): Long {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val start = LocalDate.parse(startDate, formatter)
    val end = LocalDate.parse(endDate, formatter)
    return ChronoUnit.DAYS.between(start, end) + 1
}

@Composable
fun TravelCardSection(
    travel: TravelData,
    onCheckedChange: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isOptionsVisible: Boolean,
    seeMapClick: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(travel.favourite ?: false) }
    Column(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Card(
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1 / 1f)
            ) {
                if (travel.imageUrl?.isNotEmpty() == true) {
                    AsyncImage(
                        model = travel.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (travel.imageUrl?.isNotEmpty() == true) {
                    Text(
                        text = stringResource(R.string.noPhotoAvailable),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    travel.name.let {
                        if (it != null) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                    Text(
                        text = "${travel.startDate} - ${travel.endDate}",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Days of travel: ${
                                calculateTravelDays(
                                    travel.startDate,
                                    travel.endDate
                                )
                            }",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Visited places: ${travel.places.size}",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                }
                if (isOptionsVisible) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconToggleButton(
                            checked = isFavorite,
                            onCheckedChange = { checked ->
                                isFavorite = checked
                                onCheckedChange(checked)
                            }
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        TravelPlaceActionsMenu(
                            onEditClick = { onEditClick() },
                            onDeleteClick = { onDeleteClick() }
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(8.dp)) {
            travel.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { seeMapClick() }) {
                Text(
                    text = stringResource(R.string.viewOnMap),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Default.Map,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}


@Composable
fun PlacesListSection(places: List<PlaceData?>) {
    if (places.isNotEmpty())
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(places) { place ->
                PlaceCardInit(
                    place = place,
                    onEdit = {},
                    onRemove = {},
                    isOptionsVisible = false
                )
            }
        }
}

@Composable
fun TravelGalleryItem(travel: UserTravelsResult.TravelDataResult, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            if (travel.image?.url?.isNotEmpty() == true) {
                AsyncImage(
                    model = travel.image.url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (travel.image?.url?.isNotEmpty() == true) {
                Text(
                    text = stringResource(R.string.noPhotoAvailable),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            travel.name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "${travel.from} - ${travel.to}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            travel.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun TravelPlaceActionsMenu(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(
            onClick = { isMenuExpanded = true }
        ) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    isMenuExpanded = false
                    onEditClick()
                },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.edit))
                    }
                }
            )
            DropdownMenuItem(
                onClick = {
                    isMenuExpanded = false
                    onDeleteClick()
                },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun PlaceCardStaticMapView(
    longitude: Double?,
    latitude: Double?,
    marker: String,
    accessToken: String
) {
    val mapboxClient = remember { MapboxClient() }
    var mapUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            mapUrl = mapboxClient.getStaticMapUrl(longitude, latitude, marker, accessToken)
        } catch (_: Exception) {
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            mapUrl?.let {
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } ?: Text(stringResource(R.string.failedToLoadMap), color = Color.Red)
        }
    }
}

@Composable
fun StaticMapView(
    mapUrl: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f),
        contentAlignment = Alignment.Center
    ) {
        mapUrl?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } ?: Text(stringResource(R.string.failedToLoadMap), color = Color.Red)
    }
}

@Composable
fun TravelsPage(viewModel: TravelGalleryViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(bottom = 64.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.finishedTravelsList ?: emptyList()) { travel ->
                TravelGalleryItem(travel, onClick = {
                    viewModel.travelOverview.id = travel.id
                    viewModel.showTravel()
                })
            }
        }

        if (viewModel.finishedTravelsList?.isNotEmpty() == true) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { viewModel.loadPreviousPageOfFinishedTravels() },
                    enabled = viewModel.currentPageOfFinishedTravels > 1
                ) {
                    Text("Previous")
                }

                Text(
                    text = "Page ${viewModel.currentPageOfFinishedTravels} of ${viewModel.totalPagesOfFinishedTravels}",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Button(
                    onClick = { viewModel.loadNextPageOfFinishedTravels() },
                    enabled = viewModel.currentPageOfFinishedTravels < viewModel.totalPagesOfFinishedTravels
                ) {
                    Text("Next")
                }
            }
        }
    }
}

@Composable
fun FavoritesPage(viewModel: TravelGalleryViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(bottom = 64.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.favouriteTravelsList ?: emptyList()) { travel ->
                TravelGalleryItem(travel, onClick = {
                    viewModel.travelOverview.id = travel.id
                    viewModel.showTravel()
                })
            }
        }

        if (viewModel.favouriteTravelsList?.isNotEmpty() == true) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { viewModel.loadPreviousPageOfFavouriteTravels() },
                    enabled = viewModel.currentPageOfFavouriteTravels > 1
                ) {
                    Text("Previous")
                }

                Text(
                    text = "Page ${viewModel.currentPageOfFavouriteTravels} of ${viewModel.totalPagesOfFavouriteTravels}",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Button(
                    onClick = { viewModel.loadNextPageOfFavouriteTravels() },
                    enabled = viewModel.currentPageOfFavouriteTravels < viewModel.totalPagesOfFavouriteTravels
                ) {
                    Text("Next")
                }
            }
        }
    }
}

@Composable
fun PlannedPage(viewModel: TravelGalleryViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(bottom = 64.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.plannedTravelsList ?: emptyList()) { travel ->
                TravelGalleryItem(travel, onClick = {
                    viewModel.travelOverview.id = travel.id
                    viewModel.showTravel()
                })
            }
        }

        if (viewModel.plannedTravelsList?.isNotEmpty() == true) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { viewModel.loadPreviousPageOfPlannedTravels() },
                    enabled = viewModel.currentPageOfPlannedTravels > 1
                ) {
                    Text("Previous")
                }

                Text(
                    text = "Page ${viewModel.currentPageOfPlannedTravels} of ${viewModel.totalPagesOfPlannedTravels}",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Button(
                    onClick = { viewModel.loadNextPageOfPlannedTravels() },
                    enabled = viewModel.currentPageOfPlannedTravels < viewModel.totalPagesOfPlannedTravels
                ) {
                    Text("Next")
                }
            }
        }
    }
}