package com.example.triplog

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.triplog.ui.theme.TripLogTheme


fun registrationValidation(
    username: String, email: String,
    password: String, repeatedPassword: String, context:Context
): Boolean {
    if (username == "" || email == "" || password == "" || repeatedPassword == "") {
        Toast.makeText(context, "Uzupełnij wymagane pola.", Toast.LENGTH_SHORT).show()
        return true
    }
    Toast.makeText(context, "Rejestracja...", Toast.LENGTH_SHORT).show()
    return false
}


@Composable
fun RegistrationScreen(navController: NavController) {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatedPassword by remember { mutableStateOf("") }

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
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = stringResource(R.string.rejestracja),
                fontSize = 32.sp,
                modifier = Modifier
                    .padding(bottom = 10.dp, top = 10.dp)
            )
            UsernameInput(
                label = R.string.username,
                value = username,
                onValueChanged = { username = it },
                modifier = Modifier
                    .fillMaxWidth()
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
            PasswordInput(
                label = R.string.repeatedPassword,
                value = repeatedPassword,
                onValueChanged = { repeatedPassword = it },
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
            RegisterButton(username, email, password, repeatedPassword)

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color.DarkGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            BackToLoginButton(navController)
        }

    }
}

@Composable
fun RegisterButton(username: String, email: String, password: String, repeatedPassword: String) {
    var emptyRegistrationFields by remember { mutableStateOf(false) }
    val context: Context = LocalContext.current
    Button(
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = {
            emptyRegistrationFields = registrationValidation(username, email, password, repeatedPassword, context)
        },
        modifier = Modifier.width(240.dp)
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
fun BackToLoginButton(navController: NavController) {
    Button(
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = { navController.navigate(Screen.LoginScreen.destination) },
        modifier = Modifier
            .width(240.dp)
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

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    TripLogTheme {
        val navController = rememberNavController()
        RegistrationScreen(navController = navController)
    }
}