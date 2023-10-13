package ru.fi.sportapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.fi.sportapp.screens.CasinosScreen
import ru.fi.sportapp.screens.DescriptionCasinoScreen
import ru.fi.sportapp.screens.MainScreen
import ru.fi.sportapp.screens.StartScreen
import ru.fi.sportapp.viewModels.MainViewModel

sealed class Screens(val route : String){
    object Main : Screens("main_screen")
    object Casinos : Screens("casinos_screen")
    object Start : Screens("start_screen")
    object DescriptionCasino : Screens("description_casino_screen")
}

@Composable
fun NavApp(
    startDestination: String,
    navHostController: NavHostController,
    mainViewModel: MainViewModel
){
    NavHost(navController = navHostController, startDestination = startDestination){
        composable(
            route = Screens.Start.route
        ){
            StartScreen(navHostController = navHostController, mainViewModel)
        }
        composable(Screens.Main.route){
            MainScreen(navHostController = navHostController, mainViewModel)
        }
        composable(Screens.DescriptionCasino.route){
            DescriptionCasinoScreen(navHostController = navHostController)
        }
        composable(Screens.Casinos.route){
            CasinosScreen(navHostController = navHostController, mainViewModel)
        }
    }
}