package ru.fi.sportapp.models

import android.graphics.Bitmap

data class Puzzle(
    val name : String,
    val wholePuzzle : Bitmap,
    val piecesPuzzle : List<PuzzlePiece>
)
