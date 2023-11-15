package com.boundless.GIGABET.wonders.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.boundless.GIGABET.wonders.R
import com.boundless.GIGABET.wonders.event.UiEventSettingsPuzzle

@Composable
fun SettingsScreen(navHostController: NavHostController, puzzleViewModel: PuzzleViewModel){

    val state = puzzleViewModel.stateSettingsPuzzle

    Image(
        painter = painterResource(id = R.drawable.background),
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        contentDescription = ""
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(Modifier.fillMaxWidth().background(Color.Black.copy(0.5f))) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Puzzle pictures are open", color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(checked = state.imageIsVisible, onCheckedChange = {
                    puzzleViewModel.onEventSettingsPuzzle(UiEventSettingsPuzzle.VisibleStateImageChoose(it))
                })
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Assembling puzzles against time", color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(checked = state.timerIsOn, onCheckedChange = {
                    puzzleViewModel.onEventSettingsPuzzle(UiEventSettingsPuzzle.TimerStateImageChoose(it))
                })
            }

            Button(
                onClick = {
                    puzzleViewModel.onEventSettingsPuzzle(UiEventSettingsPuzzle.ResetCompletedPuzzle)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(text = "Reset completed puzzles")
            }

            Text(
                text = "Affects the visibility of puzzles if they have already been completed",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}