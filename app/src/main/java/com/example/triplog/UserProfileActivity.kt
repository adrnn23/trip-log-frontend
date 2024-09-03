package com.example.triplog

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplog.ui.theme.TripLogTheme

var username: String = "Username123"
var bio: String =
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."

@Composable
fun UserProfile(innerpadding: PaddingValues) {
    val count1 by remember {
        mutableIntStateOf(511)
    }
    val count2 by remember {
        mutableIntStateOf(2)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(innerpadding)
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        UserMainInfo(count1 = count1, count2 = count2)

        FavoriteCategories()

        Spacer(modifier = Modifier.height(10.dp))

        TripsNavigation()
    }
}

@Composable
fun UserProfileScreen() {
    Scaffold(
        topBar = {
            TopApplicationBar(username)
        },
        bottomBar = {
            BottomApplicationBar()
        }
    ) { innerpadding ->
        UserProfile(innerpadding)
    }
}

data class TripsNavigationElementData(
    var icon: ImageVector,
    @StringRes val label: Int,
)

@Composable
fun TripsNavigationElement(icon: ImageVector, @StringRes label: Int) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
            Text(
                stringResource(id = label),
                fontSize = 14.sp
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun TripsNavigation() {
    val tripsNavigationElements =
        listOf(
            TripsNavigationElementData(Icons.Filled.TravelExplore, R.string.trips),
            TripsNavigationElementData(Icons.Filled.EditCalendar, R.string.plannedTrips),
            TripsNavigationElementData(Icons.AutoMirrored.Filled.Notes, R.string.tripsAdvices)
        )

    LazyColumn {
        items(tripsNavigationElements) { item ->
            TripsNavigationElement(item.icon, item.label)
            Divider()
        }
    }
}


@Composable
fun UserMainInfo(count1: Int, count2: Int) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {

                Image(
                    painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .size(size = 160.dp)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Username123",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                )

            }

            Column {

                TripsCard(tripsNumber = count1, label = R.string.numberOfTrips)
                Spacer(modifier = Modifier.height(32.dp))
                TripsCard(tripsNumber = count2, label = R.string.numberOfPlannedTrips)
            }
        }
        Bio(bio = bio)
    }
}


@Composable
fun TripsCard(tripsNumber: Int, @StringRes label: Int) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(70.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize(1f)
        ) {
            Text(text = tripsNumber.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = stringResource(id = label), fontSize = 16.sp)
        }
    }
}

@Composable
fun FavoriteCategories() {
    val categories by remember {
        mutableStateOf(mutableListOf("Mountains", "Lake", "Bike trips"))
    }
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(10.dp)
    ) {
        Row {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
            Text(
                stringResource(id = R.string.favoriteCategories),
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3), horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categories) { category ->
                TripCategoryCard(category)
            }
        }
    }


}

@Composable
fun Bio(bio: String) {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.aboutMe),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(4.dp)
        )
        Text(
            text = bio,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(4.dp)
        )
    }

}

@Composable
fun TripCategoryCard(category: String) {
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiaryContainer),
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        modifier = Modifier.size(50.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize(1f)
        ) {
            Text(
                text = category,
                fontSize = 14.sp,
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {
    TripLogTheme {
        UserProfileScreen()
    }
}