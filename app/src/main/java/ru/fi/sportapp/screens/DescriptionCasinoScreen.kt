package ru.fi.sportapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import ru.fi.sportapp.Helper

@Composable
fun DescriptionCasinoScreen(navHostController: NavHostController) {

    val selectedCasino = Helper.selectedCasino

    LazyColumn(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ){
        item {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(selectedCasino.urlImageCasino)
                    .crossfade(true)
                    .build()
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedCasino.nameCasino,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )

                Image(
                    painter = painter,
                    contentDescription = "",
                    alignment = Alignment.TopCenter
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
                                contentDescription = ""
                            )

                        }
                    }
                }
            }
        }
    }
}