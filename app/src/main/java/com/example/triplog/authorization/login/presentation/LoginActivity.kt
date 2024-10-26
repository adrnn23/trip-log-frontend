package com.example.triplog.authorization.login.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.authorization.login.components.ButtonDivider
import com.example.triplog.authorization.login.components.ErrorDetails
import com.example.triplog.authorization.login.components.LinearIndicator
import com.example.triplog.authorization.login.components.LoginActivityButton
import com.example.triplog.authorization.login.components.InformationDialog
import com.example.triplog.authorization.registration.components.EmailInput
import com.example.triplog.authorization.registration.components.PasswordInput
import com.example.triplog.main.navigation.Screen


@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)

    LaunchedEffect(viewModel.loginState) {
        viewModel.handleLoginState(navController)
    }

    LaunchedEffect(viewModel.loadingState) {
        viewModel.handleLoadingState()
    }

    if (viewModel.isUnauthorizedDialogVisible) {
        InformationDialog(
            title = R.string.result,
            text = { Text(text = viewModel.loginResult?.message.toString()) },
            icon = {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            },
            containerColor = { MaterialTheme.colorScheme.errorContainer },
            onDismiss = {
                viewModel.isUnauthorizedDialogVisible = false
                viewModel.loginState = LoginState.NotLogged
            },
            onConfirmClick = {
                viewModel.isUnauthorizedDialogVisible = false
                viewModel.loginState = LoginState.NotLogged
            }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
        }

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
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)
            )
            if (viewModel.isErrorsVisible) {
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
            if (viewModel.isErrorsVisible) {
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
            LoginActivityButton(
                R.string.login,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ) { viewModel.login() }

            ButtonDivider()

            LoginActivityButton(
                R.string.createNewAccount,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            ) { navController.navigate(Screen.RegistrationScreen.destination) }

            Spacer(modifier = Modifier.height(20.dp))

            if (viewModel.isProgressIndicatorVisible) {
                LinearIndicator()
            }
        }
    }
}