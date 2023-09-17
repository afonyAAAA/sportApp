package ru.fi.sportapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue.Companion.Saver
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.fi.sportapp.news.News
import ru.fi.sportapp.ui.theme.SportAppTheme

class MainActivity : ComponentActivity() {
    fun restartApp(context: Context){
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = applicationContext
        val appFirebase = AppFirebase()

        setContent {
            SportAppTheme {
                val viewModel = ViewModel(context)

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ){
                    if(viewModel.localUrl.isNotEmpty()){
                        if(viewModel.isInternetAvailable())
                            WebView(url = viewModel.localUrl)
                        else
                            NeedInternet {
                                restartApp(context)
                            }
                    }else if(!viewModel.isInternetAvailable()){
                        NeedInternet {
                            restartApp(context)
                        }
                    }else{
                        LaunchedEffect(viewModel.url){
                            val result = appFirebase.getUrl()

                            if(result.second || !viewModel.isInternetAvailable()){
                                viewModel.stateAlertDialog = true
                            }

                            if(result.first.isNotEmpty() && !result.second && viewModel.phone){
                                viewModel.url = result.first
                                viewModel.saveUrl()
                            }
                        }

                        if(viewModel.phone && viewModel.url.isNotEmpty() && viewModel.isInternetAvailable()){
                            WebView(url = viewModel.url)
                        }else{
                            ReallyApp(viewModel = viewModel)
                        }

                        if(viewModel.stateAlertDialog){
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


@Composable
fun ReallyApp(viewModel: ViewModel){

    val context = LocalContext.current

    LaunchedEffect(viewModel.listNews){
        viewModel.getNews()
    }

    if(viewModel.listNews.isEmpty()){
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            CircularProgressIndicator()
        }
    }
    Column {
        Text(text = "Sports News", fontSize = 18.sp, modifier = Modifier.padding(12.dp))
        LazyColumn(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            items(items = viewModel.listNews){
                if(viewModel.listNews.isNotEmpty()) NewsItem(context = context, news = it)
            }
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
                .data(news.urlImage.replace("https", "http"))
                .size(Size.ORIGINAL)
                .crossfade(true)
                .build()
        )


        var imageIsLoading : Boolean by rememberSaveable {
            mutableStateOf(false)
        }
        var loadingTime by rememberSaveable {
            mutableStateOf(0L)
        }

        imageIsLoading = when(painter.state){
            AsyncImagePainter.State.Empty -> {
                painter.imageLoader.shutdown()
                false
            }
            is AsyncImagePainter.State.Loading -> {
                if(loadingTime == 0L){
                    loadingTime = System.currentTimeMillis()
                }
                if(System.currentTimeMillis() - loadingTime > 10000L){
                    painter.imageLoader.shutdown()
                }
                true
            }
            is AsyncImagePainter.State.Success -> {
                painter.imageLoader.shutdown()
                false
            }
            is AsyncImagePainter.State.Error -> {
                painter.imageLoader.shutdown()
                false
            }
        }


        Column{
            Box(
                modifier = Modifier.background(color = Color.Black.copy(0.2f)),
                contentAlignment = Alignment.BottomStart
            ){
                Column {
                    if(imageIsLoading){
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ){
                            CircularProgressIndicator(
                                color = Color.Black.copy(0.5f)
                            )
                        }
                    }else{
                        Image(
                            painter = painter,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.TopCenter,
                            modifier = Modifier.fillMaxWidth(),
                            colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.4f), BlendMode.Darken)
                        )
                    }
                }

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

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WebView(url : String){

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var searchIsComplete by rememberSaveable { mutableStateOf(false) }
    var searchText by rememberSaveable { mutableStateOf("") }
    var searchResult by rememberSaveable { mutableStateOf(0) }
    var targetMatches by rememberSaveable { mutableStateOf(0) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    var webView by remember{
        mutableStateOf(WebView(context))
    }
    val webViewState by rememberSaveable{
        mutableStateOf(Bundle())
    }
    var stateSearchAnimation by rememberSaveable {
        mutableStateOf(false)
    }

    val onSearch : () -> Unit = {
        coroutineScope.launch {

            keyboardController?.hide()

            delay(500L)

            webView.findAllAsync(searchText)

            webView.setFindListener { activeMatchOrdinal, numberOfMatches, isDoneCounting ->
                targetMatches = activeMatchOrdinal + 1

                if (isDoneCounting) {
                    searchIsComplete = true
                    searchResult = if (numberOfMatches > 0) {
                        numberOfMatches
                    } else {
                        0
                    }
                }
            }

            stateSearchAnimation = false
        }
    }

    val onDismiss : () -> Unit = {
        stateSearchAnimation = false
        searchResult = 0
        searchText = ""
        searchIsComplete = false
        webView.clearMatches()
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
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        isLoading = true
                        if(searchText.isNotEmpty())
                            onDismiss()
                        return super.shouldOverrideUrlLoading(view, request)
                    }
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

    LaunchedEffect(Unit){
        if (searchText.isNotEmpty()) {
            delay(2000L)
            onSearch()
        }
    }

    AnimatedVisibility(
        visible = stateSearchAnimation,
        enter = slideInVertically(initialOffsetY = {it}),
        exit = slideOutVertically(targetOffsetY = {it})
    ) {
        TextField(
            value = searchText,
            onValueChange = { newValue ->
                searchText = newValue
                searchIsComplete = false
                webView?.findAllAsync(searchText)
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            trailingIcon ={
                if(searchText.isNotEmpty()) IconButton(onClick = {
                   onSearch()
                }) {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = "")
                }else{
                    IconButton(onClick = {onDismiss()}) {
                        Icon(imageVector = Icons.Outlined.Close, contentDescription = "")
                    }
                }
            },
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    onSearch()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(0.5f))
                .padding(16.dp)
        )
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(searchResult != 0 && !stateSearchAnimation && searchIsComplete) {
            Button(
                onClick = {
                    webView.findNext(false)
                }
            ) {
                Text(text = "<")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    webView.findNext(true)
                }
            ) {
                Text(text = ">")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = { /*TODO*/ }) {
                Text(text = "$targetMatches / $searchResult")
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ){
            if(!stateSearchAnimation){
                IconButton(onClick = {
                    if (searchText.isEmpty()) {
                        stateSearchAnimation = true
                    } else {
                        onDismiss()
                    }
                }, modifier = Modifier
                    .size(60.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(16.dp)
                ) {
                    if(searchText.isEmpty()){
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }else{
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Search",
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }
        }
    }

    if(searchText.isNotEmpty() &&
        searchResult == 0 &&
        searchIsComplete &&
        !stateSearchAnimation
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.BottomStart
        ){
            Button(onClick = { }) {
                Text(text = "Matches not found")
            }
        }
    }

    DisposableEffect(Unit){
        onDispose {
            webView.saveState(webViewState)
        }
    }

    BackHandler {
        if(searchText.isNotEmpty()){
            onDismiss()
            webView.goBack()
        }else{
            webView.goBack()
        }
    }
}


