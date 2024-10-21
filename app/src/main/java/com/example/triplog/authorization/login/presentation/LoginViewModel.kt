package com.example.triplog.authorization.login.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.triplog.main.TripLogApplication
import com.example.triplog.authorization.login.data.LoginRequest
import com.example.triplog.authorization.login.data.LoginResult
import com.example.triplog.network.InterfaceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class LoginState {
    NotLogged, Logged, Error, Unauthorized
}

enum class LoadingState {
    NotLoaded, Loading, Loaded
}


class LoginViewModel(private val repository: InterfaceRepository) : ViewModel() {

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
                LoginViewModel(repository = repository)
            }
        }
    }

    fun login() {
        loginRequest = LoginRequest(email, password, deviceName)
        loadingState = LoadingState.Loading
        viewModelScope.launch {
            try {
                delay(500)
                val result = repository.getLoginResult(loginRequest)
                if ((result?.resultCode == 200) && (result.token != null)) {
                    loginResult = result
                    loginState = LoginState.Logged
                } else if ((result?.resultCode == 422 && result.message != null) && (result.errors?.email != null || result.errors?.password != null)) {
                    loginResult = result
                    loginState = LoginState.Error
                } else if (result?.resultCode == 401 && result.message != null) {
                    loginResult = result
                    loginState = LoginState.Unauthorized
                } else {
                    loginState = LoginState.NotLogged
                }
            } catch (e: Exception) {
                loginState = LoginState.Error
            }
            loadingState = LoadingState.Loaded
        }
    }
}