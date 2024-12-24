package com.example.triplog.main.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.triplog.main.presentation.SplashScreen
import com.example.triplog.authorization.login.presentation.LoginScreen
import com.example.triplog.authorization.registration.presentation.RegistrationScreen
import com.example.triplog.main.presentation.MainPageScreen
import com.example.triplog.profile.presentation.EditProfileScreen
import com.example.triplog.profile.presentation.ProfileScreen
import com.example.triplog.travel.presentation.MapScreen
import com.example.triplog.travel.presentation.SharedTravelViewModel
import com.example.triplog.travel.presentation.travelForm.TravelFormScreen
import com.example.triplog.travel.presentation.travelGallery.TravelGalleryScreen

sealed class Screen(val destination: String) {
    data object LoginScreen : Screen("LoginScreen")
    data object RegistrationScreen : Screen("RegistrationScreen")
    data object ProfileScreen : Screen("ProfileScreen")
    data object SplashScreen : Screen("SplashScreen")
    data object EditProfileScreen : Screen("EditProfileScreen")
    data object TravelFormScreen : Screen("TravelFormScreen")
    data object MainPageScreen : Screen("MainPageScreen")
    data object TravelGalleryScreen : Screen("TravelGalleryScreen")
    data object MapScreen : Screen("MapScreen")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sharedTravelViewModel: SharedTravelViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.SplashScreen.destination) {
        composable(route = Screen.SplashScreen.destination) {
            SplashScreen(navController = navController)
        }

        composable(route = Screen.LoginScreen.destination) {
            LoginScreen(navController = navController)
        }

        composable(route = Screen.RegistrationScreen.destination) {
            RegistrationScreen(navController = navController)
        }

        composable(
            route = "${Screen.ProfileScreen.destination}/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            ProfileScreen(
                navController = navController,
                id = id
            )
        }
        composable(
            route = Screen.EditProfileScreen.destination
        ) {
            EditProfileScreen(navController = navController)
        }
        composable(route = Screen.MainPageScreen.destination) {
            MainPageScreen(navController = navController, sharedTravelViewModel=sharedTravelViewModel)
        }
        composable(route = Screen.TravelFormScreen.destination) {
            TravelFormScreen(navController = navController, sharedTravelViewModel)
        }
        composable(route = Screen.TravelGalleryScreen.destination) {
            TravelGalleryScreen(navController = navController, sharedTravelViewModel)
        }
        composable(
            route = Screen.MapScreen.destination
        ) {
            MapScreen(
                sharedTravelViewModel = sharedTravelViewModel,
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}