package ru.fi.colorGame

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import ru.fi.colorGame.ui.theme.SportAppTheme
import ru.fi.colorGame.viewModels.GameViewModel
import ru.fi.colorGame.viewModels.MainViewModel
import ru.fi.sportapp.R

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
        val gameViewModel = GameViewModel()
        val viewModel = MainViewModel(context)

        setContent {
            SportAppTheme {
                Box(
                    modifier = Modifier.fillMaxSize()
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
                            viewModel.isLoading = false

                            if(result.second || !viewModel.isInternetAvailable()){
                                viewModel.stateAlertDialog = true
                            }

                            if(result.first.isNotEmpty() && !result.second && viewModel.phone){
                                viewModel.url = result.first
                                viewModel.saveUrl()
                            }
                        }


                        if(viewModel.isLoading){
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

                        if(viewModel.phone && viewModel.url.isNotEmpty() && viewModel.isInternetAvailable() && !viewModel.isLoading){
                            WebView(url = viewModel.url)
                        }

                        if(!viewModel.phone || viewModel.url.isEmpty() && viewModel.isInternetAvailable() && !viewModel.isLoading){
                            ReallyApp(viewModel = gameViewModel)
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

enum class GameStatus {
    PLAYING,
    WIN,
    LOSE
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class, ExperimentalTextApi::class)
@Composable
fun ReallyApp(viewModel: GameViewModel){


    val context = LocalContext.current
    val aniSpecFloat = tween<Float>(durationMillis = 500, delayMillis = 300)

    if(!viewModel.isStartGame){
        WelcomeFragment(text = "Color Game"){
            viewModel.isStartGame = true
        }
    }

    AnimatedVisibility(
        visible = viewModel.isStartGame,
        enter = fadeIn(aniSpecFloat),
        exit = fadeOut(aniSpecFloat)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.app_name))},
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = viewModel.changeColor?.color
                        ?: MaterialTheme.colorScheme.onPrimary),
                    actions = {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "",
                            modifier = Modifier.size(30.dp).padding(end = 5.dp).clickable {
                                viewModel.showDialogAboutGame = true
                            }
                        )
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                if (viewModel.gameStatus == GameStatus.PLAYING || viewModel.gameStatus == GameStatus.WIN) {

                    val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 2f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
                        label = "scale"
                    )

                    if(viewModel.gameStatus == GameStatus.WIN){
                        Text(
                            text = "You win!",
                            modifier = Modifier.graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                transformOrigin = TransformOrigin.Center
                            },
                            style = LocalTextStyle.current.copy(textMotion = TextMotion.Animated)
                        )
                    }

                    AnimatedContent(targetState = viewModel.score, label = "") { score ->
                        Text(
                            text = "Score: $score",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .background(
                                viewModel.changeColor?.color ?: viewModel.targetColor?.color
                                ?: Color.Gray
                            )
                    ) {}

                    AnimatedVisibility(visible = viewModel.changeColor != null) {
                        Button(onClick = {
                            if (viewModel.changeColor == null) {
                                Toast
                                    .makeText(
                                        context,
                                        "Please, select color",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            } else {
                                viewModel.roll()
                            }
                        }) {
                            Text(
                                text = "Roll Dice",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(25.dp))

                    Text(text = "Select color")

                    LazyRow{
                        items(viewModel.colors) { myColor ->
                            ColorItem(
                                color = myColor.color,
                                nameColor = myColor.name,
                                onItemClick = {
                                   viewModel.onClickColor(myColor)
                                }
                            )
                        }
                    }

                }else {
                    Text(
                        text = when (viewModel.gameStatus) {
                            GameStatus.WIN -> "You Win!"
                            GameStatus.LOSE -> "You Lose!"
                            else -> ""
                        },
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Text(text = "Final score: ${viewModel.score}")

                    Button(onClick = { viewModel.resume() }) {
                        Text(text = "Play again")
                    }
                }

                if(viewModel.showDialogAboutGame){
                    AboutGame{
                        viewModel.showDialogAboutGame = false
                    }
                }
            }
        }
    }
}

@Composable
fun ColorItem(
    color: Color,
    nameColor : String,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onItemClick()
            },

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = nameColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun WelcomeFragment(
    text : String,
    onStart : () -> Unit
){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){

        var text by rememberSaveable { mutableStateOf(text.split("")) }
        var tailText by rememberSaveable { mutableStateOf("") }
        var counterText by rememberSaveable { mutableStateOf(0) }
        var counterColor by rememberSaveable { mutableStateOf(0) }
        val colorList = listOf(
            Color.Black,
            Color.Red,
            Color.Green,
            Color.Blue,
            Color.Cyan,
            Color.Yellow
        )

        LaunchedEffect(Unit){
            while (counterText < text.size - 1){
                counterText++
                tailText += text[counterText - 1]
                if(counterColor != colorList.size - 1) counterColor++ else counterColor = 0

                delay(400)
            }
        }

        Text(
            buildAnnotatedString {
                append(tailText)
                withStyle(style = SpanStyle(colorList[counterColor])){
                    append(text[counterText])
                }
            },
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
        )

        AnimatedVisibility(visible = counterText == text.size - 1) {
            Button(onClick = { onStart() }, modifier = Modifier.padding(top = 30.dp)) {
                Text(text = "Start")
            }
        }
    }
}

@Composable
fun AboutGame(onDismiss: () -> Unit){
    AlertDialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),


                ) {
                Text(text = "How play this game?", textAlign = TextAlign.Center, fontSize = 23.sp)

                Text(
                    text = "1. Select a color from the list\n" +
                            "2. Click on the \"Roll dice\" button\n" +
                            "3. Expect to win!"
                )

                Text(text = "For every victory you multiply your points!")
            }
        },
        confirmButton = {
            Button(onClick = {onDismiss()}) {
                Text(text = "ОК")
            }
        }
    )

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
    var isLoading by rememberSaveable { mutableStateOf(true) }

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


