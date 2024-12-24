package com.example.triplog.main

enum class ErrorType {
    VALIDATION,
    NETWORK,
    LINKS
}

data class ErrorState(
    val isError: Boolean = false,
    val type: ErrorType? = null,
    val description: String = ""
)