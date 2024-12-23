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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.triplog.R
import com.example.triplog.main.navigation.Screen
import com.example.triplog.profile.components.DeleteFriendButton
import com.example.triplog.profile.data.profile.GetFriendsListResult
import com.example.triplog.profile.presentation.ProfileViewModel

@Composable
fun FriendsListSection(
    innerpadding: PaddingValues,
    viewModel: ProfileViewModel,
    navController: NavController
) {
    var friendToDelete = remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = innerpadding,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            viewModel.friendsList.forEach { item ->
                FriendItem(
                    item,
                    onClickDelete = {
                        viewModel.isDeleteFriendDialogVisible = true
                        item?.id?.let { friendToDelete.value = it }
                    },
                    onClick = { navController.navigate("${Screen.ProfileScreen.destination}/${item?.id}") })
                Divider(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }

    if (viewModel.isDeleteFriendDialogVisible) {
        AlertDialog(
            title = {
                val friend = viewModel.friendsList.find { it?.id == friendToDelete.value }
                Text(
                    text = "Delete ${friend?.name ?: "this user"} from friends?"
                )
            },
            icon = {
                Icon(
                    Icons.Filled.PersonRemove,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            },
            containerColor = MaterialTheme.colorScheme.errorContainer,
            onDismissRequest = {
                friendToDelete.value = null
                viewModel.isDeleteFriendDialogVisible = false
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        friendToDelete.value = null
                        viewModel.isDeleteFriendDialogVisible = false
                    },
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.isDeleteFriendDialogVisible = false
                        friendToDelete.value?.let { viewModel.deleteFriend(it) }
                        viewModel.refreshFriendsList()
                        friendToDelete.value = null
                    },
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.deleteFriend),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }
}


@Composable
fun FriendItem(data: GetFriendsListResult.Data?, onClick: () -> Unit, onClickDelete: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = data?.avatar ?: R.drawable.ic_launcher_foreground
            ),
            contentDescription = null,
            modifier = Modifier
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
            horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                data?.name?.let {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onClick() })
                }
                Spacer(modifier = Modifier.width(16.dp))
                DeleteFriendButton(onClick = onClickDelete)
            }
            Spacer(modifier = Modifier.height(4.dp))
            data?.bio?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}