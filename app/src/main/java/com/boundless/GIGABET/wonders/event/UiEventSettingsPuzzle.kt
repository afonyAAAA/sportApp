package com.boundless.GIGABET.wonders.event

sealed class UiEventSettingsPuzzle{
    data class VisibleStateImageChoose(val state: Boolean) : UiEventSettingsPuzzle()
    data class TimerStateImageChoose(val state: Boolean) : UiEventSettingsPuzzle()
    object ResetCompletedPuzzle : UiEventSettingsPuzzle()
}
