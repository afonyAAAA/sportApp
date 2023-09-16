package ru.fi.sportapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import ru.fi.sportapp.ui.theme.SportAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lateinit var webView : WebView
        val viewModel = viewModel()

        setContent {
            SportAppTheme {

                ReallyApp(viewModel = viewModel)

//                Box(modifier = Modifier.fillMaxSize()){
//                    var url by remember { mutableStateOf("https://stevdza-san.com") }
//                    val webViewState = rememberSaveable(saver = autoSaver()) {
//                        Bundle()
//                    }
//
//                    AndroidView(
//                        modifier = Modifier.fillMaxSize(),
//                        factory = { context ->
//                            WebView(context).apply {
//                                webViewClient = WebViewClient()
//
//                                if(!webViewState.isEmpty){
//                                    restoreState(webViewState)
//                                    webView = this
//                                }else{
//                                    webView = this
//                                    loadUrl(url)
//                                }
//
//                            }
//                        },
//                        update = {
//                            webView.restoreState(webViewState)
//                        }
//                    )
//
//                    DisposableEffect(Unit){
//                        onDispose {
//                            webView.saveState(webViewState)
//                        }
//                    }
//
//
//                    BackHandler {
//                        if(webView.canGoBack()){
//                            webView.goBack()
//                        }
//                    }
//                }
            }
        }
    }
}

@Composable
fun ReallyApp(viewModel: viewModel){

    val context = LocalContext.current

    LaunchedEffect(viewModel.listNews){
        viewModel.getNews()
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        item { 
            Text(text = "Sports News")
            if(viewModel.listNews.isEmpty()){
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    CircularProgressIndicator()
                }
            }
        }
        items(items = viewModel.listNews){
            if(viewModel.listNews.isNotEmpty()) NewsItem(context = context, news = it)
        }
    }
}
@Composable
fun NewsItem(context : Context, news : News){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.url))
                context.startActivity(intent)
            },
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {

        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(news.url.replace("https", "http"))
                .size(Size.ORIGINAL)
                .crossfade(true)
                .build()
        )

        Column{
            Box(
                modifier = Modifier.background(color = Color.Black.copy(0.5f)),
                contentAlignment = Alignment.BottomStart
            ){
                Image(
                    painter = painter,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter,
                    modifier = Modifier.fillMaxWidth(),
                    colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.4f), BlendMode.Darken)
                )
                Text(
                    text = news.title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom =5.dp))
            }

            Text(text = news.description, Modifier.padding(10.dp))

        }
    }
}


