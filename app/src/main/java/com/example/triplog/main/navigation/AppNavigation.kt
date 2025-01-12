package com.example.triplog.main.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.triplog.main.presentation.SplashScreen
import com.example.triplog.authorization.login.presentation.LoginScreen
import com.example.triplog.authorization.login.presentation.LoginViewModel
import com.example.triplog.authorization.registration.presentation.RegistrationScreen
import com.example.triplog.authorization.registration.presentation.RegistrationViewModel
import com.example.triplog.main.presentation.MainPageScreen
import com.example.triplog.main.presentation.MainPageViewModel
import com.example.triplog.profile.presentation.EditProfileScreen
import com.example.triplog.profile.presentation.EditProfileViewModel
import com.example.triplog.profile.presentation.ProfileScreen
import com.example.triplog.profile.presentation.ProfileViewModel
import com.example.triplog.travel.presentation.MapScreen
import com.example.triplog.travel.presentation.SearchMapScreen
import com.example.triplog.travel.presentation.SearchMapViewModel
import com.example.triplog.travel.presentation.SharedTravelViewModel
import com.example.triplog.travel.presentation.travelForm.TravelFormScreen
import com.example.triplog.travel.presentation.travelForm.TravelFormViewModel
import com.example.triplog.travel.presentation.travelGallery.TravelGalleryScreen
import com.example.triplog.travel.presentation.travelGallery.TravelGalleryViewModel

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
    data object SearchMapScreen : Screen("SearchMapScreen")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    sharedTravelViewModel: SharedTravelViewModel = viewModel(),
) {
    NavHost(navController = navController, startDestination = Screen.SplashScreen.destination) {
        composable(route = Screen.SplashScreen.destination) {
            SplashScreen(navController = navController)
        }

        composable(route = Screen.LoginScreen.destination) {
            val viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
            LoginScreen(viewModel = viewModel, navController = navController)
        }

        composable(route = Screen.RegistrationScreen.destination) {
            val viewModel: RegistrationViewModel =
                viewModel(factory = RegistrationViewModel.Factory)
            RegistrationScreen(viewModel = viewModel, navController = navController)
        }

        composable(
            route = "${Screen.ProfileScreen.destination}/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            val viewModel: ProfileViewModel =
                viewModel(factory = ProfileViewModel.provideFactory(id))
            ProfileScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable(
            route = Screen.EditProfileScreen.destination
        ) {
            val viewModel: EditProfileViewModel =
                viewModel(factory = EditProfileViewModel.provideFactory())
            EditProfileScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = Screen.MainPageScreen.destination) {
            val viewModel: MainPageViewModel = viewModel(factory = MainPageViewModel.factory)
            MainPageScreen(
                viewModel = viewModel,
                navController = navController,
                sharedTravelViewModel = sharedTravelViewModel
            )
        }
        composable(route = Screen.TravelFormScreen.destination) {
            val viewModel: TravelFormViewModel = viewModel(factory = TravelFormViewModel.Factory)
            TravelFormScreen(
                viewModel = viewModel,
                navController = navController,
                sharedTravelViewModel
            )
        }
        composable(route = "${Screen.TravelGalleryScreen.destination}/{id}",
                arguments = listOf(
                navArgument("id") { type = NavType.IntType }
                )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            val viewModel: TravelGalleryViewModel =
                viewModel(factory = TravelGalleryViewModel.provideFactory(id))
            TravelGalleryScreen(
                viewModel = viewModel,
                navController = navController,
                sharedTravelViewModel
            )
        }
        composable(
            route = Screen.MapScreen.destination
        ) {
            MapScreen(
                sharedTravelViewModel = sharedTravelViewModel,
                onBackPressed = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.SearchMapScreen.destination
        ) {
            val viewModel: SearchMapViewModel = viewModel(factory = SearchMapViewModel.Factory)
            SearchMapScreen(
                viewModel = viewModel,
                sharedTravelViewModel = sharedTravelViewModel,
                navController = navController
            )
        }
    }
}