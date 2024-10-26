package com.example.triplog.profile.components

import androidx.annotation.StringRes
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
import androidx.compose.material.icons.filled.Description
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplog.R
import com.example.triplog.authorization.login.components.InformationDialog
import com.example.triplog.authorization.registration.components.EmailInput
import com.example.triplog.authorization.registration.components.UsernameInput
import com.example.triplog.profile.data.ErrorData
import com.example.triplog.profile.data.ErrorType
import com.example.triplog.profile.data.LinkData
import com.example.triplog.profile.presentation.EditProfileSection
import com.example.triplog.profile.presentation.EditProfileViewModel
import com.example.triplog.travel.components.ButtonComponent

@Composable
fun BioInput(
    @StringRes label: Int,
    value: String,
    onValueChanged: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier
) {
    val bioIcon = @Composable {
        Icon(
            Icons.Default.Description,
            contentDescription = "bioIcon",
            tint = MaterialTheme.colorScheme.primary
        )
    }
    OutlinedTextField(
        label = { Text(stringResource(label)) },
        value = value,
        onValueChange = onValueChanged,
        leadingIcon = bioIcon,
        enabled = enabled,
        maxLines = 4,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        )
    )
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
fun EditAvatarComponent() {
    Box(modifier = Modifier.size(140.dp)) {
        Image(
            painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .size(size = 120.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(20.dp)
                )
                .clip(RoundedCornerShape(20.dp))
                .align(Alignment.Center)
        )
        ChangeButton(icon = Icons.Default.Edit, modifier = Modifier.align(Alignment.BottomEnd)) {}
    }
}

@Composable
fun EditUsernameComponent(viewModel: EditProfileViewModel) {
    if (viewModel.isUsernameDialogVisible) {
        viewModel.usernameTemp = viewModel.username
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
                    if (viewModel.usernameTemp.length < 33) {
                        viewModel.username = viewModel.usernameTemp
                        viewModel.usernameTemp = ""
                        viewModel.isUsernameDialogVisible = false
                    } else {
                        viewModel.isUsernameDialogVisible = false
                        viewModel.errorMessage = ErrorData(true, ErrorType.Username, "")
                    }
                }
            },
            onDismissClick = {
                viewModel.usernameTemp = ""
                viewModel.isUsernameDialogVisible = false
            }
        )
    }
    if (viewModel.errorMessage.isError && viewModel.errorMessage.type == ErrorType.Username) {
        InformationDialog(
            title = R.string.validationError,
            text = {
                Text(
                    stringResource(R.string.usernameLength),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            },
            icon = {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            },
            containerColor = { MaterialTheme.colorScheme.errorContainer },
            onDismiss = { viewModel.errorMessage = ErrorData(false, null, "") },
            onConfirmClick = { viewModel.errorMessage = ErrorData(false, null, "") }
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
            value = viewModel.username,
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
        viewModel.emailTemp = viewModel.email ?: ""
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
                    viewModel.email = viewModel.emailTemp
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
            value = viewModel.email ?: "",
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
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
    ) {
        TitleComponent(
            R.string.travelPreferences,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(4.dp))
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
                Text(text = stringResource(R.string.addTravelPreferences), fontSize = 12.sp)
            }
        }
        ButtonComponent(
            R.string.editPreferences,
            modifier = Modifier.width(220.dp),
            onClick = {
                viewModel.section =
                    EditProfileSection.EditTravelPreferences
                viewModel.tempTravelPreferencesList =
                    viewModel.travelPreferencesList.toMutableList()
            }
        )
    }
}

@Composable
fun EditBioComponent(viewModel: EditProfileViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        BioInput(
            R.string.biography,
            viewModel.bio,
            enabled = false,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(6.dp),
            onValueChanged = { }
        )
        ChangeButton(
            modifier = Modifier,
            icon = Icons.Default.Edit,
            changeAction = {
                viewModel.bioTemp = viewModel.bio
                viewModel.section = EditProfileSection.EditBiography
            })
    }
}

@Composable
fun ChangeButton(modifier: Modifier, icon: ImageVector, changeAction: () -> Unit) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.tertiary,
        modifier = modifier
            .size(28.dp)
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
//    var extended by remember { mutableStateOf(false) }

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
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TitleComponent(
                R.string.links,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.width(4.dp))
            /*            if (viewModel.links.size > 1) {
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
                        }*/
        }
        if (viewModel.links.isNotEmpty()) {
//            if (extended) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(6.dp)
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
                        }
                    }
                }
            }
            /*            } else {
                            if (viewModel.links[0] != null) {
                                Box(
                                    contentAlignment = Alignment.CenterStart,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Link(link = viewModel.links[0]!!)
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
                                                    viewModel.links.indexOf(viewModel.links[0])
                                            }
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }*/
        } else {
            Text(text = stringResource(R.string.addMaxThreeLinks), fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))

        ButtonComponent(
            R.string.addLink,
            modifier = Modifier.width(120.dp),
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
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
    ) {
        TitleComponent(
            R.string.editBasicInformation,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(8.dp))
        TitleComponent(
            R.string.username,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Text(text = viewModel.username, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))

        TitleComponent(
            R.string.email,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Text(text = viewModel.email ?: "", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))

        TitleComponent(
            R.string.biography,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Text(text = viewModel.bio, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))

        ButtonComponent(
            R.string.editBasicInformation,
            modifier = Modifier.width(220.dp),
            onClick = { onClick() }
        )
    }

}