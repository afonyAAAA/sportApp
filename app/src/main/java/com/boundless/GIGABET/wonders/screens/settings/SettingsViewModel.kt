package com.boundless.GIGABET.wonders.screens.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.boundless.GIGABET.wonders.event.UiEventSettingsPuzzle
import com.boundless.GIGABET.wonders.states.StateSettingsPuzzle

class SettingsViewModel(private val context : Context) : ViewModel(){

    var stateSettingsPuzzle by mutableStateOf(StateSettingsPuzzle())

    init {
        readSettingsPuzzle()
    }

    fun onEventSettingsPuzzle(event: UiEventSettingsPuzzle){
        when(event){
            UiEventSettingsPuzzle.ResetCompletedPuzzle -> {
                resetCompletedPuzzle()
            }
            is UiEventSettingsPuzzle.TimerStateImageChoose -> {
                stateSettingsPuzzle = stateSettingsPuzzle.copy(
                    timerIsOn = event.state
                )
                editTimerState()
            }
            is UiEventSettingsPuzzle.VisibleStateImageChoose -> {
                stateSettingsPuzzle =stateSettingsPuzzle.copy(
                    imageIsVisible = event.state
                )
                editVisibleImages()
            }
        }
    }

    private fun readSettingsPuzzle(){
        val sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val visibleImageState = sp.getBoolean("visibleImage", false)
        val timerState = sp.getBoolean("timerIsOn", true)

        stateSettingsPuzzle = stateSettingsPuzzle.copy(
            imageIsVisible = visibleImageState,
            timerIsOn = timerState
        )
    }

    private fun editVisibleImages(){
        val sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putBoolean("visibleImage", stateSettingsPuzzle.imageIsVisible)
        editor.apply()
    }

    private fun editTimerState(){
        val sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putBoolean("timerIsOn", stateSettingsPuzzle.timerIsOn)
        editor.apply()
    }

    private fun resetCompletedPuzzle(){
        val sp = context.getSharedPreferences("listCompletedPuzzle", Context.MODE_PRIVATE)
        val editor = sp.edit().clear()
        editor.apply()
    }

}