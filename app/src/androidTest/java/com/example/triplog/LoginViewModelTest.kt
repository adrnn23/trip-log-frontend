package com.example.triplog

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.triplog.authorization.login.data.LoginRequest
import com.example.triplog.authorization.login.data.LoginResult
import com.example.triplog.authorization.login.presentation.LoginViewModel
import com.example.triplog.main.SessionManager
import com.example.triplog.network.Repository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginViewModelTest {
    @Test
    fun loginViewModel_callsRepositoryWithCorrectData() = runTest {
        val repository = mockk<Repository>()
        val sessionManager = mockk<SessionManager>(relaxed = true)
        val viewModel = LoginViewModel(repository, sessionManager)

        val loginRequest = LoginRequest("test@example.com", "password", "android")
        viewModel.email = loginRequest.email
        viewModel.password = loginRequest.password

        coEvery { repository.getLoginResult(loginRequest) } returns LoginResult(
            resultCode = 200,
            token = "testToken",
            message = "testMessage",
            errors = null
        )

        viewModel.login()

        coVerify { repository.getLoginResult(loginRequest) }
    }
}