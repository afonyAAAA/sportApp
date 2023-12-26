package com.boundless.GIGABET.wonders.screens.assemblyPuzzle

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.boundless.GIGABET.wonders.R
import com.boundless.GIGABET.wonders.event.UiEventPuzzleAssembly
import com.boundless.GIGABET.wonders.models.PuzzlePiece
import com.boundless.GIGABET.wonders.models.SnapZone
import com.boundless.GIGABET.wonders.navigation.Screens
import com.boundless.GIGABET.wonders.utils.HelperApp
import kotlin.math.roundToInt


@Composable
fun PuzzleScreen(navHostController: NavHostController){

    val context = LocalContext.current

    val viewModel = remember {
        AssemblyPuzzleViewModel(
            context = context,
            selectedPuzzle = HelperApp.Puzzle.puzzle,
            settings = HelperApp.Settings.state
        )
    }

    val state = viewModel.stateAssemblyPuzzle

    val onSetSnapZone : (SnapZone) -> Unit = remember  {
        { snapZone ->
            viewModel.onEventAssembly(UiEventPuzzleAssembly.SetSnapZone(snapZone))
        }
    }

    val onTapPuzzlesList : (PuzzlePiece, Offset) -> Unit = remember  {
        { puzzlePiece, offSet ->
            viewModel.onEventAssembly(UiEventPuzzleAssembly.OnTapPiecePuzzle(offSet, puzzlePiece))
        }
    }

    val onDragStart : (Int) -> Unit = remember  {
        { index ->
            viewModel.onEventAssembly(UiEventPuzzleAssembly.OnDragStart(index))
        }
    }

    val onDragEnd : (Int) -> Unit = remember  {
        { index ->
            viewModel.onEventAssembly(UiEventPuzzleAssembly.DragEndPiecePuzzle(index))
        }
    }

    val onDrag : (Offset, PointerInputChange, PuzzlePiece, Int) -> Unit = remember {
        { dragAmount, change, puzzle, index ->
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

    val onDismiss = remember {
        {
            navHostController.navigate(
                Screens.Puzzles.route,
                NavOptions.Builder()
                    .setPopUpTo(Screens.Main.route, false)
                    .build()
            )
            viewModel.onEventAssembly(UiEventPuzzleAssembly.ResetAssemblyPuzzle)
        }
    }

    val onDismissOnVictory = remember {
        {
            viewModel.onEventAssembly(UiEventPuzzleAssembly.PuzzleIsCompleted)
        }
    }

    val onBackPressed = remember  {
        {
            navHostController.popBackStack()
            viewModel.onEventAssembly(UiEventPuzzleAssembly.ResetAssemblyPuzzle)
        }
    }

    val onNextRow = remember {
        {
            viewModel.onEventAssembly(UiEventPuzzleAssembly.NextRow)
        }
    }

    val onGloballySetPosition : (LayoutCoordinates) -> Unit = remember {
        {   layoutCoordinates ->
            val offsetBox = layoutCoordinates.positionInRoot()
            viewModel.onEventAssembly(UiEventPuzzleAssembly.SetOffSet(offsetBox))
        }
    }

    Image(
        painter = painterResource(id = R.drawable.background),
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        contentDescription = ""
    )

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(state.timerIsRunning){
            ProgressIndicator(progress = (state.totalTime.toFloat() / 300f) * 1.0f)
        }

        PositionsForPuzzles(
            positionsPiecePuzzles = state.positionsPiecePuzzles,
            onSetSnapZone = onSetSnapZone,
            counter = state.counter,
            onNextRow = onNextRow
        )

        PuzzlesList(
            state.piecesPuzzle,
            offset = state.offSetPuzzle,
            onGloballySetPosition = onGloballySetPosition,
            onTap = onTapPuzzlesList
        )
    }

    DraggedPuzzles(
        state.selectedPiecesPuzzle,
        onDragEnd = onDragEnd,
        onDragStart = onDragStart,
        onDrag = onDrag
    )

    if(state.totalTime == 0 || state.isVictory)
        ResultsAssemblyPuzzle(
            onDismiss = onDismiss,
            isVictory = state.isVictory,
            onDismissOnVictory = onDismissOnVictory,
            choosePuzzlePiece = state.selectedPuzzle.image!!
        )

    BackHandler(onBack = onBackPressed)
}

@Composable
fun PositionsForPuzzles(
    positionsPiecePuzzles: MutableList<PuzzlePiece>,
    onSetSnapZone: (SnapZone) -> Unit,
    counter : Int,
    onNextRow : () -> Unit
){
    listOf(1, 2, 3, 4, 5).forEach { _ ->
        Column {
            Row {
                positionsPiecePuzzles.subList(counter, counter + 5).forEach{ positionInRow ->
                    AreaOfPuzzlePiece(positionInRow){ snapZone ->
                        onSetSnapZone(snapZone)
                    }
                }
            }
        }
    }
    if(counter < 20){
        onNextRow()
    }
}

@Composable
fun ResultsAssemblyPuzzle(
    isVictory : Boolean,
    onDismiss: () -> Unit,
    onDismissOnVictory : () -> Unit,
    choosePuzzlePiece: Bitmap
){
    val isDefeat = !isVictory

    if (isDefeat){
        AlertDialogDefeat(
            onDismiss = onDismiss
        )
    }

    if(isVictory){
        AlertDialogVictory(
            onDismiss = onDismiss,
            completedPuzzle = choosePuzzlePiece
        )
        onDismiss()
        onDismissOnVictory()
    }
}

@Composable
fun DraggedPuzzles(
    selectedPiecesPuzzle: MutableList<PuzzlePiece>,
    onDragEnd : (Int) -> Unit,
    onDragStart : (Int) -> Unit,
    onDrag : (Offset, PointerInputChange, PuzzlePiece, Int) -> Unit
){
    selectedPiecesPuzzle.forEachIndexed { index, puzzle ->
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
                            onDragEnd(index)
                        },
                        onDragStart = {
                            onDragStart(index)
                        }
                    ) { change, dragAmount ->
                        onDrag(
                            dragAmount,
                            change,
                            puzzle,
                            index
                        )
                    }
                }
                .size(50.dp)
        )
    }
}

@Composable
fun PuzzlesList(
    piecesPuzzle:  MutableList<PuzzlePiece>,
    offset: Offset,
    onTap: (PuzzlePiece, Offset) -> Unit,
    onGloballySetPosition: (LayoutCoordinates) -> Unit
){
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        contentPadding = PaddingValues(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        verticalArrangement = Arrangement.Center
    ){
        items(
            piecesPuzzle,
            key = { listItem ->
                listItem.id
            }
        ){ piece ->
            com.boundless.GIGABET.wonders.screens.assemblyPuzzle.PuzzlePiece(
                puzzlePiece = piece,
                offset = offset,
                onGloballySetPosition = onGloballySetPosition,
                onTap = { puzzlePiece, offSet ->
                    onTap(puzzlePiece, offSet)
                }
            )
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
    onTap : (PuzzlePiece, Offset) -> Unit,
    onGloballySetPosition : (LayoutCoordinates) -> Unit,
    offset: Offset
){
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
                        onGloballySetPosition(layoutCoordinates)
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


