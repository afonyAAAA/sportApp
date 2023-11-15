package com.boundless.GIGABET.wonders.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.boundless.GIGABET.wonders.R
import com.boundless.GIGABET.wonders.screens.MainScreen
import com.boundless.GIGABET.wonders.screens.PuzzleScreen
import com.boundless.GIGABET.wonders.screens.PuzzleViewModel
import com.boundless.GIGABET.wonders.screens.PuzzlesScreen
import com.boundless.GIGABET.wonders.screens.SettingsScreen


sealed class Screens(val route : String){
    object Main : Screens("main_screen")
    object Puzzles : Screens("puzzles_screen")
    object Puzzle : Screens("puzzle_screen")
    object Settings : Screens("settings_screen")
}


@Composable
fun NavPuzzle(puzzleViewModel: PuzzleViewModel){
//    val context = LocalContext.current
//    val puzzleViewModel : PuzzleViewModel = PuzzleViewModel(context)
    val navHostController = rememberNavController()
    NavHost(
        navController = navHostController,
        startDestination = Screens.Main.route,
        modifier = Modifier
            .fillMaxSize()
    ){
        composable(Screens.Main.route){
            MainScreen(navHostController = navHostController)
        }
        composable(Screens.Puzzles.route){
            PuzzlesScreen(navHostController = navHostController, viewModel = puzzleViewModel)
        }
        composable(Screens.Puzzle.route){
            PuzzleScreen(navHostController = navHostController, viewModel = puzzleViewModel)
        }
        composable(Screens.Settings.route){
            SettingsScreen(navHostController = navHostController, puzzleViewModel = puzzleViewModel)
        }
    }
}