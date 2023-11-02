package ru.fi.sportapp.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ru.fi.sportapp.models.Position
import ru.fi.sportapp.models.SnapZone
import kotlin.math.roundToInt

@Composable
fun PuzzleScreen(navHostController: NavHostController, viewModel: PuzzleViewModel){
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val snapThreshold = 50.dp

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Puzzle", modifier = Modifier.padding(50.dp))

            (0..5).forEach{ columnAndRowIndex ->
                Column {
                    Row {
                        (0..5).forEach{positionInRow ->
                            AreaOfPuzzlePiece(position = Position(Triple(columnAndRowIndex, columnAndRowIndex, positionInRow))){
                                viewModel.addSnapZone(it)
                            }
                            Spacer(modifier = Modifier.width(5.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
            }
            LazyRow{
                items(viewModel.listImage){
                    PuzzlePiece(image = it){ change, offSet ->
                        viewModel.selectedPiecePuzzle = it
                        viewModel.offsetX += offSet.x
                        viewModel.offsetY += offSet.y
                        viewModel.isDragPiecePuzzle = true
                    }
                    Spacer(modifier = Modifier.width(50.dp))
                }
            }
        }

        if(viewModel.isDragPiecePuzzle){
            Image(
                bitmap = viewModel.selectedPiecePuzzle.asImageBitmap(), "",
                modifier = Modifier
                    .offset { IntOffset(viewModel.offsetX.roundToInt(), viewModel.offsetY.roundToInt()) }
                    .background(Color.Blue)
                    .size(50.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                val closestZone = viewModel.snapZones.minByOrNull { zone ->
                                    val dx = viewModel.offsetX - zone.centerX
                                    val dy = viewModel.offsetY - zone.centerY
                                    dx * dx + dy * dy
                                }

                                // Проверяем, находится ли квадрат близко к ближайшей зоне
                                if (closestZone != null && closestZone.isWithinSnapThreshold(
                                        Offset(
                                            viewModel.offsetX,
                                            viewModel.offsetX
                                        ), snapThreshold
                                    )
                                ) {
                                    // Привязываем квадрат к центру ближайшей зоны
                                    viewModel.offsetX = closestZone.centerX - 25.dp.toPx()
                                    viewModel.offsetY = closestZone.centerY - 25.dp.toPx()
                                }
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            viewModel.offsetX += dragAmount.x
                            viewModel.offsetY += dragAmount.y
                        }

                    }
            )
        }

    }
}


@Composable
fun PuzzlePiece(image : Bitmap, onDragStart : (PointerInputChange, Offset) -> Unit){
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val density = LocalDensity.current.density

    val boxOnScreen = IntOffset(
        (offsetX * density).toInt(),
        (offsetY * density).toInt()
    )

    Image(
        bitmap = image.asImageBitmap(),
        contentDescription = "",
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(50.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    onDragStart(change, Offset(boxOnScreen.x.toFloat(), boxOnScreen.y.toFloat()))
                }
            }
    )


}

@Composable
fun AreaOfPuzzlePiece(position : Position, setSnapZone: (SnapZone) -> Unit){

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .background(Color.Gray)
            .size(50.dp)
    )

    val density = LocalDensity.current.density

    val boxOnScreen = IntOffset(
        (offsetX * density).toInt(),
        (offsetY * density).toInt()
    )

    setSnapZone(SnapZone(boxOnScreen.x.toFloat(),boxOnScreen.y.toFloat()))
}
