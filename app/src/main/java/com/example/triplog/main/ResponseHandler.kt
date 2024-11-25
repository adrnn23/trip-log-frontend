package com.example.triplog.main

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class BackendResponse(
    val message: String? = null,
    val errors: List<String?>? = null
)

class ResponseHandler {
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> get() = _message

    private val _errors = MutableStateFlow<List<String?>?>(null)
    val errors: StateFlow<List<String?>?> get() = _errors

    fun showMessage(response: BackendResponse) {
        _message.value = response.message
        _errors.value = response.errors
    }

    fun clearMessage() {
        _message.value = null
        _errors.value = null
    }
}