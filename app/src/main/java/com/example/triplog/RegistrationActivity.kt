package com.example.triplog

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.triplog.ui.theme.TripLogTheme

@Composable
fun RegistrationScreen(navController: NavController) {
    val viewModel: RegistrationViewModel = viewModel(factory = RegistrationViewModel.Factory)
    var showErrors by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.registrationState) {
        if (viewModel.registrationState == RegistrationState.Error) {
            if (viewModel.registrationResult?.resultCode == 422) {
                showErrors = true
            }
        }
        if (viewModel.registrationState == RegistrationState.Registered) {
            if (viewModel.registrationResult != null) {
                showErrors = false
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = stringResource(R.string.app_name),
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 60.dp)
        )

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.registration),
                fontSize = 32.sp,
                modifier = Modifier
                    .padding(bottom = 10.dp, top = 10.dp)
            )
            UsernameInput(
                label = R.string.username,
                value = viewModel.username,
                onValueChanged = { viewModel.username = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)
            )
            if (showErrors) {
                if (viewModel.registrationResult?.errors?.name != null) {
                    ErrorDetails(error = viewModel.registrationResult?.errors?.name!![0].toString())
                }
            }
            EmailInput(
                label = R.string.email,
                value = viewModel.email,
                onValueChanged = { viewModel.email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)
            )
            if (showErrors) {
                if (viewModel.registrationResult?.errors?.email != null) {
                    ErrorDetails(error = viewModel.registrationResult?.errors?.email!![0].toString())
                }
            }
            PasswordInput(
                label = R.string.password,
                value = viewModel.password,
                onValueChanged = { viewModel.password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)
            )
            if (showErrors) {
                if (viewModel.registrationResult?.errors?.password != null) {
                    ErrorDetails(error = viewModel.registrationResult?.errors?.password!![0].toString())
                }
            }
            PasswordInput(
                label = R.string.repeatedPassword,
                value = viewModel.repeatedPassword,
                onValueChanged = { viewModel.repeatedPassword = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)
            )
            if (showErrors) {
                if (viewModel.registrationResult?.errors?.password != null) {
                    ErrorDetails(error = viewModel.registrationResult?.errors?.password!![0].toString())
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(10.dp)
        ) {
            RegisterButton(
                onRegistrationClick = { viewModel.register() }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color.DarkGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))
            HaveAccount(navController)
        }

    }
}

@Composable
fun RegisterButton(
    onRegistrationClick: () -> Unit,
) {
    Button(
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = {
            onRegistrationClick()
        },
        modifier = Modifier.width(220.dp)
    ) {
        Text(
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            text = stringResource(R.string.register),
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
    modifier: Modifier = Modifier,
) {
    val usernameIcon = @Composable {
        Icon(
            Icons.Default.Person,
            contentDescription = "userIcon",
            tint = MaterialTheme.colorScheme.primary
        )
    }
    TextField(
        label = { Text(stringResource(label)) },
        value = value,
        onValueChange = onValueChanged,
        leadingIcon = usernameIcon,
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
    modifier: Modifier = Modifier,
) {
    val emailIcon = @Composable {
        Icon(
            Icons.Default.Mail,
            contentDescription = "mailIcon",
            tint = MaterialTheme.colorScheme.primary
        )
    }
    TextField(
        label = { Text(stringResource(label)) },
        value = value,
        onValueChange = onValueChanged,
        leadingIcon = emailIcon,
        singleLine = true,
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
    TextField(
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
fun HaveAccount(navController: NavController) {
    val annotatedString = buildAnnotatedString {
        val str = stringResource(id = R.string.haveAccount)
        val startStr = str.indexOf("Login")
        val endStr = startStr + 5
        append(str)
        addStyle(
            style = SpanStyle(
                color = Color(0, 0, 128),
                textDecoration = TextDecoration.Underline,
            ),
            start = startStr, end = endStr
        )
    }
    ClickableText(
        text = annotatedString,
        onClick = { navController.navigate(Screen.LoginScreen.destination) },
        modifier = Modifier
            .padding(2.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    TripLogTheme {
        val navController = rememberNavController()
        RegistrationScreen(navController = navController)
    }
}