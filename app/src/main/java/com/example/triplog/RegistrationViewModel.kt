package com.example.triplog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.triplog.data.RegistrationRequest
import com.example.triplog.data.RegistrationResult
import com.example.triplog.network.InterfaceRepository
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
    var registrationState: RegistrationState by mutableStateOf(RegistrationState.NotRegistered)
    var registrationResult by mutableStateOf<RegistrationResult?>(null)
    private var registrationRequest by mutableStateOf<RegistrationRequest?>(null)

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
        registrationRequest =
            RegistrationRequest(username, email, password, repeatedPassword, "android")
        viewModelScope.launch {
            try {
                val result = repository.getRegistrationResult(registrationRequest!!)
                if (result?.resultCode == 200 && result.token != null && result.user != null) {
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
        }
    }
}