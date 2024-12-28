package com.example.triplog.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.triplog.R
import com.example.triplog.main.data.SearchProfilesResult
import com.example.triplog.main.navigation.Screen
import com.example.triplog.main.presentation.MainPageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPageSearchBar(viewModel: MainPageViewModel) {
    var isActive by remember { mutableStateOf(false) }

    SearchBar(
        query = viewModel.query,
        onQueryChange = { viewModel.query = it },
        onSearch = {
            viewModel.getSearchProfilesResult(viewModel.currentPage)
            isActive = false
        },
        active = isActive,
        onActiveChange = { isActive = false },
        placeholder = { Text("Search", style = TextStyle(fontSize = 16.sp)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        trailingIcon = {
            if (viewModel.query.isNotEmpty()) {
                IconButton(onClick = { viewModel.query = "" }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Query"
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text("Start typing to search...", style = TextStyle(fontSize = 16.sp))
    }
}

@Composable
fun SearchResultsSection(viewModel: MainPageViewModel, navController: NavController) {
    LazyColumn {
        items(viewModel.searchedProfilesList ?: emptyList()) { profile ->
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
                onClick = {
                    navController.navigate("${Screen.ProfileScreen.destination}/${profile.id}")
                }
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
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
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
                    .size(96.dp)
                    .clickable { onClick() }
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(24.dp)
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
                Spacer(modifier = Modifier.height(4.dp))
                data.friendStatus?.let {
                    FriendStatusAction(
                        it,
                        onClickReject = onClickReject,
                        onClickSendInvitation = onClickSendInvitation,
                        onClickAccept = onClickAccept
                    )
                }

            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.padding(4.dp)) {
            data.bio?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun FriendStatusAction(
    friendStatus: Int,
    onClickSendInvitation: () -> Unit,
    onClickAccept: () -> Unit,
    onClickReject: () -> Unit
) {
    when (friendStatus) {
        0 -> SendInvitationCard { onClickSendInvitation() }
        3 -> InvitationActionButtons(onClickAccept, onClickReject)
        2 -> InvitationSentCard()
        1 -> {
            Text(
                text = stringResource(R.string.friend), fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        else -> {}
    }
}


@Composable
fun InvitationSentCard() {
    OutlinedCard(
        modifier = Modifier
            .padding(2.dp)
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
    Box(contentAlignment = Alignment.Center) {
        OutlinedButton(
            onClick = { onClick() },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.padding(2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.addToFriends),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
fun InvitationActionButtons(
    onClickAccept: () -> Unit,
    onClickReject: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = { onClickAccept() },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier.padding(2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.acceptRequest),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        OutlinedButton(
            onClick = { onClickReject() },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            modifier = Modifier.padding(2.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }

    }
}
