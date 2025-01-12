package com.example.triplog

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.triplog.authorization.registration.presentation.RegistrationScreen
import com.example.triplog.authorization.registration.presentation.RegistrationViewModel
import com.example.triplog.main.navigation.Screen
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistrationScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun registrationScreen_displaysAllMainComponents() {
        val navController = mockk<NavController>(relaxed = true)
        val viewModel = mockk<RegistrationViewModel>(relaxed = true)

        composeTestRule.setContent {
            RegistrationScreen(viewModel = viewModel, navController = navController)
        }

        composeTestRule.onNodeWithTag("registrationText").assertExists()
        composeTestRule.onNodeWithTag("usernameInput").assertExists()
        composeTestRule.onNodeWithTag("emailInput").assertExists()
        composeTestRule.onNodeWithTag("passwordInput1").assertExists()
        composeTestRule.onNodeWithTag("passwordInput2").assertExists()
        composeTestRule.onNodeWithTag("registerButton").assertExists()
        composeTestRule.onNodeWithTag("loginButton").assertExists()
    }

    @Test
    fun registrationScreen_registrationButtonTriggersRegistrationAction() {
        val navController = mockk<NavController>(relaxed = true)
        val viewModel = mockk<RegistrationViewModel>(relaxed = true)

        composeTestRule.setContent {
            RegistrationScreen(navController = navController, viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("registerButton").performClick()

        verify { viewModel.register() }
    }

    @Test
    fun registrationScreen_navigatesToLoginScreen() {
        val navController = mockk<NavController>(relaxed = true)
        val viewModel = mockk<RegistrationViewModel>(relaxed = true)

        composeTestRule.setContent {
            RegistrationScreen(viewModel = viewModel, navController = navController)
        }
        composeTestRule.onNodeWithTag("loginButton").performClick()

        verify { navController.navigate(Screen.LoginScreen.destination) }
    }

    @Test
    fun registrationScreen_inputsUpdateViewModel() {
        val navController = mockk<NavController>(relaxed = true)
        val viewModel = mockk<RegistrationViewModel>(relaxed = true).apply {
            every { username } returns ""
            every { email } returns ""
            every { password } returns ""
            every { repeatedPassword } returns ""
        }

        composeTestRule.setContent {
            RegistrationScreen(viewModel = viewModel, navController = navController)
        }

        val testUsername = "testUser"
        val testEmail = "test@example.com"
        val testPassword = "password123"

        composeTestRule.onNodeWithTag("usernameInput").performTextInput(testUsername)
        verify { viewModel.username = testUsername }

        composeTestRule.onNodeWithTag("emailInput").performTextInput(testEmail)
        verify { viewModel.email = testEmail }

        composeTestRule.onNodeWithTag("passwordInput1").performTextInput(testPassword)
        verify { viewModel.password = testPassword }

        composeTestRule.onNodeWithTag("passwordInput2").performTextInput(testPassword)
        verify { viewModel.repeatedPassword = testPassword }
    }
}