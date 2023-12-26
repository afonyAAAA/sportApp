package com.boundless.GIGABET.wonders.states

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.boundless.GIGABET.wonders.models.Image
import com.boundless.GIGABET.wonders.models.PuzzlePiece
import com.boundless.GIGABET.wonders.models.SnapZone

@Stable
data class StateAssemblyPuzzle(
    val selectedPiecesPuzzle: MutableList<PuzzlePiece> = mutableListOf(),
    val positionsPiecePuzzles: MutableList<PuzzlePiece> = mutableListOf(),
    val piecesPuzzle: MutableList<PuzzlePiece> = mutableListOf(),
    val snapZones: MutableList<SnapZone> = mutableListOf(),
    val snapThreshold: Dp = 50.dp,
    val timerIsRunning: Boolean = true,
    val totalTime: Int = 300,
    val isDefeat: Boolean = false,
    val isVictory: Boolean = false,
    val lastOffset: Offset = Offset(0f, 0f),
    val selectedPuzzle: Image = Image(),
    val counter: Int = 0,
    val offSetPuzzle : Offset = Offset(0F, 0F)
)
