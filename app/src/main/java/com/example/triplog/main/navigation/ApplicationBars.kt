package com.example.triplog.main.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.triplog.profile.components.EditButtonComponent

@Composable
fun BottomApplicationBar() {
    BottomAppBar(actions = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,

            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    Icons.Filled.AddCircle,
                    contentDescription = "Add trip",
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "User`s Profile",
                    modifier = Modifier.size(30.dp)
                )
            }
        }

    }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopApplicationBar(title: String, navController: NavController) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = {  }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back to home",
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
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