package com.example.triplog.main

import android.app.Application
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.triplog.network.RepositoryContainer
import com.example.triplog.network.TripLogRetrofitClient

class TripLogApplication : Application() {
    lateinit var container: RepositoryContainer
    lateinit var sessionManager: SessionManager
        private set

    override fun onCreate() {
        super.onCreate()
        container = TripLogRetrofitClient()

        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            "token_shared_pref",
            masterKey,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        sessionManager = SessionManager(sharedPreferences)
    }
}
