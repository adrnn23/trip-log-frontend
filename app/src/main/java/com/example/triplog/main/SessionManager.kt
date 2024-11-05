package com.example.triplog.main

import android.content.SharedPreferences

class SessionManager(encryptedSharedPreferences: SharedPreferences) {

    private val sharedPreferences = encryptedSharedPreferences

    companion object {
        private const val TOKEN_KEY = "userToken"
    }

    fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString(TOKEN_KEY, token)
            .apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun clearToken() {
        sharedPreferences.edit()
            .remove(TOKEN_KEY)
            .apply()
    }
}
