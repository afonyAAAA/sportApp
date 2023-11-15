package com.boundless.GIGABET.wonders.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.boundless.GIGABET.wonders.event.UiEventPuzzleAssembly
import com.boundless.GIGABET.wonders.event.UiEventPuzzleChoose
import com.boundless.GIGABET.wonders.event.UiEventSettingsPuzzle
import com.boundless.GIGABET.wonders.models.Image
import com.boundless.GIGABET.wonders.models.Position
import com.boundless.GIGABET.wonders.models.PuzzlePiece
import com.boundless.GIGABET.wonders.models.SnapZone

class PuzzleViewModel(val context: Context) : ViewModel() {

    var stateAssemblyPuzzle by mutableStateOf(StateAssemblyPuzzle())
    var stateChoosePuzzle by mutableStateOf(StateChoosePuzzle())
    var stateSettingsPuzzle by mutableStateOf(StateSettingsPuzzle())

    init {
        readSettingsPuzzle()
        stateChoosePuzzle = stateChoosePuzzle.copy(listImage = getAllPuzzles())
    }

    fun onEventAssembly(event: UiEventPuzzleAssembly){
         when(event){
            UiEventPuzzleAssembly.DragEndPiecePuzzle -> {
                draggedEndPieceOnSnapZone()
            }
            is UiEventPuzzleAssembly.OnTapPiecePuzzle -> {
                stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
                    selectedPiecePuzzle = event.puzzlePiece,
                    offsetXpiece = event.offset.x,
                    offsetYpiece = event.offset.y,
                    isDragPiecePuzzle = true
                )
            }
            is UiEventPuzzleAssembly.SetSnapZone -> {
                val newSnapZones = stateAssemblyPuzzle.snapZones + event.snapZone
                stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
                    snapZones = newSnapZones.toMutableList()
                )
            }
            is UiEventPuzzleAssembly.PuzzleIsChoose -> {

                val piecesPuzzle = splitImage()

                stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
                    piecesPuzzle = piecesPuzzle.shuffled().toMutableList(),
                    positionsPiecePuzzles =
                        piecesPuzzle.map { PuzzlePiece(piece = null, position = it.position,) }.toMutableList(),
                    timerIsRunning = stateSettingsPuzzle.timerIsOn
                )
            }
            is UiEventPuzzleAssembly.ContinueDragPiecePuzzle -> {
                stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
                    offsetXpiece = stateAssemblyPuzzle.offsetXpiece + event.offset.x,
                    offsetYpiece = stateAssemblyPuzzle.offsetYpiece + event.offset.y
                )
            }
            UiEventPuzzleAssembly.MinusSecondTime -> {
                stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
                    totalTime = stateAssemblyPuzzle.totalTime - 1
                )
            }
            UiEventPuzzleAssembly.TimeIsEnd -> {
                stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
                    isDefeat = true
                )
            }
            UiEventPuzzleAssembly.PuzzleIsCompleted -> {
                addCompletedPuzzle()
            }
            is UiEventPuzzleAssembly.OnTapWithPiecePuzzle -> {
                stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
                    offsetXpiece = event.offset.x - 50.0f,
                    offsetYpiece = event.offset.y - 50.0f
                )
                draggedEndPieceOnSnapZone()
            }
            UiEventPuzzleAssembly.ResetAssemblyPuzzle -> {
                stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
                    selectedPiecePuzzle = PuzzlePiece(),
                    isDragPiecePuzzle = false,
                    offsetXpiece = 0f,
                    offsetYpiece = 0f,
                    totalTime = 300,
                    isVictory = false,
                    isDefeat = false
                )
            }
        }
    }

    fun onEventChoosePuzzle(event: UiEventPuzzleChoose){
        stateChoosePuzzle = when(event){
            UiEventPuzzleChoose.ShowImages -> {
                stateChoosePuzzle.copy(
                    listImage = getAllPuzzles()
                )
            }
            is UiEventPuzzleChoose.ImageIsChoose -> {
                stateChoosePuzzle.copy(
                    selectedPuzzle = event.image
                )
            }
        }
    }


    fun onEventSettingsPuzzle(event: UiEventSettingsPuzzle){
         when(event){
            UiEventSettingsPuzzle.ResetCompletedPuzzle -> {
                resetCompletedPuzzle()
                stateChoosePuzzle = stateChoosePuzzle.copy(
                    listImage = getAllPuzzles()
                )
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

    private fun getAllPuzzles(): MutableList<Image> {
        val sp = context.getSharedPreferences("listCompletedPuzzle", Context.MODE_PRIVATE)
        val listPathImages = context.assets.list("images")
        val images = mutableListOf<Image>()

        listPathImages!!.forEach { path ->
            if(path.substring(0..4) == "image"){
                val isVisibleImage = sp.getBoolean(path, false)
                val inputStream = context.assets.open("images/$path")
                val image = BitmapFactory.decodeStream(inputStream)
                images.add(Image(path, image, isVisibleImage))
            }
        }

        return images.shuffled().toMutableList()
    }

    private fun linkingToSnapZone(offset: Offset){
        stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
            offsetXpiece = offset.x,
            offsetYpiece = offset.y
        )
    }

    private fun addCompletedPuzzle(){
        val sp = context.getSharedPreferences("listCompletedPuzzle", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putBoolean(stateChoosePuzzle.selectedPuzzle!!.pathName, true)
        editor.apply()
    }
    private fun isCorrectSetPiecePuzzle(position: Position)  =  stateAssemblyPuzzle.selectedPiecePuzzle.position == position

    private fun setPiecePuzzle(position: Position, piece : Bitmap) {
        val newPositionsPuzzlePiece = stateAssemblyPuzzle.positionsPiecePuzzles.toMutableList()

        for (pos in newPositionsPuzzlePiece){
            if(pos.position == position){
                val index = newPositionsPuzzlePiece.indexOf(pos)
                newPositionsPuzzlePiece[index] = PuzzlePiece(piece, pos.position,)
            }
        }

        stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
            positionsPiecePuzzles = newPositionsPuzzlePiece
        )
    }

    private fun draggedEndPieceOnSnapZone(){

        val closestZone = stateAssemblyPuzzle.snapZones.minByOrNull { zone ->
            val dx = stateAssemblyPuzzle.offsetXpiece - zone.offset.x
            val dy = stateAssemblyPuzzle.offsetYpiece - zone.offset.y
            dx * dx + dy * dy
        }

        if (closestZone != null && closestZone.isWithinSnapThreshold(
                Offset(
                    stateAssemblyPuzzle.offsetXpiece,
                    stateAssemblyPuzzle.offsetYpiece
                ), stateAssemblyPuzzle.snapThreshold
            )
        ) {
            // Привязываем квадрат к центру ближайшей зоны
            linkingToSnapZone(closestZone.offset)

            if (isCorrectSetPiecePuzzle(closestZone.position)) {
                setPiecePuzzle(
                    closestZone.position,
                    stateAssemblyPuzzle.selectedPiecePuzzle.piece!!
                )
                deletePiecePuzzle(stateAssemblyPuzzle.selectedPiecePuzzle)
                deleteSnapZone(closestZone)

                stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
                    isDragPiecePuzzle = false,
                    selectedPiecePuzzle = PuzzlePiece()
                )
            }
        }
    }

    private fun splitImage() : MutableList<PuzzlePiece>{
        val fullImageOfPuzzle = stateChoosePuzzle.selectedPuzzle!!.image
        val imageWidth = fullImageOfPuzzle.width
        val imageHeight = fullImageOfPuzzle.height
        val tileWidth = imageWidth / 5
        val tileHeight = imageHeight / 5

        val tiles = mutableListOf<PuzzlePiece>()
        var counter = 1

        for (row in 0 until 5) {
            for (col in 0 until 5) {
                val x = col * tileWidth
                val y = row * tileHeight
                val tile = Bitmap.createBitmap(fullImageOfPuzzle, x, y, tileWidth, tileHeight)
                val puzzlePiece = PuzzlePiece(tile, Position(Triple(row + 1, row + 1, col + 1)), id = counter)
                tiles.add(puzzlePiece)
                counter++
            }
            counter++
        }

        return tiles
    }

    private fun deletePiecePuzzle(piece: PuzzlePiece) {
        stateAssemblyPuzzle = stateAssemblyPuzzle.copy(piecesPuzzle = stateAssemblyPuzzle.piecesPuzzle.minus(piece).toMutableList())
    }

    private fun deleteSnapZone(snap: SnapZone) {
        stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
            snapZones = stateAssemblyPuzzle.snapZones.filter { it.offset != snap.offset }.toMutableList()
        )

        if(stateAssemblyPuzzle.snapZones.isEmpty()){
            stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
                isVictory = true
            )
        }
    }

}