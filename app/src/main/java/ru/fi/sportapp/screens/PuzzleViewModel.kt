package ru.fi.sportapp.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ru.fi.sportapp.R
import ru.fi.sportapp.models.SnapZone

class PuzzleViewModel(context: Context) : ViewModel() {

    var offsetX by mutableFloatStateOf(0f)
    var offsetY by mutableFloatStateOf(0f)
    var isDragPiecePuzzle by mutableStateOf(false)
    lateinit var selectedPiecePuzzle : Bitmap
    val listImage : MutableList<Bitmap> = mutableListOf()
    val image = BitmapFactory.decodeResource(context.resources, R.drawable.cat)
    val snapZones = mutableStateListOf<SnapZone>()
    init {
        listImage.addAll(splitImage(image))
    }

    fun addSnapZone(snap : SnapZone) = snapZones.add(snap)

    private fun splitImage(image: Bitmap) : List<Bitmap>{
        val imageWidth = image.width
        val imageHeight = image.height
        val tileWidth = imageWidth / 5
        val tileHeight = imageHeight / 5

        val tiles = mutableListOf<Bitmap>()

        for (row in 0 until 5) {
            for (col in 0 until 5) {
                val x = col * tileWidth
                val y = row * tileHeight
                val tile = Bitmap.createBitmap(image, x, y, tileWidth, tileHeight)
                tiles.add(tile)
            }
        }

        return tiles
    }
}