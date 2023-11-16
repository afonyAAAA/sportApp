package com.boundless.GIGABET.wonders.event

import androidx.compose.ui.geometry.Offset
import com.boundless.GIGABET.wonders.models.PuzzlePiece
import com.boundless.GIGABET.wonders.models.SnapZone

sealed class UiEventPuzzleAssembly{
    object PuzzleIsChoose : UiEventPuzzleAssembly()
    data class OnTapPiecePuzzle(val offset: Offset, val puzzlePiece: PuzzlePiece) : UiEventPuzzleAssembly()
    data class ContinueDragPiecePuzzle(val offset: Offset, val puzzlePiece: PuzzlePiece, val index : Int) : UiEventPuzzleAssembly()
    data class DragEndPiecePuzzle(val index: Int) : UiEventPuzzleAssembly()
    data class SetSnapZone(val snapZone : SnapZone) : UiEventPuzzleAssembly()
    object MinusSecondTime : UiEventPuzzleAssembly()
    object TimeIsEnd : UiEventPuzzleAssembly()
    object PuzzleIsCompleted : UiEventPuzzleAssembly()
    object ResetAssemblyPuzzle : UiEventPuzzleAssembly()
}
