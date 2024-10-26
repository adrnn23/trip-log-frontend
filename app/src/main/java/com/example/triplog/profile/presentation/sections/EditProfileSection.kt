package com.example.triplog.profile.presentation.sections

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.main.navigation.Screen
import com.example.triplog.profile.components.EditAvatarComponent
import com.example.triplog.profile.components.EditBasicInformation
import com.example.triplog.profile.components.EditPasswordComponent
import com.example.triplog.profile.components.EditProfileDialog
import com.example.triplog.profile.components.EditTravelPreferencesComponent
import com.example.triplog.profile.components.LinksListComponent
import com.example.triplog.profile.presentation.EditProfileViewModel

@Composable
fun EditProfileSection(
    innerpadding: PaddingValues,
    viewModel: EditProfileViewModel,
    navController: NavController
) {
    val alpha = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true) {
        alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 200))
    }

    if (viewModel.isSaveChangesDialogVisible) {
        EditProfileDialog(
            icon = { Icon(Icons.Default.Save, contentDescription = null) },
            title = R.string.saveChanges,
            text = { Text(stringResource(R.string.wouldYouLikeToSaveChanges), fontSize = 14.sp) },
            onDismiss = { viewModel.isSaveChangesDialogVisible = false },
            onConfirmClick = {
                viewModel.isSaveChangesDialogVisible = false
                navController.navigate("${Screen.ProfileScreen.destination}/${viewModel.token}")
                viewModel.editUserProfile()
            },
            onDismissClick = {
                viewModel.isSaveChangesDialogVisible = false
                navController.navigate("${Screen.ProfileScreen.destination}/${viewModel.token}")
            })
    }
    LazyColumn(
        modifier = Modifier
            .alpha(alpha.value)
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = innerpadding,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        item {
            EditAvatarComponent()
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            EditBasicInformation(viewModel, onClick = {
                viewModel.section =
                    com.example.triplog.profile.presentation.EditProfileSection.EditBasicInformation
            })
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            EditTravelPreferencesComponent(viewModel)
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            LinksListComponent(viewModel) { viewModel.isAddLinkDialogVisible = true }
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            EditPasswordComponent {
                viewModel.section =
                    com.example.triplog.profile.presentation.EditProfileSection.UpdatePassword
            }
        }
    }
}