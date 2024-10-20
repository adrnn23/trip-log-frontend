package com.example.triplog.profile.presentation.sections

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.main.navigation.Screen
import com.example.triplog.profile.components.ChangeButton
import com.example.triplog.profile.components.EditAvatarComponent
import com.example.triplog.profile.components.EditBioComponent
import com.example.triplog.profile.components.EditEmailComponent
import com.example.triplog.profile.components.EditProfileDialog
import com.example.triplog.profile.components.EditUsernameComponent
import com.example.triplog.profile.components.LinksListComponent
import com.example.triplog.profile.components.TitleComponent
import com.example.triplog.profile.components.TravelPreferencesComponent
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
            icon = { Icon(Icons.Default.Save, contentDescription = null)},
            title = R.string.saveChanges,
            text = { Text(stringResource(R.string.wouldYouLikeToSaveChanges), fontSize = 14.sp) },
            onDismiss = { viewModel.isSaveChangesDialogVisible = false },
            onConfirmClick = {
                viewModel.editUserProfile()
                viewModel.isSaveChangesDialogVisible = false
                navController.navigate(Screen.UserProfileScreen.destination)
            },
            onDismissClick = {
                viewModel.isSaveChangesDialogVisible = false
                navController.navigate(Screen.UserProfileScreen.destination)
            })
    }
    LazyColumn(
        modifier = Modifier
            .alpha(alpha.value)
            .fillMaxSize()
            .padding(2.dp),
        contentPadding = innerpadding,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        item {
            EditAvatarComponent()
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Column(modifier = Modifier.padding(2.dp)) {
                TitleComponent(
                    R.string.basicInformation,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                )
            }
            EditUsernameComponent(viewModel)
        }

        item {
            EditEmailComponent(viewModel)
        }

        item {
            EditBioComponent(viewModel)
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(2.dp)
            ) {
                TitleComponent(
                    R.string.travelPreferences,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                )
                ChangeButton(
                    modifier = Modifier,
                    icon = Icons.Default.Edit,
                    changeAction = {
                        viewModel.section =
                            com.example.triplog.profile.presentation.EditProfileSection.EditTravelPreferences
                        viewModel.travelPreferencesList =
                            viewModel.travelPreferencesListInitialization(
                                viewModel.selectedTravelPreferences,
                                viewModel.travelPreferencesList
                            )
                    })
            }
            TravelPreferencesComponent(
                viewModel.selectedTravelPreferences, Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            LinksListComponent(viewModel)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}