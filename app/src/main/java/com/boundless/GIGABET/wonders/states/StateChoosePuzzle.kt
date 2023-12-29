package com.boundless.GIGABET.wonders.states

import androidx.compose.runtime.Stable
import com.boundless.GIGABET.wonders.models.Image
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.collections.immutable.persistentListOf

@Stable
data class StateChoosePuzzle(
    @Stable
    val listImage : ImmutableCollection<Image> = persistentListOf(),
    val selectedPuzzle : Image? = null
)
