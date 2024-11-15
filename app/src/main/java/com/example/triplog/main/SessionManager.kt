package com.example.triplog.main

import android.content.SharedPreferences

class SessionManager(encryptedSharedPreferences: SharedPreferences) {

    private val sharedPreferences = encryptedSharedPreferences

    companion object {
        private const val TOKEN_KEY = "userToken"
        private const val USER_ID_KEY = "userId"
        private const val USER_EMAIL_KEY = "userEmail"
        private const val USER_NAME_KEY = "userName"
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? = sharedPreferences.getString(TOKEN_KEY, null)

    fun clearToken() {
        sharedPreferences.edit()
            .remove(TOKEN_KEY)
            .remove(USER_ID_KEY)
            .remove(USER_EMAIL_KEY)
            .remove(USER_NAME_KEY)
            .apply()
    }

    fun saveUserId(userId: Int) {
        sharedPreferences.edit().putInt(USER_ID_KEY, userId).apply()
    }

    fun getUserId(): Int? = if (sharedPreferences.contains(USER_ID_KEY)) {
        sharedPreferences.getInt(USER_ID_KEY, -1)
    } else null

    fun saveUserEmail(email: String) {
        sharedPreferences.edit().putString(USER_EMAIL_KEY, email).apply()
    }

    fun getUserEmail(): String? = sharedPreferences.getString(USER_EMAIL_KEY, null)

    fun saveUserName(name: String) {
        sharedPreferences.edit().putString(USER_NAME_KEY, name).apply()
    }

    fun getUserName(): String? = sharedPreferences.getString(USER_NAME_KEY, null)
}