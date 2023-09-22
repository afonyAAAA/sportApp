package trustyapp.gameskud.sn

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import trustyapp.gameskud.sn.ui.theme.SnTheme

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
            var isLoading by rememberSaveable { mutableStateOf(true) }

            SnTheme {
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
                            isLoading = false

                            if(result.second || !viewModel.isInternetAvailable()){
                                viewModel.stateAlertDialog = true
                            }

                            if(result.first.isNotEmpty() && !result.second && viewModel.phone){
                                viewModel.url = result.first
                                viewModel.saveUrl()
                            }
                        }

//                        LaunchedEffect(Unit){
//                            if(!viewModel.isInternetAvailable()){
//                                viewModel.stateAlertDialog = true
//                            }
//                        }

                        if(isLoading){
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Image(
                                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                                    contentDescription = "",
                                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                )

                                Spacer(modifier = Modifier.height(25.dp))

                                CircularProgressIndicator()
                            }
                        }

                        if(viewModel.phone && viewModel.url.isNotEmpty() && viewModel.isInternetAvailable() && !isLoading){
                            WebView(url = viewModel.url)
                        }

                        if(!viewModel.phone || viewModel.url.isEmpty() && viewModel.isInternetAvailable() && !isLoading){
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

    if(viewModel.listNews.isEmpty()) viewModel.getNews(context.resources)

    if(viewModel.listNews.isEmpty()){
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            CircularProgressIndicator()
        }
    }
    Column {

        Text(text = "Sports News", fontSize = 18.sp, modifier = Modifier.padding(12.dp))

        if(!viewModel.openedDescriptionNews){
            LazyColumn(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                items(items = viewModel.listNews){ newsItem ->
                    if(viewModel.listNews.isNotEmpty())
                        NewsItem(context = context, news = newsItem){ chooseNews ->
                        viewModel.chooseNews = chooseNews
                        viewModel.openedDescriptionNews = true
                    }
                }
            }
        }else{
            DescriptionNews(news = viewModel.chooseNews){
                viewModel.openedDescriptionNews = false
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DescriptionNews(news : News, onBackPressed : () -> Unit){
    @Composable
    fun PhotoTile(
        photoUrl: String,
        isLarge: Boolean,
        onClick: () -> Unit
    ) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photoUrl)
                .size(Size.ORIGINAL)
                .crossfade(true)
                .build(),
        )
        val modifier = if (isLarge) {
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { onClick() }
        } else {
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clickable { onClick() }
        }

        Box(
            modifier = modifier
                .padding(4.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = if (isLarge) ContentScale.Crop else ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            when(painter.state){
                AsyncImagePainter.State.Empty -> {

                }
                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                is AsyncImagePainter.State.Success -> {

                }
                is AsyncImagePainter.State.Error -> {

                }
            }
        }
    }

    @Composable
    fun LargePhotoDialog(
        photoUrl: String,
        onClose: () -> Unit
    ) {
        Dialog(
            onDismissRequest = { onClose() },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {

            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = photoUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .scale(1.1f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                IconButton(
                    onClick = { onClose() },
                    modifier = Modifier
                        .padding(16.dp)
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.onSecondary, CircleShape)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

    }

    Surface(modifier = Modifier.fillMaxSize()) {

        var selectedPhoto by rememberSaveable {
            mutableStateOf<String?>(null)
        }
        var imagesIsShow by rememberSaveable {
            mutableStateOf(false)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
        ){
            AnimatedVisibility(visible = imagesIsShow, label = "") {
                LazyHorizontalGrid(
                    rows = GridCells.Adaptive(125.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(news.urlImages) { photoUrl ->
                        val isLargePhoto = photoUrl == news.urlImages.first()
                        PhotoTile(
                            photoUrl = photoUrl,
                            isLarge = isLargePhoto,
                            onClick = {
                                selectedPhoto = photoUrl
                            }
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = news.title, fontSize = 26.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "           " + news.description)

                Spacer(modifier = Modifier.height(20.dp))

                Spacer(Modifier.height(100.dp))

                selectedPhoto?.let { url ->
                    LargePhotoDialog(
                        photoUrl = url,
                        onClose = {
                            selectedPhoto = null
                        }
                    )
                }
            }
        }

        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.padding(16.dp)){
            Button(onClick = {imagesIsShow = !imagesIsShow}) {
                Text(text = if(!imagesIsShow) "Show images" else "Hide images")
            }
        }
        Box(contentAlignment = Alignment.BottomStart, modifier = Modifier.padding(16.dp)){
            Button(onClick = {onBackPressed()}) {
                Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "")
            }
        }

        BackHandler {
            onBackPressed()
        }

    }
}



@Composable
fun NewsItem(context : Context, news : News, onClick : (News) -> Unit){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .clickable {
                onClick(news)
            },
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {

        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(news.urlImages[0])
                .size(Size.ORIGINAL)
                .crossfade(true)
                .build()
        )
        var onErrorOrNotLoading : Boolean by rememberSaveable {
            mutableStateOf(false)
        }
        var imageIsLoading : Boolean by rememberSaveable {
            mutableStateOf(false)
        }
        var loadingTime by rememberSaveable {
            mutableStateOf(0L)
        }

         when(painter.state){
            AsyncImagePainter.State.Empty -> {
                imageIsLoading = false
                onErrorOrNotLoading = true
            }
            is AsyncImagePainter.State.Loading -> {
                if(loadingTime == 0L){
                    loadingTime = System.currentTimeMillis()
                }
                if(System.currentTimeMillis() - loadingTime > 10000L){
                    onErrorOrNotLoading = true
                }
                imageIsLoading = true
            }
            is AsyncImagePainter.State.Success -> {
                imageIsLoading = false
            }
            is AsyncImagePainter.State.Error -> {
                painter.imageLoader.shutdown()
                onErrorOrNotLoading = true
                imageIsLoading = false
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
                    }else if(onErrorOrNotLoading){
                        Icon(imageVector = Icons.Outlined.Warning, contentDescription = "")
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

            Text(text = news.description.substring(0..80) + "...", Modifier.padding(10.dp))

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
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
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
    DisposableEffect(Unit){
        onDispose {
            webView.saveState(webViewState)
        }
    }

    BackHandler {
        if(webView.canGoBack()) webView.goBack()
    }
}


