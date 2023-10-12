package ru.fi.sportapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import ru.fi.sportapp.viewModels.MainViewModel

@Composable
fun MainScreen(navHostController: NavHostController, mainViewModel: MainViewModel){
    LazyRow{
        items(5){
            CardCasino()
        }
    }

}


@Composable
fun ArticleOfCasino(){

//    Text(
//        text = stringResource(R.string.golden_age_of_hollywood),
//        fontSize = 24.sp,
//        fontWeight = FontWeight.SemiBold,
//        modifier = Modifier
//            .padding(top = 10.dp)
//            .align(Alignment.CenterHorizontally)
//    )



//        Column(
//            modifier = Modifier.padding(3.dp)
//        ) {
//
//            if(subTopic.name.isNotBlank()){
//                Text(
//                    text = subTopic.name,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    modifier = Modifier
//                        .padding(start = 13.dp)
//                )
//            }
//
//            Text(buildAnnotatedString {
//                withStyle(SpanStyle(fontSize = 50.sp, fontFamily = FontFamily.Serif)){
//                    append(subTopic.text.first())
//                }
//                append(subTopic.text.drop(1))
//            })
//
//            if(subTopic.name == "Legendary Stars"){
//                ActorsItem(actor = viewModel.actors.first { it.name == "Marilyn Monroe"})
//
//                ActorsItem(actor = viewModel.actors.first { it.name == "Audrey Hepburn"})
//            }
//        }

}

@Composable
fun CardCasino(){

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data("")
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
            .padding(8.dp)
            .fillMaxWidth()
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
                    text = "fdfdsfsdfsdfsdfsfdsfdsdf",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Казино",
                fontSize = 24.sp,
                fontWeight = FontWeight(800)
            )
        }
    }
}