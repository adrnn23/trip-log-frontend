package com.example.triplog.profile.components

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.triplog.R
import com.example.triplog.main.navigation.Screen
import com.example.triplog.profile.data.LinkData
import com.example.triplog.profile.data.profile.UserProfileResult
import com.example.triplog.profile.presentation.ProfileViewModel
import com.example.triplog.profile.presentation.UserProfileSection

@Composable
fun ProfileUsername(username: String?) {
    Text(
        text = username.toString(),
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun FriendsList(friendCount: Int, onClick: () -> Unit, modifier: Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .height(48.dp)
            .clickable { onClick() }
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.People,
            contentDescription = stringResource(R.string.friends),
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.friends),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = friendCount.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun TravelNavigation(id: Int?, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
            .clickable {
                navController.navigate("${Screen.TravelGalleryScreen.destination}/${id}")
            }
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.TravelExplore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(id = R.string.travels),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = stringResource(id = R.string.exploreTravels),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

@Composable
fun TravelPreferencesComponent(viewModel: ProfileViewModel, modifier: Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            stringResource(R.string.travelPreferences),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(4.dp))

            val preferences = viewModel.userProfile.travelPreferences
            if (!preferences.isNullOrEmpty()) {
                val visibleItems = if (expanded) preferences else preferences.take(3)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .heightIn(0.dp, if (expanded) 300.dp else 100.dp)
                        .fillMaxWidth()
                        .animateContentSize(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(visibleItems) { item ->
                        if (item != null) {
                            TravelPreferenceCard(item)
                        }
                    }
                }

                if (preferences.size > 3) {
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = if (expanded) stringResource(R.string.showLess) else stringResource(
                                R.string.showMore
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.profileWithoutPreferences),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}


@Composable
fun ProfileMainInfoComponent(
    viewModel: ProfileViewModel,
    navController: NavController
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.userProfile.avatar?.isNotEmpty() == true) {
                    AsyncImage(
                        model = viewModel.userProfile.avatar,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (viewModel.userProfile.username?.isNotEmpty() == true) {
                    Text(
                        text = viewModel.userProfile.username?.first().toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                ProfileUsername(viewModel.userProfile.username)
                StatisticCard(
                    stats = listOf(
                        viewModel.userProfile.tripsCount to stringResource(R.string.travels),
                        viewModel.userProfile.plannedCount to stringResource(R.string.planned),
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (viewModel.isOwnProfile) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                FriendsList(
                    friendCount = viewModel.friendsList.size,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.profileSection = UserProfileSection.FriendsList
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))

                EditProfileButton(navController, modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TravelPreferencesComponent(viewModel, Modifier)
    }
}


@Composable
fun EditProfileButton(navController: NavController, modifier: Modifier) {
    OutlinedButton(
        onClick = { navController.navigate(Screen.EditProfileScreen.destination) },
        modifier = modifier
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.editProfile),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.editProfile),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun Link(link: LinkData) {
    val linkString = buildAnnotatedString {
        append(link.link)
        addStyle(
            SpanStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
                textDecoration = TextDecoration.Underline
            ), 0, link.link.length
        )
        addStringAnnotation(tag = "URL", annotation = link.link, 0, link.link.length)
    }

    val uriHandler = LocalUriHandler.current

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = link.imageVector,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = link.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            ClickableText(
                text = linkString,
                onClick = {
                    linkString
                        .getStringAnnotations("URL", it, it)
                        .firstOrNull()?.let { stringAnnotation ->
                            uriHandler.openUri(stringAnnotation.item)
                        }
                }
            )
        }
    }
}

@Composable
fun LinksComponent(list: MutableList<LinkData?>, modifier: Modifier) {
    var extended by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.links),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.width(4.dp))
            if (list.size > 1) {
                Icon(
                    imageVector = if (extended) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { extended = !extended }
                )
            }
        }
        if (list.isNotEmpty()) {
            if (extended) {
                list.forEach { link ->
                    if (link != null) {
                        if (link.link.isNotEmpty()) {
                            Link(link = link)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            } else {
                if (list[0] != null) {
                    Link(link = list[0]!!)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        } else {
            Text(
                text = stringResource(R.string.noLinksAddedYet),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun TitleComponent(
    @StringRes title: Int,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    modifier: Modifier
) {
    Text(
        text = stringResource(id = title),
        fontSize = fontSize,
        fontWeight = fontWeight,
        modifier = modifier
    )
}

@Composable
fun StatisticCard(stats: List<Pair<Int?, String>>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            stats.forEach { (number, label) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = number.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}


@Composable
fun AboutMeComponent(bio: String?) {
    var extended by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.aboutMe),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.width(4.dp))
            if (bio != null && bio.length > 192) {
                Icon(
                    imageVector = if (extended) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = "Extend icon",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { extended = !extended }
                )
            }
        }
        if (bio != null && bio.isBlank().not()) {
            if (bio.length > 192) {
                if (!extended) {
                    Column {
                        Text(
                            text = bio.substring(0, 191) + "...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    Column {
                        Text(text = bio, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                Column {
                    Text(text = bio, style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            Text(
                text = stringResource(R.string.profileWithoutBiography),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun TravelPreferenceCard(travelPreference: UserProfileResult.TravelPreference) {
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                        )
                    )
                )
                .padding(8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                travelPreference.name?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimary),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}