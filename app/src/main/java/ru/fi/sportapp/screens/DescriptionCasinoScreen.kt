package ru.fi.sportapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.fi.sportapp.Helper

@Composable
fun DescriptionCasinoScreen(navHostController: NavHostController) {

    val selectedCasino = Helper.selectedCasino

    var stateAnimation by rememberSaveable {
        mutableStateOf(false)
    }

    var stateLoadImage by rememberSaveable {
        mutableStateOf(true)
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit){
        if(!stateAnimation){
            scope.launch(Dispatchers.IO) {
                delay(2500)
                stateAnimation = true
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Box(
            modifier = Modifier
                .padding(13.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .heightIn(150.dp, 200.dp),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = selectedCasino.nameCasino,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Yellow
            )
        }
    }


    AnimatedVisibility(
        visible = stateAnimation,
        enter = scaleIn(animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ))
    ) {
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
                .fillMaxSize()
        ){
            item {
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(selectedCasino.urlImageCasino)
                        .crossfade(true)
                        .build(),
                    onState = { state ->
                        stateLoadImage = when(state){
                            AsyncImagePainter.State.Empty -> {
                                true
                            }
                            is AsyncImagePainter.State.Loading -> {
                                true
                            }
                            is AsyncImagePainter.State.Success -> {
                                false
                            }
                            is AsyncImagePainter.State.Error -> {
                                true
                            }
                        }
                    }
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = selectedCasino.nameCasino,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(20.dp)
                    )

                    Image(
                        painter = painter,
                        contentDescription = "",
                        alignment = Alignment.TopCenter,
                        modifier = Modifier
                            .padding(18.dp)
                            .placeholder(
                                visible = stateLoadImage,
                                color = MaterialTheme.colorScheme.onPrimary,
                                highlight = PlaceholderHighlight.shimmer(
                                    highlightColor = MaterialTheme.colorScheme.primary.copy(
                                        0.5f
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clip(RoundedCornerShape(20.dp))
                            .fillMaxWidth()
                            .heightIn(200.dp, 300.dp)
                    )

                }
            }
            items(selectedCasino.articles){ article ->
                Column {
                    Text(
                        text = article.nameArticle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(start = 13.dp, top = 13.dp)
                    )

                    Text(buildAnnotatedString {
                        withStyle(SpanStyle(fontSize = 50.sp, fontFamily = FontFamily.Serif)){
                            append(article.textArticle.first())
                        }
                        append(article.textArticle.drop(1))
                    })

                    if(article.urlImage.isNotEmpty()){
                        LazyRow{
                            items(article.urlImage){ imageUrl ->
                                val painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(imageUrl)
                                        .crossfade(true)
                                        .build()
                                )

                                Image(
                                    painter = painter,
                                    contentDescription = "",
                                    alignment = Alignment.TopCenter,
                                    modifier = Modifier
                                        .placeholder(
                                            visible = stateLoadImage,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            highlight = PlaceholderHighlight.shimmer(
                                                highlightColor = MaterialTheme.colorScheme.primary.copy(
                                                    0.5f
                                                )
                                            ),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .padding(13.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .fillMaxWidth()
                                        .heightIn(200.dp, 250.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.heightIn(50.dp))
                }
            }
        }
    }
}