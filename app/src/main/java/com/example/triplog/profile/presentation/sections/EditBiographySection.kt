package com.example.triplog.profile.presentation.sections

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplog.R
import com.example.triplog.authorization.login.components.InformationDialog
import com.example.triplog.profile.data.ErrorData
import com.example.triplog.profile.data.ErrorType
import com.example.triplog.profile.presentation.EditProfileViewModel

@Composable
fun EditBiographySection(innerpadding: PaddingValues, viewModel: EditProfileViewModel) {
    val alpha = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true) {
        alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 200))
    }

    if (viewModel.errorMessage.isError &&  viewModel.errorMessage.type==ErrorType.Biography) {
        InformationDialog(
            R.string.validationError,
            text = {
                Text(
                    stringResource(R.string.biographyLength),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            },
            containerColor = MaterialTheme.colorScheme.errorContainer,
            onDismiss = { viewModel.errorMessage = ErrorData(false, null, "") },
            onConfirmClick = { viewModel.errorMessage = ErrorData(false, null, "") }
        )
    }
    Box(
        modifier = Modifier
            .alpha(alpha.value)
            .padding(innerpadding)
            .fillMaxSize()
            .padding(2.dp)
    ) {
        BasicTextField(
            value = viewModel.bioTemp,
            onValueChange = { viewModel.bioTemp = it },
            textStyle = TextStyle(fontSize = 16.sp),
            modifier = Modifier.fillMaxSize()
        )
    }
}