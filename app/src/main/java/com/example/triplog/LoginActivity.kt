package com.example.triplog

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.triplog.ui.theme.TripLogTheme

@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
    var showDialog by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.loginState) {
        if (viewModel.loginState == LoginState.Error || viewModel.loginState == LoginState.Unauthorized) {
            if (viewModel.loginResult?.resultCode == 422) {
                showErrors = true
                showDialog = false
            } else if (viewModel.loginResult?.resultCode == 401) {
                showDialog = true
                showErrors = false
            }
        }
        if (viewModel.loginState == LoginState.Logged) {
            if (viewModel.loginResult != null) {
                showErrors = false
                showDialog = false
                navController.navigate(Screen.UserProfileScreen.destination)
            }
        }
    }

    if (showDialog) {
        UnauthorizedError(
            viewModel.loginResult?.message.toString()
        ) {
            showDialog = false
            viewModel.loginState = LoginState.NotLogged
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
                text = stringResource(R.string.login),
                fontSize = 32.sp,
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp)
            )

            EmailInput(
                label = R.string.email,
                value = viewModel.email,
                onValueChanged = { viewModel.email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)
            )
            if (showErrors) {
                if (viewModel.loginResult?.errors?.email != null) {
                    ErrorDetails(error = viewModel.loginResult?.errors?.email!![0].toString())
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
                if (viewModel.loginResult?.errors?.password != null) {
                    ErrorDetails(error = viewModel.loginResult?.errors?.password!![0].toString())
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(10.dp)
        ) {
            LoginButton(
                onLoginClick = { viewModel.login() }
            )

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color.DarkGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))
            NoAccount(navController)
        }
    }
}
@Composable
fun ErrorDetails(error: String) {
    Text(
        text = error,
        color = MaterialTheme.colorScheme.onErrorContainer,
        fontSize = 12.sp
    )
}

@Composable
fun UnauthorizedError(error: String, onErrorDialogClick: () -> Unit) {
    AlertDialog(
        title = { Text(text = error) },
        text = { Text(text = stringResource(id = R.string.invalidLoginCredentials)) },
        icon = { Icon(imageVector = Icons.Default.Info, contentDescription = null) },
        containerColor = MaterialTheme.colorScheme.errorContainer,
        onDismissRequest = { onErrorDialogClick() },
        confirmButton = {
            TextButton(
                onClick = { onErrorDialogClick() },
                shape = RoundedCornerShape(5.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.ok),
                )
            }
        }
    )
}

@Composable
fun LoginButton(onLoginClick: () -> Unit) {
    Button(
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = {
            onLoginClick()
        },
        modifier = Modifier.width(220.dp)
    ) {
        Text(
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            text = stringResource(R.string.login),
            fontSize = 18.sp,
            modifier = Modifier
                .padding(2.dp)
        )
    }
}

@Composable
fun NoAccount(navController: NavController) {
    val annotatedString = buildAnnotatedString {
        val str = stringResource(id = R.string.noAccount)
        val startStr = str.indexOf("Sign")
        val endStr = startStr + 8
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
        onClick = { navController.navigate(Screen.RegistrationScreen.destination) },
        modifier = Modifier
            .padding(2.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TripLogTheme {
        val navController = rememberNavController()
        LoginScreen(navController = navController)
    }
}
