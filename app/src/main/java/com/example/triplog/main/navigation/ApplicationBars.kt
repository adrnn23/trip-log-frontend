package com.example.triplog.main.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.main.data.BottomNavItem
import com.example.triplog.profile.components.EditButtonComponent

@Composable
fun ApplicationBottomBar(
    block: Boolean,
    index: Int,
    goToMainPage: () -> Unit,
    goToProfile: () -> Unit,
    goToCreateTravel: () -> Unit
) {
    val navItems = listOf(
        BottomNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home, goToMainPage),
        BottomNavItem("Add", Icons.Filled.Add, Icons.Outlined.Add, goToCreateTravel),
        BottomNavItem("Profile", Icons.Filled.Person, Icons.Outlined.Person, goToProfile)
    )
    val selectedNavItem by rememberSaveable { mutableIntStateOf(index) }

    NavigationBar {
        navItems.forEachIndexed { i, item ->
            NavigationBarItem(
                onClick = {
                    if (!block)
                        item.function()
                },
                selected = i == selectedNavItem,
                label = { Text(text = navItems[i].name) },
                icon = {
                    Icon(
                        imageVector = if (selectedNavItem == i) {
                            navItems[i].selectedIcon
                        } else navItems[i].icon,
                        contentDescription = item.name
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationTopBar(title: String, icon: @Composable () -> Unit, logout: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            icon()
        },
        actions = {
            Box(modifier = Modifier.clickable {
                showMenu = !showMenu
            }) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier.size(30.dp)
                )
                DropdownMenu(
                    modifier = Modifier.align(Alignment.Center),
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            showMenu = false
                            logout()
                        },
                        text = { Text(text = stringResource(R.string.logout), fontSize = 14.sp) }
                    )
                }

            }

        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    navIcon: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    logout: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = { navIcon() },
        actions = {
            icon()
            Box(modifier = Modifier.clickable {
                showMenu = !showMenu
            }) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier.size(30.dp)
                )
                DropdownMenu(
                    modifier = Modifier.align(Alignment.Center),
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            showMenu = false
                            logout()
                        },
                        text = { Text(text = stringResource(R.string.logout), fontSize = 14.sp) }
                    )
                }

            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopApplicationBar(title: String, onClick: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = { onClick() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        actions = {
            Box(modifier = Modifier.clickable {
                showMenu = !showMenu
            }) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier.size(30.dp)
                )
            }

        }

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTravelTopBar(title: String, onClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = {
                onClick()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back to home",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileTopBar(title: String, backToEditProfile: () -> Unit) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = { backToEditProfile() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back to edit profile",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    )
}

@Composable
fun EditProfileBottomBar(
    @StringRes buttonText: Int,
    onClick: () -> Unit
) {
    BottomAppBar(actions = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,

            modifier = Modifier
                .fillMaxWidth()
        ) {
            EditButtonComponent(
                buttonText,
                MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                onClick()
            }
        }
    }
    )
}


@Composable
fun CreateTravelBottomBar(
    @StringRes buttonText: Int,
    onClick: () -> Unit
) {
    BottomAppBar(actions = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,

            modifier = Modifier
                .fillMaxWidth()
        ) {
            EditButtonComponent(
                buttonText,
                MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                onClick()
            }
        }
    }
    )
}
