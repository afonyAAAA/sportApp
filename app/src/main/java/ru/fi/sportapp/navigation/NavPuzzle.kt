package ru.fi.sportapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.fi.sportapp.screens.Main.MainScreen
import ru.fi.sportapp.screens.PuzzleScreen
import ru.fi.sportapp.screens.PuzzleViewModel
import ru.fi.sportapp.screens.PuzzlesScreen


sealed class Screens(val route : String){
    object Main : Screens("main_screen")
    object Puzzles : Screens("puzzles_screen")
    object Puzzle : Screens("puzzle_screen")
}


@Composable
fun NavPuzzle(puzzleViewModel : PuzzleViewModel){

    val navHostController = rememberNavController()

    NavHost(navController = navHostController, startDestination = Screens.Puzzle.route){
        composable(Screens.Main.route){
            MainScreen(navHostController = navHostController)
        }
        composable(Screens.Puzzles.route){
            PuzzlesScreen(navHostController = navHostController, viewModel = puzzleViewModel)
        }
        composable(Screens.Puzzle.route){
            PuzzleScreen(navHostController = navHostController, viewModel = puzzleViewModel)
        }
    }
}