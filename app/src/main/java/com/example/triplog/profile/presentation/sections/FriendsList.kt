package com.example.triplog.profile.presentation.sections

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.triplog.R
import com.example.triplog.main.navigation.Screen
import com.example.triplog.profile.data.profile.GetFriendsListResult
import com.example.triplog.profile.presentation.ProfileViewModel

@Composable
fun FriendsListSection(
    innerpadding: PaddingValues,
    viewModel: ProfileViewModel,
    navController: NavController
) {
    val friendToDelete = remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current

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
                Spacer(modifier = Modifier.padding(vertical = 6.dp))
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
                        friendToDelete.value?.let { viewModel.deleteFriend(it, context) }
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

data class FriendOptions(
    val text: String,
    val icon: ImageVector,
    val function: () -> Unit
)

@Composable
fun FriendItem(data: GetFriendsListResult.Data?, onClick: () -> Unit, onClickDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(
        FriendOptions(
            stringResource(R.string.deleteFriend),
            Icons.Default.PersonRemove,
            function = { onClickDelete() })
    )

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
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                data?.name?.let {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onClick() })
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        modifier = Modifier.clickable { expanded = true },
                        contentDescription = null
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.text) },
                                onClick = {
                                    expanded = false
                                    option.function()
                                },
                                leadingIcon = { Icon(option.icon, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
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