package ru.fi.sportapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.android.style.PlaceholderSpan
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import ru.fi.sportapp.model.Actor
import ru.fi.sportapp.ui.theme.SportAppTheme
import ru.fi.sportapp.viewModels.MainViewModel
import ru.fi.sportapp.viewModels.ArticleViewModel

class MainActivity : ComponentActivity() {
    private fun restartApp(context: Context){
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = applicationContext
        val appFirebase = AppFirebase()
        val mainViewModel = MainViewModel(context)
        val articleViewModel = ArticleViewModel(context)

        setContent {
            SportAppTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ){
                    if(mainViewModel.localUrl.isNotEmpty()){
                        if(mainViewModel.isInternetAvailable())
                            WebView(url = mainViewModel.localUrl)
                        else
                            NeedInternet {
                                restartApp(context)
                            }
                    }else if(!mainViewModel.isInternetAvailable()){
                        NeedInternet {
                            restartApp(context)
                        }
                    }else{
                        LaunchedEffect(mainViewModel.url){
                            val result = appFirebase.getUrl()
                            mainViewModel.isLoading = false

                            if(result.second || !mainViewModel.isInternetAvailable()){
                                mainViewModel.stateAlertDialog = true
                            }

                            if(result.first.isNotEmpty() && !result.second && mainViewModel.phone){
                                mainViewModel.url = result.first
                                mainViewModel.saveUrl()
                            }
                        }


                        if(mainViewModel.isLoading){
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = "",
                                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                )

                                Spacer(modifier = Modifier.height(25.dp))

                                CircularProgressIndicator()
                            }
                        }

                        if(mainViewModel.phone && mainViewModel.url.isNotEmpty() && mainViewModel.isInternetAvailable() && !mainViewModel.isLoading){
                            WebView(url = mainViewModel.url)
                        }

                        if(!mainViewModel.phone || mainViewModel.url.isEmpty() && mainViewModel.isInternetAvailable() && !mainViewModel.isLoading){
                            ReallyApp(viewModel = articleViewModel)
                        }

                        if(mainViewModel.stateAlertDialog){
                            NeedInternet(
                                onDismiss = {
                                    restartApp(context)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ReallyApp(viewModel: ArticleViewModel){

    val scrollState = rememberScrollState()
    var maxScroll by rememberSaveable { mutableStateOf(0) }

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(R.drawable.background_app)
            .size(Size.ORIGINAL)
            .crossfade(true)
            .build()
    )

    if(!viewModel.isStartQuiz && !viewModel.isShowActors){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {

            Image(
                painter = painter,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            LaunchedEffect(scrollState.value){
                if(scrollState.value > maxScroll) maxScroll = scrollState.value
            }

            Text(
                text = stringResource(R.string.golden_age_of_hollywood),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally)
            )

            viewModel.subTopics.forEach { subTopic ->
                AnimationOfTextAppearance(state = true) {
                    Column(
                        modifier = Modifier.padding(3.dp)
                    ) {

                        if(subTopic.name.isNotBlank()){
                            Text(
                                text = subTopic.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .padding(start = 13.dp)
                            )
                        }

                        Text(buildAnnotatedString {
                            withStyle(SpanStyle(fontSize = 50.sp, fontFamily = FontFamily.Serif)){
                                append(subTopic.text.first())
                            }
                            append(subTopic.text.drop(1))
                        })
                    }
                }
            }

            Button(
                onClick = {viewModel.isShowActors = true},
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(65.dp)
                    .width(150.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ){
                    Text(text = "Actors")
                    Icon(imageVector = Icons.Filled.Person, contentDescription = "")
                }

            }

            Button(
                onClick = { viewModel.isStartQuiz = true },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(65.dp)
                    .width(150.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    Text(text = "Quiz")
                    Icon(imageVector = Icons.Filled.Star, contentDescription = "")
                }
            }

            Spacer(modifier = Modifier.height(25.dp))
        }
    }else if(viewModel.isShowActors){
        Actors(actors = viewModel.actors){viewModel.isShowActors = false}
    }else{
        Quiz()
    }
}

@Composable
fun ActorsItem(actor : Actor){

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(actor.urlToImage)
            .size(Size.ORIGINAL)
            .crossfade(true)
            .build()
    )

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ){
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painter,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .size(130.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = actor.name, fontWeight = FontWeight.Black)
            }
            Text(
                text = actor.description,
                color = Color.Black,
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
fun Actors(
    actors : SnapshotStateList<Actor>,
    onClickBackArrow: () -> Unit
){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        items(actors){actor ->
            ActorsItem(actor = actor)
        }
    }
    Box(contentAlignment = Alignment.BottomStart, modifier = Modifier.fillMaxSize()){
        Button(
            onClick = { onClickBackArrow() },
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "",
                modifier = Modifier.height(25.dp)
            )
        }
    }
    BackHandler {
        onClickBackArrow()
    }
}
@Composable
fun Quiz(){
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface {

        }
        LazyColumn{

        }
    }
}

@Composable
fun AnimationOfTextAppearance(
    state : Boolean,
    content : @Composable () -> Unit
){
    AnimatedVisibility(
        visible = state,
        enter = slideInHorizontally(),
        exit = slideOutHorizontally()
    ) {
       content()
    }
}

@Composable
fun NeedInternet(onDismiss : () -> Unit){
    AlertDialog(
        title = {
            Text(text = "Exception")
        },
        text = {
            Text(text = "An internet connection is required for the application to work.")
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            Button(onClick = { onDismiss()}) {
                Text(text = "Restart")
            }
        }
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(url : String){

    val context = LocalContext.current
    var isLoading by rememberSaveable { mutableStateOf(false) }

    var webView by remember{
        mutableStateOf(WebView(context))
    }
    val webViewState by rememberSaveable{
        mutableStateOf(Bundle())
    }

    if(isLoading){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient(){
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        isLoading = false
                    }
                }
                val cookieManager: CookieManager = CookieManager.getInstance()
                val webSettings = this.settings

                webSettings.javaScriptEnabled = true
                webSettings.loadWithOverviewMode = true
                webSettings.useWideViewPort = true
                webSettings.domStorageEnabled = true
                webSettings.databaseEnabled = true
                webSettings.setSupportZoom(false)
                webSettings.allowFileAccess = true
                webSettings.allowContentAccess = true
                webSettings.useWideViewPort = true
                cookieManager.setAcceptCookie(true)

                if(!webViewState.isEmpty){
                    restoreState(webViewState)
                    webView = this
                }else{
                    webView = this
                    loadUrl(url)
                }
            }
        },
        update = {
            webView.restoreState(webViewState)
        }
    )

    DisposableEffect(Unit){
        onDispose {
            webView.saveState(webViewState)
        }
    }

    BackHandler {
        if(webView.canGoBack()) webView.goBack()
    }
}


