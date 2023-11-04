package ru.fi.sportapp.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import ru.fi.sportapp.event.UiEventPuzzleAssembly
import ru.fi.sportapp.event.UiEventPuzzleChoose
import ru.fi.sportapp.event.UiEventSettingsPuzzle
import ru.fi.sportapp.models.Position
import ru.fi.sportapp.models.PuzzlePiece
import ru.fi.sportapp.models.SnapZone

class PuzzleViewModel(val context: Context) : ViewModel() {

    var stateAssemblyPuzzle by mutableStateOf(StateAssemblyPuzzle())
    var stateChoosePuzzle by mutableStateOf(StateChoosePuzzle())
    var stateSettingsPuzzle by mutableStateOf(StateSettingsPuzzle())
    init {
        readSettingsPuzzle()
        stateChoosePuzzle = stateChoosePuzzle.copy(listImage = getAllPuzzles())
    }

    fun onEventAssembly(event: UiEventPuzzleAssembly){
        stateAssemblyPuzzle = when(event){
            UiEventPuzzleAssembly.DragEndPiecePuzzle -> {
                draggedEndPieceOnSnapZone()
                stateAssemblyPuzzle
            }
            is UiEventPuzzleAssembly.OnTapPiecePuzzle -> {
                stateAssemblyPuzzle.copy(
                    selectedPiecePuzzle = event.puzzlePiece,
                    offsetXpiece = event.offset.x,
                    offsetYpiece = event.offset.y,
                    isDragPiecePuzzle = true
                )
            }
            is UiEventPuzzleAssembly.SetSnapZone -> {
                val newSnapZones = stateAssemblyPuzzle.snapZones + event.snapZone
                stateAssemblyPuzzle.copy(
                    snapZones = newSnapZones.toMutableList()
                )
            }
            is UiEventPuzzleAssembly.PuzzleIsChoose -> {

                val piecesPuzzle = splitImage()

                stateAssemblyPuzzle.copy(
                    piecesPuzzle = piecesPuzzle,
                    positionsPiecePuzzles =
                        piecesPuzzle.map { PuzzlePiece(piece = null, position = it.position) }.toMutableList()
                )
            }
            is UiEventPuzzleAssembly.ContinueDragPiecePuzzle -> {
                stateAssemblyPuzzle.copy(
                    offsetXpiece = stateAssemblyPuzzle.offsetXpiece + event.offset.x,
                    offsetYpiece = stateAssemblyPuzzle.offsetYpiece + event.offset.y
                )
            }
            UiEventPuzzleAssembly.MinusSecondTime -> {
                stateAssemblyPuzzle.copy(
                    totalTime = stateAssemblyPuzzle.totalTime - 1
                )
            }
            UiEventPuzzleAssembly.TimeIsEnd -> {
                stateAssemblyPuzzle.copy(
                    isDefeat = true
                )
            }

            UiEventPuzzleAssembly.PuzzleIsCompleted -> {
                stateAssemblyPuzzle
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
        stateSettingsPuzzle = when(event){
            UiEventSettingsPuzzle.ResetCompletedPuzzle -> {
                resetCompletedPuzzle()
                stateChoosePuzzle = stateChoosePuzzle.copy(
                    listImage = getAllPuzzles()
                )
                stateSettingsPuzzle
            }
            is UiEventSettingsPuzzle.TimerStateImageChoose -> {
                stateSettingsPuzzle.copy(
                    timerIsOn = event.state.apply {
                        editTimerState()
                    }
                )
            }
            is UiEventSettingsPuzzle.VisibleStateImageChoose -> {
                stateSettingsPuzzle.copy(
                    imageIsVisible = event.state.apply {
                        editVisibleImages()
                    }
                )
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
        val editor = sp.edit()
        stateChoosePuzzle.listImage.forEach { image ->
            editor.putString(image.pathName, "")
        }
        editor.apply()
    }

    private fun getAllPuzzles(): MutableList<Image> {
        val sp = context.getSharedPreferences("listCompletedPuzzle", Context.MODE_PRIVATE)
        val listPathImages = context.assets.list("images")
        val images = mutableListOf<Image>()

        listPathImages!!.forEach { path ->
            if(path.substring(0..4) == "image"){
                val isVisibleImage = sp.getString(path, "")?.isNotEmpty() ?: false
                val inputStream = context.assets.open("images/$path")
                val image = BitmapFactory.decodeStream(inputStream)
                images.add(Image(path, image, isVisibleImage))
            }
        }

        return images
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
        editor.putString(stateChoosePuzzle.selectedPuzzle!!.pathName, "")
        editor.apply()
    }
    private fun isCorrectSetPiecePuzzle(position: Position)  =  stateAssemblyPuzzle.selectedPiecePuzzle.position == position

    private fun setPiecePuzzle(position: Position, piece : Bitmap) {
        val newPositionsPuzzlePiece = stateAssemblyPuzzle.positionsPiecePuzzles.toMutableList()

        for (pos in newPositionsPuzzlePiece){
            if(pos.position == position){
                val index = newPositionsPuzzlePiece.indexOf(pos)
                newPositionsPuzzlePiece[index] = PuzzlePiece(piece, pos.position)
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
                deletePiecePuzzle(stateAssemblyPuzzle.selectedPiecePuzzle.position!!)
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

        for (row in 0 until 5) {
            for (col in 0 until 5) {
                val x = col * tileWidth
                val y = row * tileHeight
                val tile = Bitmap.createBitmap(fullImageOfPuzzle, x, y, tileWidth, tileHeight)
                val puzzlePiece = PuzzlePiece(tile, Position(Triple(row + 1, row + 1, col + 1)))
                tiles.add(puzzlePiece)
            }
        }

        return tiles
    }

    private fun deletePiecePuzzle(position: Position) {
        val newListPiecesPuzzle = stateAssemblyPuzzle.piecesPuzzle.filter { it.position != position }.toMutableList()

        stateAssemblyPuzzle = stateAssemblyPuzzle.copy(piecesPuzzle = newListPiecesPuzzle)
    }

    private fun deleteSnapZone(snap: SnapZone) {
        stateAssemblyPuzzle = stateAssemblyPuzzle.copy(
            snapZones = stateAssemblyPuzzle.snapZones.filter { it.offset != snap.offset }.toMutableList()
        )
    }

}