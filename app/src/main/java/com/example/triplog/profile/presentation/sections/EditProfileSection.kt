package com.example.triplog.profile.presentation.sections

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplog.R
import com.example.triplog.profile.components.EditAvatarComponent
import com.example.triplog.profile.components.EditBasicInformation
import com.example.triplog.profile.components.EditPasswordComponent
import com.example.triplog.profile.components.EditProfileDialog
import com.example.triplog.profile.components.EditTravelPreferencesComponent
import com.example.triplog.profile.components.LinksListComponent
import com.example.triplog.profile.presentation.EditProfileViewModel
import com.example.triplog.profile.presentation.EditUserProfileSection

@Composable
fun EditProfileSection(
    innerpadding: PaddingValues,
    viewModel: EditProfileViewModel
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
                viewModel.editUserProfile()
            },
            onDismissClick = { viewModel.isSaveChangesDialogVisible = false })
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = innerpadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        item {
            Text(
                stringResource(R.string.editYourProfile),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 1.dp)
        }
        item {
            EditAvatarComponent(viewModel)
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            EditBasicInformation(viewModel, onClick = {
                viewModel.section = EditUserProfileSection.EditBasicInformation
                viewModel.bioTemp = viewModel.editProfile.bio ?: ""
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
                    EditUserProfileSection.UpdatePassword
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}