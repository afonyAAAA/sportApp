package com.boundless.GIGABET.wonders.event

import com.boundless.GIGABET.wonders.models.Image

sealed class UiEventPuzzleChoose{
    object ShowImages : UiEventPuzzleChoose()
    data class ImageIsChoose(val image : Image) : UiEventPuzzleChoose()
}
