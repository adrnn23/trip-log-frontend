package com.example.triplog.authorization.registration.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegistrationActivityButton(
    @StringRes text: Int,
    containerColor: Color,
    onClick: () -> Unit
) {
    Button(
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        onClick = {
            onClick()
        },
        modifier = Modifier.fillMaxWidth()
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
fun UsernameInput(
    @StringRes label: Int,
    value: String,
    onValueChanged: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val usernameIcon = @Composable {
        Icon(
            Icons.Default.Person,
            contentDescription = "userIcon",
            tint = MaterialTheme.colorScheme.primary
        )
    }
    OutlinedTextField(
        label = { Text(stringResource(label)) },
        value = value,
        onValueChange = onValueChanged,
        leadingIcon = usernameIcon,
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        ),
        modifier = modifier,
    )
}

@Composable
fun EmailInput(
    @StringRes label: Int,
    value: String,
    onValueChanged: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val emailIcon = @Composable {
        Icon(
            Icons.Default.Mail,
            contentDescription = "mailIcon",
            tint = MaterialTheme.colorScheme.primary
        )
    }
    OutlinedTextField(
        label = { Text(stringResource(label)) },
        value = value,
        onValueChange = onValueChanged,
        leadingIcon = emailIcon,
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        ),
        modifier = modifier,
    )
}

@Composable
fun PasswordInput(
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