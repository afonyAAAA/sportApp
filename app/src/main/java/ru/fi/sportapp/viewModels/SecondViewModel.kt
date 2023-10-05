package ru.fi.sportapp.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SecondViewModel : ViewModel() {
    var timeShowAnimation by mutableStateOf(0)



    fun addTimeForTimeAnimation() = timeShowAnimation++

}