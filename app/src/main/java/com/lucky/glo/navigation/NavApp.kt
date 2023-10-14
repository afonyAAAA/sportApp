package com.lucky.glo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lucky.glo.screens.CasinosScreen
import com.lucky.glo.screens.DescriptionCasinoScreen
import com.lucky.glo.screens.MainScreen
import com.lucky.glo.screens.StartScreen
import com.lucky.glo.viewModels.MainViewModel

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