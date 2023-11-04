package ru.fi.sportapp.event

import androidx.compose.ui.geometry.Offset
import ru.fi.sportapp.models.PuzzlePiece
import ru.fi.sportapp.models.SnapZone

sealed class UiEventPuzzleAssembly{
    object PuzzleIsChoose : UiEventPuzzleAssembly()
    data class OnTapPiecePuzzle(val offset: Offset, val puzzlePiece: PuzzlePiece) : UiEventPuzzleAssembly()
    data class ContinueDragPiecePuzzle(val offset: Offset) : UiEventPuzzleAssembly()
    object DragEndPiecePuzzle : UiEventPuzzleAssembly()
    data class SetSnapZone(val snapZone : SnapZone) : UiEventPuzzleAssembly()
    object MinusSecondTime : UiEventPuzzleAssembly()
    object TimeIsEnd : UiEventPuzzleAssembly()
    object PuzzleIsCompleted : UiEventPuzzleAssembly()
}
