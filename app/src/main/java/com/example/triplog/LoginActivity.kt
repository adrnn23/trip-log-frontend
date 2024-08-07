package com.example.triplog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.triplog.ui.theme.TripLogTheme

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
                text = stringResource(R.string.app_name),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 20.dp, top = 60.dp)
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
                text = stringResource(R.string.logowanie),
                fontSize = 32.sp,
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp)
            )

            EmailInput(
                label = R.string.email,
                value = email,
                onValueChanged = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)
            )
            PasswordInput(
                label = R.string.password,
                value = password,
                onValueChanged = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(10.dp)
        ) {
            LoginButton()

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color.DarkGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            NoAccount(navController)
        }

    }
}

@Composable
fun NoAccount(navController: NavController) {

    val annotatedString = buildAnnotatedString {
        val str = "Nie masz konta? Zarejestruj się!"
        val startStr = str.indexOf("Zarejestruj")
        val endStr = str.indexOf("!")

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

@Composable
fun LoginButton() {
    Button(
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = { /*TODO*/ },
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

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TripLogTheme {
        val navController = rememberNavController()
        LoginScreen(navController = navController)
    }
}
