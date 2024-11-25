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
import com.example.triplog.travel.presentation.CreateTravelScreen

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
            route = "${Screen.ProfileScreen.destination}/{id}/{friendStatus}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("friendStatus") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            val friendStatus = backStackEntry.arguments?.getString("friendStatus")
            if (id != null) {
                ProfileScreen(navController = navController, id = id, friendStatus = friendStatus)
            }
        }
        composable(
            route = Screen.EditProfileScreen.destination
        ) {
            EditProfileScreen(navController = navController)
        }
        composable(route = Screen.MainPageScreen.destination) {
            MainPageScreen(navController = navController)
        }
        composable(route = Screen.CreateTravelScreen.destination) {
            CreateTravelScreen(navController = navController)
        }
    }
}