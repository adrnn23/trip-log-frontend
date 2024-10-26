package com.example.triplog.travel.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.triplog.main.TripLogApplication
import com.example.triplog.network.InterfaceRepository

class CreateTravelViewModel(private val repository: InterfaceRepository) : ViewModel() {

    var travelName by mutableStateOf("")

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                CreateTravelViewModel(repository = repository)
            }
        }
    }
}