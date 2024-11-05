package com.example.triplog.authorization.login.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.example.triplog.authorization.login.data.LoginRequest
import com.example.triplog.authorization.login.data.LoginResult
import com.example.triplog.main.SessionManager
import com.example.triplog.main.TripLogApplication
import com.example.triplog.main.navigation.Screen
import com.example.triplog.main.states.LoadingState
import com.example.triplog.network.InterfaceRepository
import kotlinx.coroutines.launch

sealed class LoginState {
    data object NotLogged : LoginState()
    data object Logged : LoginState()
    data object Error : LoginState()
    data object Unauthorized : LoginState()
}

class LoginViewModel(
    private val repository: InterfaceRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    var token by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    private val deviceName by mutableStateOf("android")

    var loginState: LoginState by mutableStateOf(LoginState.NotLogged)
    var loadingState: LoadingState by mutableStateOf(LoadingState.NotLoaded)

    var loginResult by mutableStateOf<LoginResult?>(null)
    private var loginRequest by mutableStateOf(LoginRequest(email, password, deviceName))

    var isUnauthorizedDialogVisible by mutableStateOf(false)
    var isErrorsVisible by mutableStateOf(false)
    var isProgressIndicatorVisible by mutableStateOf(false)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                val sessionManager = application.sessionManager
                LoginViewModel(repository = repository, sessionManager = sessionManager)
            }
        }
    }

    fun login() {
        loadingState = LoadingState.Loading
        loginRequest = LoginRequest(email, password, deviceName)
        viewModelScope.launch {
            try {
                val result = repository.getLoginResult(loginRequest)
                processLoginResult(result)
            } catch (e: Exception) {
                loginState = LoginState.Error
            } finally {
                loadingState = LoadingState.Loaded
            }
        }
    }

    fun handleLoginState(navController: NavController) {
        when (loginState) {
            LoginState.Error -> {
                if (loginResult?.resultCode == 422) {
                    isErrorsVisible = true
                    isUnauthorizedDialogVisible = false
                }
            }

            LoginState.Unauthorized -> {
                if (loginResult?.resultCode == 401) {
                    isErrorsVisible = false
                    isUnauthorizedDialogVisible = true
                }
            }

            LoginState.Logged -> {
                if (loginResult?.resultCode == 200 && loginResult != null) {
                    isErrorsVisible = false
                    isUnauthorizedDialogVisible = false
                    navController.popBackStack()
                    navController.navigate(Screen.MainPageScreen.destination)
                }
            }

            LoginState.NotLogged -> {}
        }
    }

    private fun processLoginResult(result: LoginResult?) {
        loginResult = result
        when (loginResult?.resultCode) {
            200 -> {
                if (loginResult?.token != null) {
                    token = loginResult?.token ?: ""
                    sessionManager.saveToken(token)
                    loginState = LoginState.Logged
                }
            }

            401 -> {
                if (loginResult?.message != null)
                    loginState = LoginState.Unauthorized
            }

            422 -> {
                if (loginResult?.message != null)
                    loginState = LoginState.Error
            }

            else -> loginState = LoginState.NotLogged
        }
    }

    fun handleLoadingState() {
        isProgressIndicatorVisible =
            loadingState == LoadingState.Loading && loginState != LoginState.Logged
    }
}