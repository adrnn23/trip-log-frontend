package com.example.triplog.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.triplog.R
import com.example.triplog.main.data.SearchProfilesResult
import com.example.triplog.main.navigation.Screen
import com.example.triplog.main.presentation.MainPageViewModel

@Composable
fun SearchBar(viewModel: MainPageViewModel) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = viewModel.query,
            onValueChange = {
                viewModel.query = it
            },
            label = { Text("Search for profiles") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
            }
        )
        Spacer(modifier = Modifier.width(12.dp))
        OutlinedButton(onClick = { viewModel.getSearchProfilesResult() }) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
            )
        }
    }
}

@Composable
fun SearchResultsSection(viewModel: MainPageViewModel, navController: NavController) {
    LazyColumn {
        items(viewModel.searchedProfilesList ?: emptyList()) { profile ->
            profile?.let {
                UserItem(
                    profile,
                    onClickReject = {
                        profile.receivedRequestId?.let { viewModel.rejectFriendRequest(it) }
                    },
                    onClickAccept = {
                        profile.receivedRequestId?.let { viewModel.acceptFriendRequest(it) }
                    },
                    onClickSendInvitation = {
                        profile.id?.let { viewModel.sendFriendRequest(it) }
                    },
                    onClick = { navController.navigate("${Screen.ProfileScreen.destination}/${profile.id}") }
                )
            }
            Divider(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun UserItem(
    data: SearchProfilesResult.Data,
    onClickSendInvitation: () -> Unit,
    onClick: () -> Unit,
    onClickAccept: () -> Unit,
    onClickReject: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = data.avatar ?: R.drawable.ic_launcher_foreground
            ),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clickable { onClick() }
                .clip(RoundedCornerShape(20.dp))
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(20.dp)
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
        ) {
            data.name?.let {
                Text(
                    text = it,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onClick() })
            }
            if (data.friendStatus == 1) {
                Text(
                    text = stringResource(R.string.friend), fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            data.bio?.let {
                Text(
                    text = it,
                    fontSize = 14.sp
                )
            }
            when (data.friendStatus) {
                0 -> {
                    SendInvitationCard { onClickSendInvitation() }
                }
                3 -> {
                    InvitationActionButtons(
                        onClickAccept,
                        onClickReject
                    )
                }
                2 -> {
                    InvitationSentCard()
                }
                else -> {
                }
            }

        }
    }
}

@Composable
fun InvitationSentCard() {
    OutlinedCard(
        modifier = Modifier
            .padding(4.dp)
            .wrapContentSize(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = stringResource(R.string.invitationSent),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun SendInvitationCard(onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .padding(6.dp)
            .clickable { onClick() }
            .wrapContentSize(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = stringResource(R.string.addToFriends),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun InvitationActionButtons(
    onClickAccept: () -> Unit,
    onClickReject: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.youHaveBeenInvited), fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onClickAccept,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text("Accept", color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Button(
                onClick = onClickReject,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.errorContainer),
            ) {
                Text("Reject", color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
    }
}
