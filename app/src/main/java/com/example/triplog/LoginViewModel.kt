package com.example.triplog

import android.app.Application
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
import com.example.triplog.data.LoginRequest
import com.example.triplog.data.LoginResult
import com.example.triplog.network.InterfaceRepository
import com.example.triplog.network.RepositoryContainer
import com.example.triplog.network.TripLogRetrofitClient
import kotlinx.coroutines.launch

class TripLogApplication : Application() {
    lateinit var container: RepositoryContainer
    override fun onCreate() {
        super.onCreate()
        container = TripLogRetrofitClient()
    }
}

enum class LoginState {
    NotLogged, Logged, Error
}

class LoginViewModel(private val repository: InterfaceRepository) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var loginState: LoginState by mutableStateOf(LoginState.NotLogged)

    var loginResult by mutableStateOf<LoginResult?>(null)

    private var loginRequest by mutableStateOf<LoginRequest?>(null)


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

    fun login(context: Context) {
/*        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, R.string.emptyFieldsInfo, Toast.LENGTH_SHORT).show()
            return
        }*/
        loginRequest = LoginRequest(email, password, "android")
        viewModelScope.launch {
            try {
                val result = repository.getLoginResult(loginRequest!!)
                if (result?.resultCode == 200 && result.token != null && result.user != null) {
                    loginResult = result
                    loginState = LoginState.Logged
                    email = loginResult?.token.toString()
                } else if ((result?.resultCode == 422 && result.message!!.isNotBlank()) && (result.errors?.email!!.isNotEmpty() || result.errors.password!!.isNotEmpty())) {
                    loginResult = result
                    loginState = LoginState.Error
                } else if (result?.resultCode == 401 && result.message!!.isNotBlank()) {
                    loginResult = result
                    loginState = LoginState.Error
                } else {
                    loginState = LoginState.NotLogged
                }
            } catch (e: Exception) {
                loginState = LoginState.Error
            }
        }


    }
}