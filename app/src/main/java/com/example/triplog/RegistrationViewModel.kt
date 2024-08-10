package com.example.triplog

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.triplog.data.LoginRegistrationResult
import com.example.triplog.data.RegistrationRequest
import com.example.triplog.data.RegistrationResult
import com.example.triplog.network.InterfaceRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
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

    var registrationRequest by mutableStateOf<RegistrationRequest?>(null)

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

    fun register(context: Context) {
        if (username.isBlank() || email.isBlank() || password.isBlank() || repeatedPassword.isBlank()) {
            Toast.makeText(context, "Uzupełnij wymagane pola.", Toast.LENGTH_SHORT).show()
            return
        }
        registrationRequest =
            RegistrationRequest(username, email, password, repeatedPassword, "android")
        viewModelScope.launch {
            try {
                registrationResult = repository.getRegistrationResult(registrationRequest!!)
                registrationState = RegistrationState.Registered
            } catch (e: IOException) {
                registrationState = RegistrationState.NotRegistered
                Toast.makeText(context, "Błąd sieci", Toast.LENGTH_SHORT).show()
            } catch (e: HttpException) {
                registrationState = RegistrationState.NotRegistered
                Toast.makeText(context, "Błąd serwera", Toast.LENGTH_SHORT).show()
            }
        }
    }
}