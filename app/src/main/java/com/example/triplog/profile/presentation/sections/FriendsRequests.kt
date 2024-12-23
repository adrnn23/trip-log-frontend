package com.example.triplog.profile.presentation.sections

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.triplog.R
import com.example.triplog.main.components.InvitationActionButtons
import com.example.triplog.main.navigation.Screen
import com.example.triplog.profile.data.profile.GetFriendsRequestsResult
import com.example.triplog.profile.presentation.ProfileViewModel

@Composable
fun FriendsRequestsSection(
    innerpadding: PaddingValues,
    viewModel: ProfileViewModel,
    navController: NavController
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = innerpadding,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            viewModel.friendsRequests.forEach { item ->
                FriendsRequestItem(
                    item,
                    onClickReject = {
                        item?.id?.let {
                            viewModel.rejectFriendRequest(it)
                            viewModel.refreshFriendsRequests()
                        }
                    },
                    onClickAccept = {
                        item?.id?.let {
                            viewModel.refreshFriendsRequests()
                            viewModel.acceptFriendRequest(it)
                        }
                    },
                    onClick = { navController.navigate("${Screen.ProfileScreen.destination}/${item?.user?.id}") })
                Divider(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun FriendsRequestItem(
    data: GetFriendsRequestsResult.Data?,
    onClick: () -> Unit,
    onClickAccept: () -> Unit,
    onClickReject: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = data?.user?.avatar ?: R.drawable.ic_launcher_foreground
            ),
            contentDescription = null,
            modifier = Modifier
                .clickable { onClick() }
                .size(96.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(24.dp)
                )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            data?.user?.name?.let {
                Text(
                    text = it,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onClick() })
            }
            Spacer(modifier = Modifier.height(4.dp))
            data?.user?.bio?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
            }
            data?.createdAtDiff?.let {
                Text(
                    text = "Invitation sent: $it",
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            InvitationActionButtons(onClickAccept, onClickReject)
        }
    }
}