package com.example.triplog.authorization.registration.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.example.triplog.main.TripLogApplication
import com.example.triplog.authorization.registration.data.RegistrationRequest
import com.example.triplog.authorization.registration.data.RegistrationResult
import com.example.triplog.main.LoadingState
import com.example.triplog.main.SessionManager
import com.example.triplog.main.navigation.Screen
import com.example.triplog.network.InterfaceRepository
import kotlinx.coroutines.launch
import java.io.IOException

sealed class RegistrationState {
    data object NotRegistered : RegistrationState()
    data object Registered : RegistrationState()
    data object Error : RegistrationState()
}

class RegistrationViewModel(
    private val repository: InterfaceRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    var token by mutableStateOf("")
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var repeatedPassword by mutableStateOf("")
    private val deviceName by mutableStateOf("android")

    var registrationState: RegistrationState by mutableStateOf(RegistrationState.NotRegistered)
    var loadingState: LoadingState by mutableStateOf(LoadingState.NotLoaded)

    var registrationResult by mutableStateOf<RegistrationResult?>(null)
    private var registrationRequest by mutableStateOf(
        RegistrationRequest(
            username,
            email,
            password,
            repeatedPassword,
            deviceName
        )
    )

    var isErrorsVisible by mutableStateOf(false)
    var isProgressIndicatorVisible by mutableStateOf(false)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                val sessionManager = application.sessionManager
                RegistrationViewModel(repository = repository, sessionManager = sessionManager)
            }
        }
    }

    fun register() {
        loadingState = LoadingState.Loading
        registrationRequest =
            RegistrationRequest(username, email, password, repeatedPassword, deviceName)
        viewModelScope.launch {
            try {
                val result = repository.getRegistrationResult(registrationRequest)
                processRegistrationResult(result)
            } catch (e: IOException) {
                registrationState = RegistrationState.Error
            } finally {
                loadingState = LoadingState.Loaded
            }
        }
    }

    fun handleRegistrationState(navController: NavController) {
        when (registrationState) {
            RegistrationState.Error -> {
                if (registrationResult?.resultCode == 422)
                    isErrorsVisible = true
            }

            RegistrationState.Registered -> {
                if (registrationResult?.resultCode == 201 && registrationResult != null) {
                    isErrorsVisible = false
                    navController.popBackStack()
                    navController.navigate(Screen.MainPageScreen.destination)
                }
            }

            RegistrationState.NotRegistered -> {}
        }
    }

    fun handleLoadingState() {
        isProgressIndicatorVisible =
            loadingState == LoadingState.Loading && registrationState != RegistrationState.Registered
    }

    private fun processRegistrationResult(result: RegistrationResult?) {
        registrationResult = result
        when (registrationResult?.resultCode) {
            201 -> {
                if (registrationResult?.token != null) {
                    token = registrationResult?.token ?: ""
                    sessionManager.saveToken(token)
                    registrationState = RegistrationState.Registered
                }
            }

            422 -> {
                if (registrationResult?.message != null)
                    registrationState = RegistrationState.Error
            }

            else -> registrationState = RegistrationState.NotRegistered
        }
    }
}