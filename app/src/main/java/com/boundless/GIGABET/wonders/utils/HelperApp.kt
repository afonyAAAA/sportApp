package com.boundless.GIGABET.wonders.utils

import com.boundless.GIGABET.wonders.models.Image
import com.boundless.GIGABET.wonders.states.StateSettingsPuzzle

object HelperApp {

    object Settings {
        var state : StateSettingsPuzzle = StateSettingsPuzzle()
    }

    object Puzzle{
        var puzzle : Image = Image()
    }

}