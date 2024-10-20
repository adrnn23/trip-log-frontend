package com.example.triplog.profile.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.triplog.main.TripLogApplication
import com.example.triplog.network.InterfaceRepository
import com.example.triplog.profile.data.LinkData

class ProfileViewModel(private val repository: InterfaceRepository) : ViewModel() {
    var username by mutableStateOf("username123")
    var bio by mutableStateOf("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam enim enim, hendrerit in mauris id, molestie blandit metus. Donec ultricies neque et dolor auctor, in condimentum eros consequat. Maecenas aliquet ornare dui, sit amet finibus velit molestie eu. Maecenas aliquet ornare dui, sit amet finibus velit molestie eu.")
    var tripsCount by mutableIntStateOf(20)
    var plannedCount by mutableIntStateOf(2)
    var travelPreferences =
        mutableListOf<String?>(
            "Mountains",
            "Lakes",
            "Bike trips",
            "Cultural travel"
        )
    var links = mutableListOf<LinkData?>(
        LinkData("Facebook", Icons.Default.Facebook, "https://www.facebook.com/"),
        LinkData("Instagram", Icons.Default.Link, "https://www.instagram.com/"),
        LinkData("X", Icons.Default.Link, "")
    )
    var email by mutableStateOf("username123@example.com")

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TripLogApplication)
                val repository = application.container.repository
                ProfileViewModel(repository = repository)
            }
        }
    }
}