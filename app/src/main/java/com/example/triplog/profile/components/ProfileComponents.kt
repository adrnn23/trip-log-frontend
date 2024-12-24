package com.example.triplog.profile.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.main.navigation.Screen
import com.example.triplog.profile.data.LinkData
import com.example.triplog.profile.data.TravelNavigationElementData
import com.example.triplog.profile.data.profile.UserProfileResult
import com.example.triplog.profile.presentation.ProfileViewModel
import com.example.triplog.profile.presentation.UserProfileSection

@Composable
fun TravelNavigationElement(icon: ImageVector, @StringRes label: Int, onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                stringResource(id = label),
                fontSize = 14.sp
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier
                .size(28.dp)
        )
    }
    Divider(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        thickness = 1.dp
    )
}

@Composable
fun ProfileUsername(username: String?) {
    Text(
        text = username.toString(),
        fontSize = 18.sp
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
            .background(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(12.dp))
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
fun TravelNavigation(navController: NavController) {
    val tripsNavigationElements =
        listOf(
            TravelNavigationElementData(
                Icons.Filled.TravelExplore,
                R.string.travels
            ) { navController.navigate(Screen.TravelGalleryScreen.destination) },
            TravelNavigationElementData(Icons.Filled.Favorite, R.string.favorite) { },
            TravelNavigationElementData(Icons.Filled.EditCalendar, R.string.planned) { },
            TravelNavigationElementData(Icons.AutoMirrored.Filled.Notes, R.string.travelAdvices) { }
        )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondaryContainer,
            thickness = 1.dp
        )
        tripsNavigationElements.forEach { item ->
            TravelNavigationElement(item.icon, item.label) { item.navigate() }
        }
    }
}

@Composable
fun TravelPreferencesComponent(viewModel: ProfileViewModel, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        TitleComponent(
            R.string.travelPreferences,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(4.dp))
            if (viewModel.userProfile.travelPreferences != null) {
                if (viewModel.userProfile.travelPreferences!!.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(42.dp, 108.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(viewModel.userProfile.travelPreferences!!) { item ->
                            if (item != null)
                                TravelPreferenceCard(item)
                        }
                    }
                } else {
                    Text(text = stringResource(R.string.addTravelPreferences), fontSize = 12.sp)
                }
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
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clip(RoundedCornerShape(20.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                ProfileUsername(viewModel.userProfile.username)
                StatisticCard(
                    stats = listOf(
                        viewModel.userProfile.tripsCount to "Travels",
                        viewModel.userProfile.plannedCount to "Planned",
                        viewModel.userProfile.favoriteCount to "Favorites"
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
                fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
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
            Text(text = link.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
            TitleComponent(
                R.string.links,
                fontSize = 16.sp,
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
            Text(text = stringResource(R.string.addLinks), fontSize = 12.sp)
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
            modifier = Modifier.padding(8.dp).fillMaxWidth()
        ) {
            stats.forEach { (number, label) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = number.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = label,
                        fontSize = 10.sp
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
            TitleComponent(
                R.string.aboutMe,
                fontSize = 16.sp,
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
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { extended = !extended }
                )
            }
        }
        if (bio != null) {
            if (bio.length > 192) {
                if (!extended) {
                    Column {
                        Text(text = bio.substring(0, 191) + "...", fontSize = 12.sp)
                    }
                } else {
                    Column {
                        Text(text = bio, fontSize = 12.sp)
                    }
                }
            } else {
                Column {
                    Text(text = bio, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun TravelPreferenceCard(travelPreference: UserProfileResult.TravelPreference) {
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(5.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize(1f)
                .heightIn(32.dp)
        ) {
            travelPreference.name?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DividerComponent() {
    Spacer(modifier = Modifier.height(8.dp))
    Divider(
        color = MaterialTheme.colorScheme.secondaryContainer,
        thickness = 1.dp,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun DeleteFriendButton(onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onClick() }) {
        Icon(
            imageVector = Icons.Default.PersonRemove,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier
                .size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.deleteFriend),
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontSize = 12.sp
        )
    }
}
