package com.boundless.GIGABET.wonders.models

import android.graphics.Bitmap

data class PuzzlePiece(
    val piece: Bitmap? = null,
    val position: Position? = null,
    val offSetX : Float = 0f,
    val offsetY : Float = 0f,
    val id: Int = 0
)