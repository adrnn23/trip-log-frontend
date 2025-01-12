package com.example.triplog.main.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.triplog.R
import com.example.triplog.main.data.SearchFilters
import com.example.triplog.main.data.SearchProfilesResult
import com.example.triplog.main.data.TimelineResult
import com.example.triplog.main.data.TimelineResult.TimelineTravel
import com.example.triplog.main.navigation.Screen
import com.example.triplog.main.presentation.MainPageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPageSearchBar(viewModel: MainPageViewModel) {
    var isActive by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    SearchBar(
        query = viewModel.query,
        onQueryChange = { viewModel.query = it },
        onSearch = {
            if (viewModel.selectedFilters.searchType == "Users") {
                viewModel.searchProfiles(viewModel.searchedProfilesCurrentPage)
            }
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showBottomSheet = true }) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filters"
                    )
                }
                if (viewModel.query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.query = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear query"
                        )
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = "Search with filters...",
            style = TextStyle(fontSize = 16.sp)
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = false
            )
        ) {
            SearchFiltersContent(
                filters = viewModel.selectedFilters,
                onFiltersChanged = { updatedFilters -> viewModel.selectedFilters = updatedFilters },
                onClose = { showBottomSheet = false },
                searchedTravelsClear = {
                    viewModel.searchedTravels = emptyList<TimelineTravel>()
                },
                searchedProfilesClear = {
                    viewModel.searchedProfiles = emptyList<SearchProfilesResult.Data>()
                },
                onApplySearchTravels = {
                    if (viewModel.selectedFilters.searchType == "Travels") {
                        viewModel.searchTravels(viewModel.searchedTravelsCurrentPage)
                    }
                }
            )
        }
    }
}


@Composable
fun SearchResultsSection(viewModel: MainPageViewModel, navController: NavController) {
    when (viewModel.selectedFilters.searchType) {
        "Users" -> {
            LazyColumn {
                items(viewModel.searchedProfiles ?: emptyList()) { profile ->
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

        "Travels" -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 64.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.searchedTravels) { travel ->
                    SearchedTravelItem(travel, onClick = {
                        viewModel.travelOverview.id = travel.travel?.id
                        viewModel.showTravel()
                    })
                }
            }
        }
    }
}

@Composable
fun SearchedTravelItem(travel: TimelineResult.TimelineTravel, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            if (travel.travel?.image?.url?.isNotEmpty() == true) {
                AsyncImage(
                    model = travel.travel.image.url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (travel.travel?.image?.url?.isNotEmpty() == true) {
                Text(
                    text = stringResource(R.string.noPhotoAvailable),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            travel.travel?.name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "${travel.travel?.from} - ${travel.travel?.to}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            travel.travel?.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SearchFiltersContent(
    filters: SearchFilters,
    onFiltersChanged: (SearchFilters) -> Unit,
    onClose: () -> Unit,
    searchedProfilesClear: () -> Unit,
    searchedTravelsClear: () -> Unit,
    onApplySearchTravels: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.searchFilters),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Divider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.searchType),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(end = 16.dp)
            )
            DropdownMenuFilter(
                options = listOf("Users", "Travels"),
                selectedOption = filters.searchType,
                onOptionSelected = {
                    onFiltersChanged(filters.copy(searchType = it))
                    when (filters.searchType) {
                        "Travels" -> searchedProfilesClear()
                        "Users" -> searchedTravelsClear()
                    }
                }
            )
        }

        if (filters.searchType == "Travels") {
            OutlinedTextField(
                value = filters.dateFrom ?: "",
                onValueChange = { onFiltersChanged(filters.copy(dateFrom = it)) },
                label = { Text("Date From") },
                placeholder = { Text("YYYY-MM-DD") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = filters.dateTo ?: "",
                onValueChange = { onFiltersChanged(filters.copy(dateTo = it)) },
                label = { Text("Date To") },
                placeholder = { Text("YYYY-MM-DD") },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenuFilter(
                options = listOf("Ascending", "Descending"),
                selectedOption = filters.sortingDirection ?: "Ascending",
                onOptionSelected = { onFiltersChanged(filters.copy(sortingDirection = it)) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onClose) {
                Text(stringResource(R.string.cancel))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    stringResource(R.string.apply),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.clickable {
                        onApplySearchTravels()
                    }
                )
            }
        }
    }
}

@Composable
fun DropdownMenuFilter(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(200.dp)
            .wrapContentSize(Alignment.CenterEnd)
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selectedOption)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
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