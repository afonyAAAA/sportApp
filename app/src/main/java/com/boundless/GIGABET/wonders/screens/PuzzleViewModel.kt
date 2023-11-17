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
            is UiEventPuzzleAssembly.DragEndPiecePuzzle -> {
                draggedEndPieceOnSnapZone(event.index)
            }
            is UiEventPuzzleAssembly.OnTapPiecePuzzle -> {
                val newListSelectedPuzzle = stateAssemblyPuzzle.selectedPiecesPuzzle + event.puzzlePiece.copy(
                    offSetX = event.offset.x,
                    offsetY = event.offset.y
                )

                stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
                    selectedPiecesPuzzle = newListSelectedPuzzle.toMutableList(),
                )

                deletePiecePuzzle(event.puzzlePiece.copy(offsetY = 0f, offSetX = 0f))
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
                        piecesPuzzle.map { PuzzlePiece(piece = null, position = it.position) }.toMutableList(),
                    timerIsRunning = stateSettingsPuzzle.timerIsOn
                )
            }
            is UiEventPuzzleAssembly.ContinueDragPiecePuzzle -> {
                val updatedPuzzles = stateAssemblyPuzzle.selectedPiecesPuzzle.toMutableList()
                val selectedPuzzle = updatedPuzzles[event.index]

                updatedPuzzles[event.index] = selectedPuzzle.copy(offsetY = selectedPuzzle.offsetY + event.offset.y, offSetX = selectedPuzzle.offSetX + event.offset.x)
                stateAssemblyPuzzle = stateAssemblyPuzzle.copy(selectedPiecesPuzzle = updatedPuzzles)
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
            UiEventPuzzleAssembly.ResetAssemblyPuzzle -> {
                stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
                    selectedPiecesPuzzle = mutableListOf(),
                    totalTime = 300,
                    isVictory = false,
                    isDefeat = false
                )
            }

             is UiEventPuzzleAssembly.OnDragStart -> {
                 val updatedPuzzles = stateAssemblyPuzzle.selectedPiecesPuzzle.toMutableList()
                 val selectedPuzzle = updatedPuzzles[event.index]
                 stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
                     lastOffset = Offset(selectedPuzzle.offSetX, selectedPuzzle.offsetY)
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

    private fun linkingToSnapZone(offset: Offset, index: Int){
        changeOffsetPuzzle(offset, index)
    }

    private fun changeOffsetPuzzle(offset: Offset, index: Int){
        val updatedPuzzle = stateAssemblyPuzzle.selectedPiecesPuzzle.toMutableList()

        val selectedPuzzle = updatedPuzzle[index]

        updatedPuzzle[index] = selectedPuzzle.copy(
            offsetY = offset.y,
            offSetX = offset.x
        )

        stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
            selectedPiecesPuzzle = updatedPuzzle
        )
    }

    private fun addCompletedPuzzle(){
        val sp = context.getSharedPreferences("listCompletedPuzzle", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putBoolean(stateChoosePuzzle.selectedPuzzle!!.pathName, true)
        editor.apply()
    }
    private fun isCorrectSetPiecePuzzle(position: Position, puzzlePiece: PuzzlePiece) = puzzlePiece.position == position

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

    private fun draggedEndPieceOnSnapZone(index : Int){
        val updatedPuzzles = stateAssemblyPuzzle.selectedPiecesPuzzle.toMutableList()
        val puzzlePiece = updatedPuzzles[index]

        val closestZone = stateAssemblyPuzzle.snapZones.minByOrNull { zone ->
            val dx = puzzlePiece.offSetX - zone.offset.x
            val dy = puzzlePiece.offsetY - zone.offset.y
            dx * dx + dy * dy
        }

        if (closestZone != null && closestZone.isWithinSnapThreshold(
                Offset(
                    puzzlePiece.offSetX,
                    puzzlePiece.offsetY
                ), stateAssemblyPuzzle.snapThreshold
            )
        ) {
            var isReplace = false
            var oldPuzzle = PuzzlePiece()
            stateAssemblyPuzzle.selectedPiecesPuzzle.forEach {
                if (it.offSetX == closestZone.offset.x && it.offsetY == closestZone.offset.y) {
                    replacePuzzles(selectedPuzzle = puzzlePiece, oldPuzzle = it)
                    oldPuzzle = it
                    isReplace = true
                }
            }
                // Привязываем квадрат к центру ближайшей зоны
                linkingToSnapZone(closestZone.offset, index)

                if (isCorrectSetPiecePuzzle(closestZone.position, puzzlePiece)) {
                    setPiecePuzzle(
                        closestZone.position,
                        puzzlePiece.piece!!
                    )
                    deleteSnapZone(closestZone)
                    deleteSelectedPiecePuzzle(index)
                }

                if(isReplace){
                    val updatedPuzzless = stateAssemblyPuzzle.selectedPiecesPuzzle.toMutableList()
                    val indexO = updatedPuzzless.indexOf(oldPuzzle.copy(
                        offSetX = stateAssemblyPuzzle.lastOffset.x,
                        offsetY = stateAssemblyPuzzle.lastOffset.y
                    ))
                    val closestZoneO = stateAssemblyPuzzle.snapZones.minByOrNull { zone ->
                        val dx = stateAssemblyPuzzle.lastOffset.x - zone.offset.x
                        val dy = stateAssemblyPuzzle.lastOffset.y - zone.offset.y
                        dx * dx + dy * dy
                    }
                    if (closestZoneO != null && closestZoneO.isWithinSnapThreshold(
                            Offset(
                                stateAssemblyPuzzle.lastOffset.x,
                                stateAssemblyPuzzle.lastOffset.y
                            ), stateAssemblyPuzzle.snapThreshold
                        )
                    ) {
                        if (isCorrectSetPiecePuzzle(closestZoneO.position, oldPuzzle)) {
                            setPiecePuzzle(
                                closestZoneO.position,
                                oldPuzzle.piece!!
                            )
                            deleteSnapZone(closestZoneO)
                            deleteSelectedPiecePuzzle(indexO)
                        }
                    }
                }

        }
    }

    private fun replacePuzzles(selectedPuzzle : PuzzlePiece, oldPuzzle : PuzzlePiece){
        //val offsetForOldPuzzle = Offset(selectedPuzzle.offSetX, selectedPuzzle.offsetY)
        val offsetForSelectedPuzzle = Offset(oldPuzzle.offSetX, oldPuzzle.offsetY)

        val updatedPuzzles = stateAssemblyPuzzle.selectedPiecesPuzzle.toMutableList()
        val indexSelected = updatedPuzzles.indexOf(selectedPuzzle)
        val indexOld = updatedPuzzles.indexOf(oldPuzzle)

        updatedPuzzles[indexSelected] = selectedPuzzle.copy(
            offSetX = offsetForSelectedPuzzle.x,
            offsetY = offsetForSelectedPuzzle.y
        )

        updatedPuzzles[indexOld] = oldPuzzle.copy(
            offSetX = stateAssemblyPuzzle.lastOffset.x,
            offsetY = stateAssemblyPuzzle.lastOffset.y
        )

        stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
            selectedPiecesPuzzle = updatedPuzzles
        )


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

    private fun deleteSelectedPiecePuzzle(index : Int){
        val updatedPuzzles = stateAssemblyPuzzle.selectedPiecesPuzzle.toMutableList()
        updatedPuzzles.removeAt(index)
        stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
            selectedPiecesPuzzle = updatedPuzzles
        )
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