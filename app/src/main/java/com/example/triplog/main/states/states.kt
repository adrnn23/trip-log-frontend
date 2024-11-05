package com.example.triplog.main.states

sealed class LoadingState {
    data object Loading : LoadingState()
    data object Loaded : LoadingState()
    data object NotLoaded : LoadingState()
}