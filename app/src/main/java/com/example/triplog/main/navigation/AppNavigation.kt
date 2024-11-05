package com.example.triplog.main.navigation

import androidx.compose.runtime.Composable
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

sealed class Screen(val destination: String) {
    data object LoginScreen : Screen("LoginScreen")
    data object RegistrationScreen : Screen("RegistrationScreen")
    data object ProfileScreen : Screen("ProfileScreen")
    data object SplashScreen : Screen("SplashScreen")
    data object EditProfileScreen : Screen("EditProfileScreen")
    data object CreateTravelScreen : Screen("CreateTravelScreen")
    data object MainPageScreen : Screen("MainPageScreen")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

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
            route = "${Screen.ProfileScreen.destination}/{token}/{email}/{id}",
            arguments = listOf(
                navArgument("token") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType },
                navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")
            val email = backStackEntry.arguments?.getString("email")
            val id = backStackEntry.arguments?.getInt("id")
            if (token != null && id != null && email != null) {
                ProfileScreen(navController = navController, token = token, id = id, email = email)
            }
        }
        composable(
            route = "${Screen.EditProfileScreen.destination}/{token}/{id}/{email}",
            arguments = listOf(
                navArgument("token") { type = NavType.StringType },
                navArgument("id") { type = NavType.IntType },
                navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")
            val id = backStackEntry.arguments?.getInt("id")
            val email = backStackEntry.arguments?.getString("email")
            if (token != null && id != null && email != null) {
                EditProfileScreen(
                    navController = navController,
                    token = token,
                    id = id,
                    email = email
                )
            }
        }
        composable(route = Screen.MainPageScreen.destination) {
            MainPageScreen(navController = navController)
        }
    }
}