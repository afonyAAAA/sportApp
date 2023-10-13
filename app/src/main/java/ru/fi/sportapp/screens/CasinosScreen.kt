package ru.fi.sportapp.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import ru.fi.sportapp.Helper
import ru.fi.sportapp.navigation.Screens
import ru.fi.sportapp.viewModels.MainViewModel

@Composable
fun CasinosScreen(navHostController: NavHostController, mainViewModel: MainViewModel){
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        items(mainViewModel.casinos){ casino ->
            CardCasino(casino){
                Helper.selectedCasino = casino
                navHostController.navigate(Screens.DescriptionCasino.route)
            }
        }
    }
}