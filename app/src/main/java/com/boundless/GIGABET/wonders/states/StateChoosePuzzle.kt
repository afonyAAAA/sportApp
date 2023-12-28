package com.boundless.GIGABET.wonders.states

import androidx.compose.runtime.Stable
import com.boundless.GIGABET.wonders.models.Image

@Stable
data class StateChoosePuzzle(
    @Stable
    val listImage : List<Image> = emptyList(),
    val selectedPuzzle : Image? = null
)
