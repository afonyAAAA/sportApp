package com.boundless.GIGABET.wonders.screens.listPuzzle

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.boundless.GIGABET.wonders.event.UiEventPuzzleChoose
import com.boundless.GIGABET.wonders.models.Image
import com.boundless.GIGABET.wonders.states.StateChoosePuzzle
import kotlinx.collections.immutable.toImmutableList

class ChoosePuzzleViewModel(private val context: Context) : ViewModel() {

    var stateChoosePuzzle by mutableStateOf(StateChoosePuzzle())

    init {
        stateChoosePuzzle = stateChoosePuzzle.copy(listImage = getAllPuzzles().toImmutableList())
    }

    fun onEventChoosePuzzle(event: UiEventPuzzleChoose){
        stateChoosePuzzle = when(event){
            UiEventPuzzleChoose.ShowImages -> {
                stateChoosePuzzle.copy(
                    listImage = getAllPuzzles().toImmutableList()
                )
            }
            is UiEventPuzzleChoose.ImageIsChoose -> {
                stateChoosePuzzle.copy(
                    selectedPuzzle = event.image
                )
            }
        }
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

}