package com.example.triplog.main.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.triplog.R
import com.example.triplog.main.LoadingState
import com.example.triplog.main.data.TimelineResult
import com.example.triplog.main.navigation.Screen
import com.example.triplog.main.presentation.MainPageViewModel
import com.example.triplog.travel.data.TravelData

@Composable
fun TravelPost(
    travel: TimelineResult.TimelineTravel,
    onTravelClick: () -> Unit,
    onUserClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Divider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        UserInfoSection(
            username = travel.user?.name,
            timeAgo = travel.travel?.created,
            travelName = travel.travel?.name,
            avatar = travel.user?.avatar,
            onUserClick = { onUserClick() }
        )

        Column(modifier = Modifier.clickable { onTravelClick() }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1 / 1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (travel.travel?.image?.url?.isNotEmpty() == true) {
                        AsyncImage(
                            model = travel.travel.image.url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (travel.travel?.image?.url?.isNotEmpty() == true) {
                        Text(
                            text = stringResource(R.string.noPhotoAvailable),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

            }

            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    travel.travel?.name?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "${travel.travel?.from} - ${travel.travel?.to}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                }
                travel.travel?.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun TravelPosts(navController: NavController, viewModel: MainPageViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        if (viewModel.currentPageOfTimelineTravels > 1) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.loadTimelineTravels(viewModel.currentPageOfTimelineTravels - 1)
                        }
                    ) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = "Previous Travels")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Load previous Travels", fontSize = 16.sp)
                    }
                }
            }
        }

        itemsIndexed(viewModel.timelineTravels) { index, travelPost ->
            TravelPost(
                travelPost,
                onTravelClick = {
                    val travelOverview = TravelData(id = travelPost.travel?.id)
                    viewModel.travelOverview = travelOverview
                    viewModel.showTravel()
                },
                onUserClick = {
                    navController.navigate("${Screen.ProfileScreen.destination}/${travelPost.user?.id}")
                }
            )
        }

        if (viewModel.currentPageOfTimelineTravels < viewModel.totalPagesOfTimelineTravels) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.loadTimelineTravels(viewModel.currentPageOfTimelineTravels + 1)
                        }
                    ) {
                        Text("Load next Travels", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowDownward, contentDescription = "Next Travels")
                    }
                }
            }
        }

        if (viewModel.loadingState == LoadingState.Loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}


@Composable
fun UserInfoSection(
    username: String?,
    timeAgo: String?,
    travelName: String?,
    avatar: String?,
    onUserClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (avatar != null) {
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onUserClick() }
            ) {
                AsyncImage(
                    model = avatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .clickable { onUserClick() }
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = username?.first().toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            username?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.clickable { onUserClick() }
                )
            }

            travelName?.let {
                Text(
                    text = "$timeAgo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}