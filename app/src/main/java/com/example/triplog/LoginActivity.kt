package com.example.triplog

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.triplog.data.LoginResult
import com.example.triplog.ui.theme.TripLogTheme
import com.example.triplog.ui.theme.errorContainerDark
import com.example.triplog.ui.theme.onErrorContainerDark


/*fun loginValidation(
    email: String, password: String, context: Context,
): Boolean {
    if (email == "" || password == "") {
        Toast.makeText(context, R.string.emptyFieldsInfo, Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}*/

@Composable
fun LoginScreen(navController: NavController) {
    val context: Context = LocalContext.current

    val viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.loginState) {
        if (viewModel.loginState == LoginState.Error) {
            if (viewModel.loginResult != null) {
                showDialog = true
            }
        }
    }

    if (showDialog) {
        LoginErrorDialog(
            viewModel.loginResult
        ) {
            showDialog = false
            viewModel.loginState = LoginState.NotLogged
        }
    }

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
            PasswordInput(
                label = R.string.password,
                value = viewModel.password,
                onValueChanged = { viewModel.password = it },
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
            LoginButton(
                email = viewModel.email,
                password = viewModel.password,
                onLoginClick = { viewModel.login(context) }
            )

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color.DarkGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            NoAccount(navController)
        }

    }
}

@Composable
fun LoginErrorDialog(loginResult: LoginResult?, onErrorDialogClick: () -> Unit) {
    Dialog(
        onDismissRequest = { onErrorDialogClick() },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(color = Color.Black, width = 1.dp, shape = RoundedCornerShape(10.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Text(
                        text = loginResult?.message.toString(),
                        fontSize = 22.sp,
                        color = onErrorContainerDark
                    )
                    Spacer(Modifier.height(10.dp))
                    if (loginResult?.resultCode == 422 && loginResult.errors != null) {

                        if (loginResult.errors.email != null) {
                            LazyColumn {
                                items(loginResult.errors.email) { error ->
                                    error?.let { ErrorDetails(it) }
                                }
                            }
                        }
                        if (loginResult.errors.password != null) {
                            LazyColumn {
                                items(loginResult.errors.password) { error ->
                                    error?.let { ErrorDetails(it) }
                                }
                            }
                        }
                    } else if (loginResult?.resultCode == 401) {
                        ErrorDetails(loginResult.message.toString())
                    }
                }
                Button(
                    onClick = { onErrorDialogClick() },
                    colors = ButtonDefaults.buttonColors(errorContainerDark)
                ) {
                    Text(
                        text = stringResource(id = R.string.ok),
                        color = onErrorContainerDark,
                        fontSize = 14.sp
                    )
                }
            }
        }

    }
}

@Composable
fun ErrorDetails(error: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun LoginButton(email: String, password: String, onLoginClick: () -> Unit) {
    val context: Context = LocalContext.current

    Button(
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = {
/*            if (loginValidation(email, password, context)) {
                onLoginClick()
            }*/
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
        val str = "Don\'t have an account? Sign up!"
        val startStr = str.indexOf("Sign")
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

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TripLogTheme {
        val navController = rememberNavController()
        LoginScreen(navController = navController)
    }
}
