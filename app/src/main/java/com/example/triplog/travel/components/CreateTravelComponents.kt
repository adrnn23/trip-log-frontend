package com.example.triplog.travel.components

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplog.R
import com.example.triplog.profile.components.TitleComponent

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
fun TravelBasicInformationComponent(
    travelName: String,
    travelDescription: String,
    startDateTravel: String,
    endDateTravel: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(4.dp)
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
            Text(text = travelName, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))

            TitleComponent(
                R.string.travelDescription,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )
            Text(text = travelDescription, style = MaterialTheme.typography.bodyMedium)
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
                    text = startDateTravel,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.width(4.dp))
                Divider(modifier = Modifier.width(20.dp))
                Spacer(Modifier.width(4.dp))

                Text(
                    text = endDateTravel,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            ButtonComponent(R.string.editBasicInformation, modifier = Modifier, onClick = {})
        }
    }
}

@Composable
fun TravelPhotoComponent(
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(4.dp)
        ) {
            TitleComponent(
                R.string.travelPhoto,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box {
                Image(
                    painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .size(size = 200.dp)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            ButtonComponent(
                R.string.changeTravelPhoto,
                modifier = Modifier.width(200.dp),
                onClick = {})
        }
    }
}

@Composable
fun TravelPlacesComponent(
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(4.dp)
        ) {
            TitleComponent(
                R.string.travelPlaces,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(8.dp))
            Row {
                ButtonComponent(
                    R.string.addNewPlace,
                    modifier = Modifier.width(200.dp),
                    onClick = {})
                Spacer(modifier = Modifier.width(10.dp))
                ButtonComponent(
                    R.string.editPlaces,
                    modifier = Modifier.width(200.dp),
                    onClick = {})
            }

        }
    }
}

@Preview
@Composable
fun Preview() {
    TravelPlacesComponent()
}