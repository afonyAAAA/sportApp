package ru.fi.sportapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.fi.sportapp.screens.MainScreen
import ru.fi.sportapp.screens.PuzzleScreen
import ru.fi.sportapp.screens.PuzzleViewModel
import ru.fi.sportapp.screens.PuzzlesScreen
import ru.fi.sportapp.screens.SettingsScreen


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