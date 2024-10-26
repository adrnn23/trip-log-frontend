package com.example.triplog.profile.presentation.sections

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplog.R
import com.example.triplog.authorization.login.components.InformationDialog
import com.example.triplog.profile.components.TitleComponent
import com.example.triplog.profile.components.UpdatePasswordComponent
import com.example.triplog.profile.presentation.EditProfileViewModel
import com.example.triplog.profile.presentation.UpdatePasswordState

@Composable
fun UpdatePasswordSection(
    innerpadding: PaddingValues,
    viewModel: EditProfileViewModel,
) {
    val alpha = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true) {
        alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 200))
    }

    if (viewModel.updatePasswordState != UpdatePasswordState.NotUpdated) {
        var text = viewModel.updatePasswordResult?.message.toString() + "\n"
        if (viewModel.updatePasswordState == UpdatePasswordState.ValidationError) {
            if (viewModel.updatePasswordResult?.errors != null) {
                viewModel.updatePasswordResult?.errors?.password?.forEach { item ->
                    text += "\n - $item"
                }
                viewModel.updatePasswordResult?.errors?.oldPassword?.forEach { item ->
                    text += "\n - $item"
                }
            }
        }
        InformationDialog(
            R.string.updatePasswordInformation,
            text = {
                Text(
                    text = text,
                    fontSize = 14.sp,
                    color = if (viewModel.updatePasswordState == UpdatePasswordState.Updated) {
                        MaterialTheme.colorScheme.onTertiaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    }
                )
            },
            icon = {
                if (viewModel.updatePasswordResult?.resultCode != 200) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            },
            containerColor = {
                if (viewModel.updatePasswordResult?.resultCode != 200)
                    MaterialTheme.colorScheme.errorContainer
                else
                    MaterialTheme.colorScheme.tertiaryContainer
            },
            onDismiss = { viewModel.updatePasswordState = UpdatePasswordState.NotUpdated },
            onConfirmClick = { viewModel.updatePasswordState = UpdatePasswordState.NotUpdated }
        )
    }

    LazyColumn(
        modifier = Modifier
            .alpha(alpha.value)
            .fillMaxSize()
            .padding(2.dp),
        contentPadding = innerpadding,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            TitleComponent(
                R.string.updateYourPassword,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )
        }
        item {
            UpdatePasswordComponent(viewModel)
        }
    }
}