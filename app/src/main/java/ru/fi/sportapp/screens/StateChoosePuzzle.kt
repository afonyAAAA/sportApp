package ru.fi.sportapp.screens

import android.graphics.Bitmap

data class StateChoosePuzzle(
    val listImage : List<Image> = emptyList(),
    val selectedPuzzle : Image? = null
)

data class Image(val pathName : String, val image : Bitmap, val visible : Boolean)
