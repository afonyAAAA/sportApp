package ru.fi.sportapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.fi.sportapp.screens.MainScreen
import ru.fi.sportapp.screens.StartScreen

sealed class Screens(val route : String){
    object Main : Screens("main_screen")
    object Casinos : Screens("casinos_screen")
    object Start : Screens("start_screen")
    object DescriptionCasino : Screens("description_casino_screen")
}

@Composable
fun NavApp(startDestination: String, navHostController: NavHostController){

    NavHost(navController = navHostController, startDestination = startDestination){
        composable(
            route = Screens.Start.route,
        ){
            StartScreen(navHostController = navHostController)
        }
        composable(Screens.Main.route){
            MainScreen(navHostController = navHostController)
        }
        composable(Screens.DescriptionCasino.route){

        }
    }
}