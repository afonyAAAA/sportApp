package com.boundless.GIGABET.wonders.states

import androidx.compose.runtime.Stable

@Stable
data class StateSettingsPuzzle(
    val imageIsVisible : Boolean = false,
    val timerIsOn : Boolean = true
)
