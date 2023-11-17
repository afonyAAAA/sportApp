package com.boundless.GIGABET.wonders.screens

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.boundless.GIGABET.wonders.R
import com.boundless.GIGABET.wonders.event.UiEventPuzzleAssembly
import com.boundless.GIGABET.wonders.event.UiEventPuzzleChoose
import com.boundless.GIGABET.wonders.models.PuzzlePiece
import com.boundless.GIGABET.wonders.models.SnapZone
import com.boundless.GIGABET.wonders.navigation.Screens
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun PuzzleScreen(navHostController: NavHostController, viewModel: PuzzleViewModel){

    val state = viewModel.stateAssemblyPuzzle

    Image(
        painter = painterResource(id = R.drawable.background),
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        contentDescription = ""
    )

    Box(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ){
        LaunchedEffect(state.timerIsRunning){
            if(state.timerIsRunning){
                while (viewModel.stateAssemblyPuzzle.totalTime > 0){
                    delay(1000)
                    viewModel.onEventAssembly(UiEventPuzzleAssembly.MinusSecondTime)
                }
                viewModel.onEventAssembly(UiEventPuzzleAssembly.TimeIsEnd)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if(state.timerIsRunning){
                ProgressIndicator(progress = (state.totalTime.toFloat() / 300f) * 1.0f)
            }

            var counter = 0

            (1..5).forEach{ column ->
                Column {
                    Row {
                        state.positionsPiecePuzzles.subList(counter, counter + 5).forEach{ positionInRow ->
                            AreaOfPuzzlePiece(positionInRow){ snapZone ->
                                if(state.snapZones.size != 25){
                                    viewModel.onEventAssembly(UiEventPuzzleAssembly.SetSnapZone(snapZone))
                                }
                            }
                        }
                        counter += 5
                    }
                }
            }

            LazyHorizontalGrid(
                rows = GridCells.Fixed(2),
                contentPadding = PaddingValues(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                verticalArrangement = Arrangement.Center
            ){
                items(
                    state.piecesPuzzle,
                    key = { listItem ->
                        listItem.id
                    }
                ){ piece ->
                    PuzzlePiece(puzzlePiece = piece,
                        onTap = { puzzlePiece, offSet ->
                            viewModel.onEventAssembly(UiEventPuzzleAssembly.OnTapPiecePuzzle(offSet, puzzlePiece))
                        }
                    )
                }
            }
        }

        if (state.isDefeat){
            AlertDialogDefeat {
                navHostController.navigate(
                    Screens.Puzzles.route,
                    NavOptions.Builder()
                        .setPopUpTo(Screens.Main.route, false)
                        .build()
                )
                viewModel.onEventAssembly(UiEventPuzzleAssembly.ResetAssemblyPuzzle)
            }
        }

        if(state.isVictory){
            AlertDialogVictory(
                onDismiss = {
                    navHostController.navigate(
                        Screens.Puzzles.route,
                        NavOptions.Builder()
                            .setPopUpTo(Screens.Main.route, false)
                            .build()
                    )
                    viewModel.onEventAssembly(UiEventPuzzleAssembly.ResetAssemblyPuzzle)
                    viewModel.onEventChoosePuzzle(UiEventPuzzleChoose.ShowImages)
                },
                completedPuzzle = viewModel.stateChoosePuzzle.selectedPuzzle!!.image
            )
            viewModel.onEventAssembly(UiEventPuzzleAssembly.PuzzleIsCompleted)
        }

        state.selectedPiecesPuzzle.forEachIndexed { index, puzzle ->
            Image(
                bitmap = puzzle.piece!!.asImageBitmap(), "",
                modifier = Modifier
                    .offset {
                        IntOffset(
                            puzzle.offSetX.roundToInt(),
                            puzzle.offsetY.roundToInt()
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                viewModel.onEventAssembly(
                                    UiEventPuzzleAssembly.DragEndPiecePuzzle(
                                        index
                                    )
                                )
                            },
                            onDragStart = {
                                viewModel.onEventAssembly(UiEventPuzzleAssembly.OnDragStart(index))
                            }
                        ) { change, dragAmount ->
                            viewModel.onEventAssembly(
                                UiEventPuzzleAssembly.ContinueDragPiecePuzzle(
                                    dragAmount,
                                    puzzle,
                                    index
                                )
                            )
                            change.consumeAllChanges()
                        }
                    }
                    .size(50.dp)
            )
        }

        BackHandler {
            navHostController.popBackStack()
            viewModel.onEventAssembly(UiEventPuzzleAssembly.ResetAssemblyPuzzle)
        }
    }
}

@Composable
fun AlertDialogDefeat(onDismiss : () -> Unit){
    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = MaterialTheme.colorScheme.background,
        confirmButton = { 
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "OK")
            }
        },
        title = {
            Text(text = "You didn't have time :(", color = MaterialTheme.colorScheme.onPrimary)
        }
    )
}

@Composable
fun AlertDialogVictory(onDismiss : () -> Unit, completedPuzzle : Bitmap){
    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = MaterialTheme.colorScheme.background,
        confirmButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "OK")
            }
        },
        title = {
            Text(text = "You've completed the puzzle!", color = MaterialTheme.colorScheme.onPrimary)
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                Image(
                    bitmap = completedPuzzle.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier.size(200.dp)
                )
            }
        }
    )
}

@Composable
fun ProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    LinearProgressIndicator(
        progress = progress,
        color = color,
        modifier = modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Transparent, shape = RoundedCornerShape(4.dp))
            .height(25.dp)
            .fillMaxWidth()
    )

}

@Composable
fun PuzzlePiece(
    puzzlePiece : PuzzlePiece,
    onTap : (PuzzlePiece, Offset) -> Unit
){
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }

    Box(modifier = Modifier.size(75.dp), contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier.size(75.dp),
            color = MaterialTheme.colorScheme.primary
        ) {
            Image(
                bitmap = puzzlePiece.piece!!.asImageBitmap(),
                contentDescription = "",
                modifier = Modifier
                    .padding(8.dp)
                    .size(50.dp)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            onTap(puzzlePiece, offset)
                        }
                    }
                    .onGloballyPositioned { layoutCoordinates ->
                        val offsetBox = layoutCoordinates.positionInRoot()
                        offset = offsetBox
                    }
            )
        }
    }
}

@Composable
fun AreaOfPuzzlePiece(puzzlePiece: PuzzlePiece, setSnapZone: (SnapZone) -> Unit){
    if(puzzlePiece.piece == null){
        Box(
            modifier = Modifier
                .background(Color.Gray)
                .size(50.dp)
                .onGloballyPositioned {
                    val offset = it.positionInRoot()
                    setSnapZone(SnapZone(offset, puzzlePiece.position!!))
                }
        )
    }else{
        Image(
            bitmap = puzzlePiece.piece.asImageBitmap(),
            contentDescription = "",
            modifier = Modifier.size(50.dp)
        )
    }
}


