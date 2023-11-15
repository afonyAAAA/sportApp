package com.boundless.GIGABET.wonders.screens

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.boundless.GIGABET.wonders.models.PuzzlePiece
import com.boundless.GIGABET.wonders.models.SnapZone

@Stable
data class StateAssemblyPuzzle(
    val offsetXpiece: Float = 0f,
    val offsetYpiece: Float = 0f,
    val isDragPiecePuzzle: Boolean = false,
    val selectedPiecePuzzle: PuzzlePiece = PuzzlePiece(),
    val positionsPiecePuzzles: MutableList<PuzzlePiece> = mutableListOf(),
    val piecesPuzzle : MutableList<PuzzlePiece> = mutableListOf(),
    val snapZones: MutableList<SnapZone> = mutableListOf(),
    val snapThreshold: Dp = 50.dp,
    val timerIsRunning: Boolean = true,
    val totalTime: Int = 300,
    val isDefeat: Boolean = false,
    val isVictory: Boolean = false
)
