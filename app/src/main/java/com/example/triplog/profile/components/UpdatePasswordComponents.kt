package com.example.triplog.profile.components

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplog.R
import com.example.triplog.profile.presentation.EditProfileViewModel
import com.example.triplog.travel.components.ButtonComponent

@Composable
fun OutlinedPasswordInput(
    @StringRes label: Int,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val passwordIcon = @Composable {
        Icon(
            Icons.Default.Password,
            contentDescription = "passwordIcon",
            tint = MaterialTheme.colorScheme.primary
        )
    }
    OutlinedTextField(
        label = { Text(stringResource(label)) },
        value = value,
        onValueChange = onValueChanged,
        singleLine = true,
        leadingIcon = passwordIcon,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        ),
        modifier = modifier,
        visualTransformation = PasswordVisualTransformation()
    )
}

@Composable
fun EditPasswordComponent(onClick: () -> Unit) {
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
            R.string.updatePassword,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(8.dp))
        TitleComponent(
            R.string.password,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Text(text = "********", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        ButtonComponent(
            R.string.updateYourPassword,
            modifier = Modifier.width(200.dp),
            onClick = { onClick() }
        )
    }
}

@Composable
fun UpdatePasswordComponent(
    viewModel: EditProfileViewModel
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        OutlinedPasswordInput(
            label = R.string.currentPassword,
            value = viewModel.currentPassword ?: "",
            onValueChanged = { viewModel.currentPassword = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp)
        )

        OutlinedPasswordInput(
            label = R.string.newPassword,
            value = viewModel.newPassword ?: "",
            onValueChanged = { viewModel.newPassword = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp)
        )

        OutlinedPasswordInput(
            label = R.string.repeatedNewPassword,
            value = viewModel.repeatedNewPassword ?: "",
            onValueChanged = { viewModel.repeatedNewPassword = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp)
        )
    }
}