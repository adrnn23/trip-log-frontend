package com.example.triplog.profile.data

enum class ErrorType {
    Username, Email, Biography, TravelPreferences, Links
}

data class ErrorData(var isError: Boolean, var type: ErrorType?, var description: String?)