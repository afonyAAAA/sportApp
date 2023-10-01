package ru.fi.colorGame.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import ru.fi.colorGame.GameStatus
import ru.fi.colorGame.models.MyColor

class GameViewModel : ViewModel() {

    var isStartGame by mutableStateOf(false)
    val colors = listOf(
        MyColor(Color.Magenta, "Magenta"),
        MyColor(Color.Red, "Red"),
        MyColor(Color.Green, "Green"),
        MyColor(Color.Blue, "Blue"),
        MyColor(Color.Cyan, "Cyan"),
        MyColor(Color.Yellow, "Yellow")
    )
    private var xScope by mutableStateOf(1)
    var gameStatus by mutableStateOf(GameStatus.PLAYING)
    var targetColor by mutableStateOf<MyColor?>(null)
    var changeColor by mutableStateOf<MyColor?>(null)
    var score by mutableStateOf(0)
    var showDialogAboutGame by mutableStateOf(false)

    private fun generateRandomColor() {
        targetColor = colors.random()
    }
    private fun checkColor() {
        if(changeColor!!.color == targetColor!!.color){
            if(score != 0) xScope++
            gameStatus = GameStatus.WIN
            score += (score + 1) * xScope
            changeColor = null
        }else{
            gameStatus = GameStatus.LOSE
        }
    }

    fun resume(){
        xScope = 1
        gameStatus = GameStatus.PLAYING
        targetColor = null
        changeColor = null
        score = 0
    }
    fun roll(){
        generateRandomColor()
        checkColor()
    }

    fun onClickColor(myColor: MyColor) {
        if(gameStatus != GameStatus.PLAYING) gameStatus = GameStatus.PLAYING
        changeColor = myColor
    }
}