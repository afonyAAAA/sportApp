package com.boundless.GIGABET.wonders.screens

import com.boundless.GIGABET.wonders.models.Image

data class StateChoosePuzzle(
    val listImage : List<Image> = emptyList(),
    val selectedPuzzle : Image? = null
)
