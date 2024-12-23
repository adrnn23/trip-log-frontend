package com.example.triplog.profile.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.triplog.R
import com.example.triplog.authorization.login.components.InformationDialog
import com.example.triplog.authorization.registration.components.EmailInput
import com.example.triplog.authorization.registration.components.UsernameInput
import com.example.triplog.profile.data.ErrorData
import com.example.triplog.profile.data.ErrorType
import com.example.triplog.profile.data.LinkData
import com.example.triplog.profile.presentation.EditProfileViewModel
import com.example.triplog.profile.presentation.EditUserProfileSection
import com.example.triplog.travel.components.ButtonComponent

@Composable
fun EditBioComponent(viewModel: EditProfileViewModel) {
    var enabled by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        BioInput(
            R.string.biography,
            viewModel.bioTemp,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(6.dp),
            onValueChanged = { viewModel.bioTemp = it }
        )
        ChangeButton(
            modifier = Modifier,
            icon = Icons.Default.Edit,
            changeAction = {
                enabled = !enabled
            })
    }
}

@Composable
fun BioInput(
    @StringRes labelRes: Int,
    text: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onValueChanged: (String) -> Unit
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = text,
            onValueChange = onValueChanged,
            enabled = enabled,
            maxLines = 6,
            textStyle = TextStyle(fontSize = 12.sp),
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(id = labelRes)) }
        )
        Text(
            text = if (512 - text.length >= 0) {
                "${512 - text.length} characters remaining"
            } else "Too many characters!",
            style = TextStyle(fontSize = 10.sp),
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp)
        )
    }
}


@Composable
fun LinkInput(
    @StringRes label: Int,
    value: String,
    onValueChanged: (String) -> Unit,
    imageVector: ImageVector,
    modifier: Modifier
) {
    val linkIcon = @Composable {
        Icon(
            imageVector,
            contentDescription = "linkIcon",
            tint = MaterialTheme.colorScheme.primary
        )
    }
    OutlinedTextField(
        label = { Text(stringResource(label)) },
        value = value,
        onValueChange = onValueChanged,
        leadingIcon = linkIcon,
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        )
    )
}

@Composable
fun EditAvatarComponent(viewModel: EditProfileViewModel) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            viewModel.avatar = uri
        }
    val showPhoto = remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            stringResource(R.string.editAvatar),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 1.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { showPhoto.value = true }
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center) {
            if (viewModel.avatar != null) {
                val painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = viewModel.avatar)
                        .build()
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.Center)
                )
            } else {
                Text(
                    text = stringResource(R.string.noPhotoSelected),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            ChangeButton(
                icon = Icons.Default.Edit,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }
    if (showPhoto.value) {
        AlertDialog(
            onDismissRequest = { showPhoto.value = false },
            title = {
                Text(text = stringResource(R.string.travelPhoto))
            },
            text = {
                if (viewModel.avatar != null) {
                    val painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = viewModel.avatar)
                            .build()
                    )
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(text = stringResource(R.string.noPhotoSelected))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showPhoto.value = false }
                ) {
                    Text(text = stringResource(R.string.close))
                }
            }
        )
    }
}

