package com.example.triplog

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Screen(val destination: String){
    data object LoginScreen : Screen("LoginScreen")
    data object RegistrationScreen : Screen("RegistrationScreen")
}

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.LoginScreen.destination) {
        composable(route = Screen.LoginScreen.destination){
            LoginScreen(navController = navController)
        }

        composable(route = Screen.RegistrationScreen.destination){
            RegistrationScreen(navController = navController)
        }
    }
}