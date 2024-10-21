package com.example.triplog.authorization.registration.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.triplog.main.TripLogApplication
import com.example.triplog.authorization.login.presentation.LoadingState
import com.example.triplog.authorization.registration.data.RegistrationRequest
import com.example.triplog.authorization.registration.data.RegistrationResult
import com.example.triplog.network.InterfaceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException


enum class RegistrationState {
    NotRegistered, Registered, Error
}

class RegistrationViewModel(private val repository: InterfaceRepository) : ViewModel() {
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var repeatedPassword by mutableStateOf("")
    private val deviceName by mutableStateOf("android")

    var registrationState: RegistrationState by mutableStateOf(RegistrationState.NotRegistered)
    var loadingState: LoadingState by mutableStateOf(LoadingState.NotLoaded)

    var registrationResult by mutableStateOf<RegistrationResult?>(null)
    private var registrationRequest by mutableStateOf(RegistrationRequest(username, email, password, repeatedPassword, deviceName))

    var isErrorsVisible by mutableStateOf(false)
    var isProgressIndicatorVisible by mutableStateOf(false)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                RegistrationViewModel(repository = repository)
            }
        }
    }

    fun register() {
        registrationRequest = RegistrationRequest(username, email, password, repeatedPassword, deviceName)
        loadingState = LoadingState.Loading
        viewModelScope.launch {
            try {
                delay(500)
                val result = repository.getRegistrationResult(registrationRequest)
                if (result?.resultCode == 201 && result.token != null) {
                    registrationResult = result
                    registrationState = RegistrationState.Registered
                } else if ((result?.resultCode == 422 && result.message != null) && (result.errors?.email != null || result.errors?.name != null || result.errors?.password != null)) {
                    registrationResult = result
                    registrationState = RegistrationState.Error
                } else {
                    registrationState = RegistrationState.NotRegistered
                }
            } catch (e: IOException) {
                registrationState = RegistrationState.Error
            }
            loadingState = LoadingState.Loaded
        }
    }
}