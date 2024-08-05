package com.example.triplog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                color = MaterialTheme.colorScheme.onSecondary,
                text = stringResource(R.string.app_name),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 20.dp, top = 60.dp)
            )
            Text(
                color = MaterialTheme.colorScheme.onSecondary,
                text = stringResource(R.string.logowanie),
                fontSize = 32.sp,
                modifier = Modifier
                    .padding(bottom = 10.dp, top = 120.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(10.dp)
        ) {

            UsernameEmailInput(
                label = R.string.email,
                value = email,
                onValueChanged = { email = it },
                modifier = Modifier.padding(5.dp)
            )
            PasswordInput(
                label = R.string.password,
                value = password,
                onValueChanged = { password = it },
                modifier = Modifier.padding(5.dp)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(10.dp)
        ) {
            LoginButton()
            Spacer(modifier = Modifier.height(30.dp))
            NoAccount(navController)
        }

    }
}

@Composable
fun NoAccount(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            color = MaterialTheme.colorScheme.onSecondary,
            text = stringResource(id = R.string.noAccount),
            fontSize = 16.sp,
            modifier = Modifier
                .padding(bottom = 10.dp, top = 10.dp)
        )
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            onClick = { navController.navigate(Screen.RegistrationScreen.destination) }
        ) {
            Text(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                text = stringResource(R.string.register),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(2.dp)
            )
        }
    }
}

@Composable
fun LoginButton() {
    Button(
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = { /*TODO*/ }
    ) {
        Text(
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            text = stringResource(R.string.login),
            fontSize = 14.sp,
            modifier = Modifier
                .padding(2.dp)
        )
    }
}
