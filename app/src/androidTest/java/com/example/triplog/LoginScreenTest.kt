package com.example.triplog

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.triplog.authorization.login.components.LoginActivityButton
import com.example.triplog.authorization.login.presentation.LoginScreen
import com.example.triplog.authorization.login.presentation.LoginViewModel
import com.example.triplog.main.navigation.Screen
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysAllMainComponents() {
        val viewModel = mockk<LoginViewModel>(relaxed = true)
        val navController = mockk<NavController>(relaxed = true)

        composeTestRule.setContent {
            LoginScreen(navController = navController, viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("loginButton").assertExists()
        composeTestRule.onNodeWithTag("createAccountButton").assertExists()
        composeTestRule.onNodeWithTag("passwordInput").assertExists()
        composeTestRule.onNodeWithTag("emailInput").assertExists()
        composeTestRule.onNodeWithTag("loginText").assertExists()
    }

    @Test
    fun loginScreen_loginButtonTriggersLoginAction() {
        val navController = mockk<NavController>(relaxed = true)
        val viewModel = mockk<LoginViewModel>(relaxed = true)

        composeTestRule.setContent {
            LoginScreen(navController = navController, viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("loginButton").performClick()

        verify { viewModel.login() }
    }


    @Test
    fun loginActivityButton_displaysCorrectText() {
        composeTestRule.setContent {
            LoginActivityButton(
                text = R.string.createNewAccount,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                onClick = {}, modifier = Modifier
            )
        }

        composeTestRule.onNodeWithText("Create new account").assertExists()
    }

    @Test
    fun loginScreen_navigatesToRegistrationScreen() {
        val viewModel = mockk<LoginViewModel>(relaxed = true)
        val navController = mockk<NavController>(relaxed = true)

        composeTestRule.setContent {
            LoginScreen(navController = navController, viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("createAccountButton").performClick()

        verify { navController.navigate(Screen.RegistrationScreen.destination) }
    }

    @Test
    fun loginScreen_inputsUpdateViewModel() {
        val navController = mockk<NavController>(relaxed = true)
        val viewModel = mockk<LoginViewModel>(relaxed = true).apply {
            every { email } returns ""
            every { password } returns ""
        }

        composeTestRule.setContent {
            LoginScreen(viewModel = viewModel, navController = navController)
        }

        val testEmail = "test@example.com"
        val testPassword = "password123"


        composeTestRule.onNodeWithTag("emailInput").performTextInput(testEmail)
        verify { viewModel.email = testEmail }

        composeTestRule.onNodeWithTag("passwordInput").performTextInput(testPassword)
        verify { viewModel.password = testPassword }

    }

}