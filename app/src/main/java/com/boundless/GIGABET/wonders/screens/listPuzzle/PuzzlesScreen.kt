package com.boundless.GIGABET.wonders.screens.listPuzzle

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.boundless.GIGABET.wonders.R
import com.boundless.GIGABET.wonders.event.UiEventPuzzleChoose
import com.boundless.GIGABET.wonders.models.Image
import com.boundless.GIGABET.wonders.navigation.Screens
import com.boundless.GIGABET.wonders.states.StateChoosePuzzle
import com.boundless.GIGABET.wonders.utils.HelperApp

val stateSettings = HelperApp.Settings.state

@Composable
fun PuzzlesScreen(navHostController: NavHostController){

    val context = LocalContext.current
    val viewModel = remember { ChoosePuzzleViewModel(context)}
    val state = viewModel.stateChoosePuzzle
    val onSelectImage : (Image) -> Unit = remember(viewModel) {
        { selectedImage ->
            HelperApp.Puzzle.puzzle = selectedImage
            viewModel.onEventChoosePuzzle(UiEventPuzzleChoose.ImageIsChoose(selectedImage))
            navHostController.navigate(
                Screens.Puzzle.route
            )
        }
    }

    Image(
        painter = painterResource(id = R.drawable.background),
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        contentDescription = ""
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .height(80.dp)
                .width(100.dp),
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Text(text = "Choose puzzle", textAlign = TextAlign.Center)
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
        ListImages(
            listImage = state.listImage,
            onClick = onSelectImage
        )
    }
}

@Composable
fun ListImages( listImage: List<Image>, onClick: (Image) -> Unit){
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ){
        items(listImage){ image ->
            ItemImage(item = image, settingsVisibleImage = stateSettings.imageIsVisible) {
                onClick(image)
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemImage(item : Image, settingsVisibleImage : Boolean, onClick: () -> Unit){
    OutlinedCard(
        onClick = {},
        modifier = Modifier.size(200.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically)
        ) {
            if(item.visible || settingsVisibleImage){
                Image(bitmap = item.image!!.asImageBitmap(), contentDescription = "",
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .size(100.dp))
            }else{
                Image(
                    painter = painterResource(id = R.drawable.question_mark_draw_svgrepo_com),
                    contentDescription = "",
                    modifier = Modifier
                        .size(100.dp)
                )
            }
            Button(onClick = { onClick() }) {
                Text(text = "Collect")
            }
        }
    }
}