@Composable
fun EditUsernameComponent(viewModel: EditProfileViewModel) {
    if (viewModel.isUsernameDialogVisible) {
        viewModel.usernameTemp = viewModel.editProfile.name ?: ""
        EditProfileDialog(
            title = R.string.editUsername,
            text = {
                UsernameInput(
                    label = R.string.username,
                    value = viewModel.usernameTemp,
                    onValueChanged = { viewModel.usernameTemp = it },
                    enabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                )
            },
            icon = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            onDismiss = {
                viewModel.isUsernameDialogVisible = false
                viewModel.usernameTemp = ""
            },
            onConfirmClick = {
                if (viewModel.usernameTemp.isNotEmpty()) {
                    viewModel.editProfile.name = viewModel.usernameTemp
                    viewModel.usernameTemp = ""
                    viewModel.isUsernameDialogVisible = false
                }
            },
            onDismissClick = {
                viewModel.usernameTemp = ""
                viewModel.isUsernameDialogVisible = false
            }
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        UsernameInput(
            label = R.string.username,
            value = viewModel.editProfile.name ?: "",
            onValueChanged = { },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(6.dp)
        )
        ChangeButton(
            modifier = Modifier,
            icon = Icons.Default.Edit,
            changeAction = {
                viewModel.isUsernameDialogVisible = !viewModel.isUsernameDialogVisible
            }
        )
    }

}

@Composable
fun EditEmailComponent(viewModel: EditProfileViewModel) {
    if (viewModel.isEmailDialogVisible) {
        viewModel.emailTemp = viewModel.editProfile.email ?: ""
        EditProfileDialog(
            icon = { Icon(Icons.Default.Email, contentDescription = null) },
            title = R.string.editEmail,
            text = {
                EmailInput(
                    label = R.string.email,
                    value = viewModel.emailTemp,
                    onValueChanged = { viewModel.emailTemp = it },
                    enabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                )
            },
            onDismiss = {
                viewModel.isEmailDialogVisible = false
                viewModel.emailTemp = ""
            },
            onConfirmClick = {
                if (viewModel.emailTemp.isNotEmpty()) {
                    viewModel.editProfile.email = viewModel.emailTemp
                    viewModel.emailTemp = ""
                    viewModel.isEmailDialogVisible = false
                }
            },
            onDismissClick = {
                viewModel.isEmailDialogVisible = false
                viewModel.emailTemp = ""
            }
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        EmailInput(
            label = R.string.email,
            value = viewModel.editProfile.email ?: "",
            onValueChanged = { },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(6.dp)
        )
        ChangeButton(
            modifier = Modifier,
            icon = Icons.Default.Edit,
            changeAction = { viewModel.isEmailDialogVisible = !viewModel.isEmailDialogVisible }
        )
    }
}

@Composable
fun EditTravelPreferencesComponent(viewModel: EditProfileViewModel) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            stringResource(R.string.travelPreferences),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 1.dp)

        if (viewModel.travelPreferencesList.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(24.dp, 108.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(viewModel.travelPreferencesList.filter { it!!.isSelected }) { item ->
                    if (item != null) {
                        TravelPreferenceCard(item)
                    }
                }
            }
        } else {
            Text(
                text = stringResource(R.string.addTravelPreferences),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        ButtonComponent(
            R.string.editPreferences,
            modifier = Modifier.width(220.dp),
            enabled = true,
            onClick = {
                viewModel.section =
                    EditUserProfileSection.EditTravelPreferences
                viewModel.tempTravelPreferencesList =
                    viewModel.travelPreferencesList.toMutableList()
            }
        )
    }
}

@Composable
fun ChangeButton(modifier: Modifier, icon: ImageVector, size: Int = 28, changeAction: () -> Unit) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.tertiary,
        modifier = modifier
            .size(size.dp)
            .clickable { changeAction() }
    )
}

@Composable
fun ContentDivider() {
    Spacer(modifier = Modifier.height(4.dp))
    Divider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxWidth(1f)
    )
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
fun EditButtonComponent(
    @StringRes text: Int,
    containerColor: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Button(
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        onClick = {
            onClick()
        },
        modifier = modifier
    ) {
        Text(
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            text = stringResource(text),
            fontSize = 18.sp,
            modifier = Modifier
                .padding(2.dp)
        )
    }
}

