package com.example.triplog

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Screen(val destination: String) {
    data object LoginScreen : Screen("LoginScreen")
    data object RegistrationScreen : Screen("RegistrationScreen")
    data object UserProfileScreen : Screen("UserProfileScreen")
    data object SplashScreen : Screen ("SplashScreen")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.SplashScreen.destination) {
        composable(route=Screen.SplashScreen.destination){
            SplashScreen(navController = navController)
        }
        
        composable(route = Screen.LoginScreen.destination) {
            LoginScreen(navController = navController)
        }

        composable(route = Screen.RegistrationScreen.destination) {
            RegistrationScreen(navController = navController)
        }

        composable(route = Screen.UserProfileScreen.destination) {
            UserProfileScreen()
        }
    }
}