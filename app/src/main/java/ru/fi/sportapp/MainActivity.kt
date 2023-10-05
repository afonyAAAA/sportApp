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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import ru.fi.sportapp.ui.theme.SportAppTheme
import ru.fi.sportapp.viewModels.MainViewModel
import ru.fi.sportapp.viewModels.SecondViewModel

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
        val secondViewModel = SecondViewModel()

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
                            ReallyApp(viewModel = secondViewModel)
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
fun ReallyApp(viewModel: SecondViewModel){

    val scrollState = rememberScrollState()
    var maxScroll by rememberSaveable { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {

        Image(
            painter = painterResource(id = R.drawable.background_app),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        LaunchedEffect(Unit){
            while (viewModel.timeShowAnimation != 10){
                delay(750)
                viewModel.addTimeForTimeAnimation()
            }
        }

        LaunchedEffect(scrollState.value){
            if(scrollState.value > maxScroll) maxScroll = scrollState.value
        }

        Text(
            text = stringResource(R.string.golden_age_of_hollywood),
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.CenterHorizontally)
        )

        AnimationOfTextAppearance(state = maxScroll >= 0) {
            Text(text = "In the field of cinema, the United States has been superior to all other countries by an order of magnitude for many decades. This is very strange, because cinema was invented in France, its pioneers were Europeans, and only then it began to develop in America. Is it really all about the huge money that producers have and the gigantic budgets of the films they produce? As practice shows, this is often not enough.")
        }

        AnimationOfTextAppearance(state = maxScroll > 200) {
            Text(text = "In the field of cinema, the United States has been superior to all other countries by an order of magnitude for many decades. This is very strange, because cinema was invented in France, its pioneers were Europeans, and only then it began to develop in America. Is it really all about the huge money that producers have and the gigantic budgets of the films they produce? As practice shows, this is often not enough.")
        }
        
        Button(onClick = {}) {
            Text(text = "Actors")
        }
        
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Quiz")
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


