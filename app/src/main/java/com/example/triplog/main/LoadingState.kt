package com.example.triplog.main

sealed class LoadingState {
    data object Loaded : LoadingState()
    data object Loading : LoadingState()
    data object NotLoaded : LoadingState()
}