@Composable
fun EditProfileDialog(
    @StringRes title: Int,
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    onDismiss: () -> Unit,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    AlertDialog(
        icon = { icon() },
        title = { Text(stringResource(title)) },
        text = { text() },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = { onConfirmClick() }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissClick() }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
fun LinksListComponent(viewModel: EditProfileViewModel, onClick: () -> Unit) {
    var indexToRemove by remember { mutableStateOf<Int?>(null) }

    var site by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    if (viewModel.errorMessage.isError && viewModel.errorMessage.type == ErrorType.Links) {
        InformationDialog(
            R.string.validationError,
            text = {
                if (viewModel.errorMessage.description == "Number") {
                    Text(
                        stringResource(R.string.linksNumber),
                        fontSize = 14.sp, color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                if (viewModel.errorMessage.description == "SiteAndLink") {
                    Text(
                        stringResource(R.string.siteAndLink),
                        fontSize = 14.sp, color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer
                )
            },
            containerColor = { MaterialTheme.colorScheme.errorContainer },
            onDismiss = {
                viewModel.errorMessage = ErrorData(false, null, "")
                viewModel.isAddLinkDialogVisible = true
            },
            onConfirmClick = {
                viewModel.errorMessage = ErrorData(false, null, "")
                viewModel.isAddLinkDialogVisible = true
            }
        )
    }

    if (viewModel.isAddLinkDialogVisible) {
        EditProfileDialog(
            icon = { Icon(Icons.Default.Link, contentDescription = null) },
            title = R.string.addLink,
            text = {
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AddLinkForm(viewModel.links, site, link,
                        onLinkChange = { link = it },
                        onSiteChange = { site = it })
                }
            },
            onDismiss = { viewModel.isAddLinkDialogVisible = false },
            onConfirmClick = {
                viewModel.addNewLink(
                    site,
                    link,
                    onShowDialogChange = { viewModel.isAddLinkDialogVisible = it },
                    onErrorValidation = {
                        viewModel.errorMessage = ErrorData(true, ErrorType.Links, "SiteAndLink")
                    },
                    onClearInputs = {
                        site = ""
                        link = ""
                    },
                )
            },
            onDismissClick = {
                viewModel.isAddLinkDialogVisible = false
                site = ""
                link = ""
            }

        )
    }

    if (viewModel.isDeleteLinkDialogVisible) {
        EditProfileDialog(
            icon = { Icon(Icons.Default.Delete, contentDescription = null) },
            title = R.string.deleteLinkTitle,
            text = { Text(stringResource(R.string.deleteLinkText), fontSize = 14.sp) },
            onDismiss = {
                indexToRemove = null
                viewModel.isDeleteLinkDialogVisible = false
            },
            onConfirmClick = {
                if (indexToRemove != null) {
                    when (indexToRemove) {
                        0 -> {
                            viewModel.links[indexToRemove!!] =
                                (LinkData("Facebook", Icons.Default.Facebook, ""))
                        }
                        1 -> {
                            viewModel.links[indexToRemove!!] =
                                (LinkData("Instagram", Icons.Default.Link, ""))
                        }
                        2 -> {
                            viewModel.links[indexToRemove!!] =
                                (LinkData("X", Icons.Default.Link, ""))
                        }
                    }
                    indexToRemove = null
                }
                viewModel.isDeleteLinkDialogVisible = false
            },
            onDismissClick = {
                indexToRemove = null
                viewModel.isDeleteLinkDialogVisible = false
            })
    }
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            stringResource(R.string.links),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 1.dp)
        if (viewModel.links.isNotEmpty()) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .heightIn(0.dp, 128.dp)
                    .fillMaxWidth()
            ) {
                items(viewModel.links) { link ->
                    if (link.link != "" || link.link.isNotEmpty()) {
                        Column {
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Link(link = link)
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    tint = MaterialTheme.colorScheme.error,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .size(24.dp)
                                        .clickable {
                                            viewModel.isDeleteLinkDialogVisible = true
                                            indexToRemove =
                                                viewModel.links.indexOf(link)
                                        }
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        } else {
            Text(
                text = stringResource(R.string.addMaxThreeLinks),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        ButtonComponent(
            R.string.addLink,
            modifier = Modifier.width(120.dp),
            enabled = true,
            onClick = { onClick() }
        )
    }
}

@Composable
fun AddLinkForm(
    sites: MutableList<LinkData>,
    site: String,
    link: String,
    onLinkChange: (String) -> Unit,
    onSiteChange: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        LinkInput(
            label = R.string.link,
            value = link,
            onValueChanged = onLinkChange,
            imageVector = Icons.Default.Link,
            modifier = Modifier
                .padding(6.dp)
        )
        Box(modifier = Modifier
            .padding(10.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(5.dp)
            )
            .fillMaxWidth(0.5f)
            .height(50.dp)
            .clickable { expanded = !expanded })
        {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    text = site.ifEmpty { stringResource(R.string.selectSite) },
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Icon(
                    imageVector = if (!expanded) {
                        Icons.Default.KeyboardArrowDown
                    } else {
                        Icons.Default.KeyboardArrowUp
                    },
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            DropdownMenu(
                modifier = Modifier.align(Alignment.Center),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                sites.forEach { item ->
                    if (item.link == "" || item.link.isEmpty()) {
                        DropdownMenuItem(
                            onClick = {
                                onSiteChange(item.name)
                                expanded = false
                            },
                            text = { Text(text = item.name, fontSize = 14.sp) }
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun EditBasicInformation(viewModel: EditProfileViewModel, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            stringResource(R.string.editBasicInformation),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 1.dp)
        Text(
            stringResource(R.string.username),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = viewModel.editProfile.name ?: "", style = MaterialTheme.typography.bodyMedium)

        Text(
            stringResource(R.string.email),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = viewModel.editProfile.email ?: "", style = MaterialTheme.typography.bodyMedium)

        Text(
            stringResource(R.string.biography),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = viewModel.editProfile.bio ?: "", style = MaterialTheme.typography.bodyMedium)

        ButtonComponent(
            R.string.editBasicInformation,
            modifier = Modifier.width(220.dp),
            enabled = true,
            onClick = { onClick() }
        )
    }
}