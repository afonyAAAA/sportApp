package com.boundless.GIGABET.wonders.screens.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.boundless.GIGABET.wonders.event.UiEventSettingsPuzzle
import com.boundless.GIGABET.wonders.states.StateSettingsPuzzle
import com.boundless.GIGABET.wonders.utils.HelperApp

class SettingsViewModel(private val context : Context) : ViewModel(){


    private val _stateSettingsPuzzle = MutableLiveData(StateSettingsPuzzle())
    val stateSettingsPuzzle : LiveData<StateSettingsPuzzle> = _stateSettingsPuzzle

    init {
        readSettingsPuzzle()
    }

    fun onEventSettingsPuzzle(event: UiEventSettingsPuzzle){
        when(event){
            UiEventSettingsPuzzle.ResetCompletedPuzzle -> {
                resetCompletedPuzzle()
            }
            is UiEventSettingsPuzzle.TimerStateImageChoose -> {
                _stateSettingsPuzzle.value = stateSettingsPuzzle.value!!.copy(
                    timerIsOn = event.state
                )
                editTimerState()
            }
            is UiEventSettingsPuzzle.VisibleStateImageChoose -> {
                _stateSettingsPuzzle.value = stateSettingsPuzzle.value!!.copy(
                    imageIsVisible = event.state
                )
                editVisibleImages()
            }
        }
        HelperApp.Settings.state = _stateSettingsPuzzle.value!!
    }

    private fun readSettingsPuzzle(){
        val sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val visibleImageState = sp.getBoolean("visibleImage", false)
        val timerState = sp.getBoolean("timerIsOn", true)

        _stateSettingsPuzzle.value = stateSettingsPuzzle.value!!.copy(
            imageIsVisible = visibleImageState,
            timerIsOn = timerState
        )
    }

    private fun editVisibleImages(){
        val sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putBoolean("visibleImage", _stateSettingsPuzzle.value!!.imageIsVisible)
        editor.apply()
    }

    private fun editTimerState(){
        val sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putBoolean("timerIsOn", _stateSettingsPuzzle.value!!.timerIsOn)
        editor.apply()
    }

    private fun resetCompletedPuzzle(){
        val sp = context.getSharedPreferences("listCompletedPuzzle", Context.MODE_PRIVATE)
        val editor = sp.edit().clear()
        editor.apply()
    }

}