package ru.fi.sportapp.event

import android.graphics.Bitmap
import ru.fi.sportapp.screens.Image

sealed class UiEventPuzzleChoose{
    object ShowImages : UiEventPuzzleChoose()
    data class ImageIsChoose(val image : Image) : UiEventPuzzleChoose()
}
