package ru.fi.sportapp.screens

import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.fi.sportapp.Helper
import ru.fi.sportapp.models.Article
import ru.fi.sportapp.models.Casino
import ru.fi.sportapp.navigation.Screens
import ru.fi.sportapp.viewModels.MainViewModel

@Composable
fun MainScreen(navHostController: NavHostController, mainViewModel: MainViewModel){
    Column {
        var shouldShowCasinos by remember {
            mutableStateOf(true)
        }

        AnimatedVisibility(visible = shouldShowCasinos) {
            CasinosFragment(
                viewModel = mainViewModel,
                navHostController = navHostController
            )
        }
        Button(
            onClick = { shouldShowCasinos = !shouldShowCasinos},
            modifier = Modifier
                .width(200.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Icon(imageVector = if(shouldShowCasinos) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown, contentDescription = "")
        }
        ArticleFragment(article = mainViewModel.mainArticle)
    }
}

@Composable
fun CasinosFragment(viewModel: MainViewModel, navHostController: NavHostController){
    LazyRow(horizontalArrangement = Arrangement.Center){
        items(viewModel.casinos){ casino ->
            CardCasino(casino){
                if(!Helper.isClickedCardCasino){
                    Helper.isClickedCardCasino = true
                    Helper.selectedCasino = casino
                    navHostController.navigate(Screens.DescriptionCasino.route)
                }
            }
        }
    }
}
@Composable
fun ArticleFragment(article: List<Article>){
    LazyColumn(
        modifier = Modifier.padding(8.dp)
    ){
        items(article){ article ->
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
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(article.urlImage.first())
                        .crossfade(true)
                        .build()
                )

                Image(
                    painter = painter,
                    contentDescription = ""
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CardCasino(
    casino : Casino,
    onClick : () -> Unit
){

    val density = LocalDensity.current.density
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val screenWidthPx = with(LocalDensity.current) {
        (view.width / density).dp
    }

    Card(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .padding(8.dp)
            .width(screenWidthPx - 5.dp)
            .heightIn(275.dp, 300.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Column(
            modifier = Modifier
                .padding(17.dp)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(25.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var stateLoadImage by rememberSaveable{
                    mutableStateOf(true)
                }

                LaunchedEffect(Unit){
                    delay(1000)
                    stateLoadImage = false
                }

                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(casino.urlImageCasino)
                        .size(size = Size.ORIGINAL)
                        .crossfade(true)
                        .build()
                )

                if(stateLoadImage){
                    Box(
                        modifier = Modifier
                            .placeholder(
                                visible = true,
                                color = MaterialTheme.colorScheme.onPrimary,
                                highlight = PlaceholderHighlight.shimmer(
                                    highlightColor = MaterialTheme.colorScheme.primary.copy(
                                        0.5f
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(18.dp)
                            .width(110.dp)
                            .height(100.dp)
                    ){}
                }else{
                    Surface(modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                        .size(180.dp), color = MaterialTheme.colorScheme.primary) {

                        GlideImage(
                            model = casino.urlImageCasino,
                            contentDescription = ""
                        )
//                        Image(
//                            painter = painter,
//                            contentDescription = "",
//                            modifier = Modifier
//                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
//                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .heightIn(100.dp, 175.dp)
                        .verticalScroll(rememberScrollState())
                ){
                    Text(
                        text = casino.shortDescription,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(contentAlignment = Alignment.BottomStart, modifier = Modifier.fillMaxHeight()){
                Text(
                    text = casino.nameCasino,
                    fontSize = 24.sp,
                    color = Color.Yellow,
                    fontWeight = FontWeight(800)
                )
            }
        }
    }
}