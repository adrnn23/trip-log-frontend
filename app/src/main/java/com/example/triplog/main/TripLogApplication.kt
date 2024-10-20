package com.example.triplog.main

import android.app.Application
import com.example.triplog.network.RepositoryContainer
import com.example.triplog.network.TripLogRetrofitClient

class TripLogApplication : Application() {
    lateinit var container: RepositoryContainer
    override fun onCreate() {
        super.onCreate()
        container = TripLogRetrofitClient()
    }
}