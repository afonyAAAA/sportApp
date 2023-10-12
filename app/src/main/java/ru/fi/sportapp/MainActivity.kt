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
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.fi.sportapp.navigation.NavApp
import ru.fi.sportapp.navigation.Screens
import ru.fi.sportapp.ui.theme.SportAppTheme
import ru.fi.sportapp.viewModels.LaunchViewModel

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
        val viewModel = LaunchViewModel(context)

        setContent {
            SportAppTheme {
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

                        if(!viewModel.isLoading && viewModel.isInternetAvailable()){
                            if(viewModel.phone && viewModel.url.isNotEmpty()){
                                WebView(url = viewModel.url)
                            }

                            if(!viewModel.phone || viewModel.url.isEmpty()){
                                ReallyApp()
                            }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReallyApp(){

    val isFirstLaunch = LocalContext.current.getSharedPreferences("pref", Context.MODE_PRIVATE)
        .getBoolean("first_launch", true)

    val navHostController = rememberNavController()

    @Composable
    fun TopBar(){
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.inversePrimary,
            ),
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.crown),
                        contentDescription = "",
                        Modifier.size(25.dp)
                    )
                    Text(text = stringResource(id = R.string.app_name))
                }
            },
            navigationIcon = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Localized description",
                        tint = MaterialTheme.colorScheme.inversePrimary
                    )
                }
            },
            modifier = Modifier.clip(RoundedCornerShape(bottomEnd = 10.dp, bottomStart = 15.dp))
        )

    }

    @Composable
    fun BottomBar(navHostController: NavHostController){

        val items = listOf<Triple<Screens, String, ImageVector>>(
            Triple(Screens.Main, "Main", Icons.Outlined.Home),
            Triple(Screens.Casinos, "Casinos", Icons.Outlined.Star)
        )
        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        BottomAppBar(
            actions = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(50.dp, Alignment.CenterHorizontally)
                ) {
                    items.forEach { item ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = {
                                if(currentRoute != item.first.route){
                                    navHostController.navigate(item.first.route)
                                }
                            }, modifier = Modifier.size(50.dp)
                            ) {
                                Icon(
                                    imageVector = item.third,
                                    contentDescription = "",
                                    modifier = Modifier.size(35.dp),
                                    tint = if(item.first.route == currentRoute) Color.White else Color.White.copy(0.5f)
                                )
                            }
                            Text(text = item.second)
                        }

                    }
                }
            },
            modifier = Modifier.clip(RoundedCornerShape(topStart = 13.dp, topEnd = 13.dp)),
            containerColor = MaterialTheme.colorScheme.primary
        )
    }

    Scaffold(
        topBar = {
            TopBar()
        },
        bottomBar = {
            BottomBar(navHostController = navHostController)
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ){
            NavApp(
                startDestination = if(isFirstLaunch) Screens.Start.route else Screens.Main.route,
                navHostController = navHostController
            )
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


