package brazilian.sports.craze.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import brazilian.sports.craze.models.GameStatus
import brazilian.sports.craze.models.MyColor

class GameViewModel : ViewModel() {

    val colors = listOf(
        MyColor(Color.Magenta, "Magenta"),
        MyColor(Color.Red, "Red"),
        MyColor(Color.Green, "Green"),
        MyColor(Color.Blue, "Blue"),
        MyColor(Color.Cyan, "Cyan"),
        MyColor(Color.Yellow, "Yellow")
    )
    var maxScore = 0.0
    var xScope by mutableStateOf(1)
    var gameStatus by mutableStateOf(GameStatus.PLAYING)
    var targetColors by mutableStateOf(mutableListOf<MyColor?>())
    var changeColor by mutableStateOf<MyColor?>(null)
    var score by mutableStateOf(100.0)
    var numberStringBet by mutableStateOf("")
    var showDialogAboutGame by mutableStateOf(false)
    var isStartGame by mutableStateOf(false)

    private fun generateRandomColor() {

        if(targetColors.isNotEmpty()) targetColors.clear()

        repeat(3){
            targetColors.add(colors.random())
        }

    }
    private fun checkColor() {
        val numberBetToLong = numberStringBet.filter { it.isDigit() }.toLong()

        if(targetColors!!.contains(changeColor)){
            xScope++
            gameStatus = GameStatus.WIN
            score += (score + numberBetToLong) * xScope
            if(maxScore < score) maxScore = score
        }else{
            xScope = 1
            score -= numberBetToLong
            gameStatus = GameStatus.LOSE
        }
        changeColor = null
    }

    fun resume(){
        xScope = 1
        gameStatus = GameStatus.PLAYING
        targetColors.clear()
        changeColor = null
        score = 100.0
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