package ru.fi.sportapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
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
            modifier = Modifier.width(200.dp).align(Alignment.CenterHorizontally)
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
                Helper.selectedCasino = casino
                navHostController.navigate(Screens.DescriptionCasino.route)
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



@Composable
fun CardCasino(
    casino : Casino,
    onClick : () -> Unit
){

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(casino.urlImageCasino)
            .build(),
        onState = { state ->
            when(state){
                AsyncImagePainter.State.Empty -> {}
                is AsyncImagePainter.State.Loading -> {}
                is AsyncImagePainter.State.Success -> {}
                is AsyncImagePainter.State.Error -> {}
            }
        }
    )

    Card(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .padding(8.dp)
            .fillMaxWidth()
            .width(300.dp)
            .heightIn(240.dp, 270.dp),
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Top
            ) {
                val cornerShape = RoundedCornerShape(20.dp)

                Image(
                    painter = painter,
                    contentDescription = "",
                    modifier = Modifier
                        .size(150.dp)
                        .background(MaterialTheme.colorScheme.onPrimary, cornerShape)
                        .clip(cornerShape)
                )
                Text(
                    text = casino.shortDescription,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = casino.nameCasino,
                fontSize = 24.sp,
                color = Color.Yellow,
                fontWeight = FontWeight(800)
            )
        }
    }
}