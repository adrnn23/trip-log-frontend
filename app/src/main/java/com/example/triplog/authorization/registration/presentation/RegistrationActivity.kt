package com.example.triplog.authorization.registration.presentation

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
import com.example.triplog.authorization.registration.components.EmailInput
import com.example.triplog.authorization.registration.components.PasswordInput
import com.example.triplog.authorization.registration.components.RegistrationActivityButton
import com.example.triplog.authorization.registration.components.UsernameInput
import com.example.triplog.main.navigation.Screen

@Composable
fun RegistrationScreen(navController: NavController) {
    val viewModel: RegistrationViewModel = viewModel(factory = RegistrationViewModel.Factory)

    LaunchedEffect(viewModel.registrationState) {
        viewModel.handleRegistrationState(navController)
    }
    LaunchedEffect(viewModel.loadingState) {
        viewModel.handleLoadingState()
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
            Spacer(modifier = Modifier.width(12.dp))
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
                text = stringResource(R.string.registration),
                fontSize = 32.sp,
                modifier = Modifier
                    .padding(bottom = 10.dp, top = 10.dp)
            )
            UsernameInput(
                label = R.string.username,
                value = viewModel.username,
                onValueChanged = { viewModel.username = it },
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)
            )
            if (viewModel.isErrorsVisible) {
                if (viewModel.registrationResult?.errors?.name != null) {
                    ErrorDetails(error = viewModel.registrationResult?.errors?.name!![0].toString())
                }
            }
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
            if (viewModel.isErrorsVisible) {
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
            if (viewModel.isErrorsVisible) {
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
            RegistrationActivityButton(
                R.string.register, MaterialTheme.colorScheme.primaryContainer
            ) { viewModel.register() }

            ButtonDivider()

            RegistrationActivityButton(
                R.string.logInToYourAccount, MaterialTheme.colorScheme.tertiaryContainer
            ) { navController.navigate(Screen.LoginScreen.destination) }

            Spacer(modifier = Modifier.height(20.dp))

            if (viewModel.isProgressIndicatorVisible) {
                LinearIndicator()
            }
        }
    }
}