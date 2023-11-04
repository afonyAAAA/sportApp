package ru.fi.sportapp.screens

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.fi.sportapp.models.PuzzlePiece
import ru.fi.sportapp.models.SnapZone
import java.util.Timer

@Stable
data class StateAssemblyPuzzle(
    val offsetXpiece : Float = 0f,
    val offsetYpiece : Float = 0f,
    val isDragPiecePuzzle : Boolean = false,
    val selectedPiecePuzzle : PuzzlePiece = PuzzlePiece(),
    val piecesPuzzle : MutableList<PuzzlePiece> = mutableListOf(),
    val positionsPiecePuzzles : MutableList<PuzzlePiece> = mutableListOf(),
    val snapZones : MutableList<SnapZone> = mutableListOf(),
    val snapThreshold : Dp = 50.dp,
    val timerIsRunning : Boolean = true,
    val totalTime : Int = 300
)
