package com.boundless.GIGABET.wonders.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.boundless.GIGABET.wonders.models.NavhostValue
import com.boundless.GIGABET.wonders.screens.MainScreen
import com.boundless.GIGABET.wonders.screens.assemblyPuzzle.PuzzleScreen
import com.boundless.GIGABET.wonders.screens.listPuzzle.PuzzlesScreen
import com.boundless.GIGABET.wonders.screens.settings.SettingsScreen


sealed class Screens(val route : String){
    object Main : Screens("main_screen")
    object ListPuzzles : Screens("puzzles_screen")
    object AssemblyPuzzle : Screens("puzzle_screen")
    object Settings : Screens("settings_screen")
}


@Composable
fun NavPuzzle(){

    val navHostControllerValue =
        NavhostValue(
            navHostController = rememberNavController()
        )

    NavHost(
        navController = navHostControllerValue.navHostController,
        startDestination = Screens.Main.route,
        modifier = Modifier
            .fillMaxSize()
    ){
        composable(Screens.Main.route){
            MainScreen(navHostController = navHostControllerValue)
        }
        composable(Screens.ListPuzzles.route){
            PuzzlesScreen(navHostController = navHostControllerValue)
        }
        composable(Screens.AssemblyPuzzle.route){
            PuzzleScreen(navHostController = navHostControllerValue)
        }
        composable(Screens.Settings.route){
            SettingsScreen(navHostController = navHostControllerValue)
        }
    }
}