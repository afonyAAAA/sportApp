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
    object PuzzleIsCompleted : UiEventPuzzleAssembly()
    object ResetAssemblyPuzzle : UiEventPuzzleAssembly()
    data class OnDragStart(val index : Int) : UiEventPuzzleAssembly()
    data class SetOffSet(val offSet : Offset) : UiEventPuzzleAssembly()
    object NextRow: UiEventPuzzleAssembly()

}
