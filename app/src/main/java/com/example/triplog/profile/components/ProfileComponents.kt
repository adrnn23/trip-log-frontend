package com.example.triplog.profile.components

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

@Composable
fun TravelNavigationElement(icon: ImageVector, @StringRes label: Int) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
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
            modifier = Modifier.size(28.dp)
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
fun FriendsList(onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }) {
        Text(
            text = stringResource(R.string.friends),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            Icons.Default.People,
            contentDescription = stringResource(R.string.friends),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun TravelNavigation() {
    val tripsNavigationElements =
        listOf(
            TravelNavigationElementData(Icons.Filled.TravelExplore, R.string.travels),
            TravelNavigationElementData(Icons.Filled.Favorite, R.string.favorite),
            TravelNavigationElementData(Icons.Filled.EditCalendar, R.string.planned),
            TravelNavigationElementData(Icons.AutoMirrored.Filled.Notes, R.string.travelAdvices)
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
        tripsNavigationElements.forEach() { item ->
            TravelNavigationElement(item.icon, item.label)
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
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(size = 100.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clip(RoundedCornerShape(20.dp))
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                ProfileUsername(viewModel.userProfile.username)
                Spacer(modifier = Modifier.height(4.dp))
                if (viewModel.isOwnProfile) {
                    FriendsList { viewModel.getFriendsListResult() }
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            if (viewModel.isOwnProfile) {
                EditProfileButton(navController, viewModel)
            }
        }
        DividerComponent()
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            StatisticCard(tripsNumber = viewModel.userProfile.tripsCount, label = R.string.travels)
            Spacer(modifier = Modifier.width(8.dp))
            StatisticCard(
                tripsNumber = viewModel.userProfile.plannedCount,
                label = R.string.planned
            )
            Spacer(modifier = Modifier.width(8.dp))
            StatisticCard(
                tripsNumber = viewModel.userProfile.favoriteCount,
                label = R.string.favorite
            )
        }
        DividerComponent()
        TravelPreferencesComponent(viewModel, Modifier)
    }
}

@Composable
fun EditProfileButton(navController: NavController, viewModel: ProfileViewModel) {
    Button(
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiaryContainer),
        onClick = {
            navController.navigate(Screen.EditProfileScreen.destination)
        }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.editProfile),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier
                    .size(16.dp)
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
                fontSize = 12.sp,
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
fun StatisticCard(tripsNumber: Int?, @StringRes label: Int) {
    Card {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(2.dp)
                .width(100.dp)
                .height(50.dp)
                .fillMaxSize(1f)
        ) {
            Text(text = tripsNumber.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = stringResource(id = label), fontSize = 12.sp)
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