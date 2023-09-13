package ru.fi.sportapp

import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.firebase.BuildConfig
import ru.fi.sportapp.firebase.AppFirebase
import ru.fi.sportapp.ui.theme.SportAppTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SportAppTheme {
                if(checkIsEmu()){
                    val appFirebase = AppFirebase()
                    WebView(url = appFirebase.url)
                }else{

                }
            }
        }
    }
}


private fun checkIsEmu(): Boolean {
    if (BuildConfig.DEBUG) return false
    val phoneModel = Build.MODEL
    val buildProduct = Build.PRODUCT
    val buildHardware = Build.HARDWARE
    val brand = Build.BRAND;
    var result = (Build.FINGERPRINT.startsWith("generic")
            || phoneModel.contains("google_sdk")
            || phoneModel.lowercase(Locale.getDefault()).contains("droid4x")
            || phoneModel.contains("Emulator")
            || phoneModel.contains("Android SDK built for x86")
            || Build.MANUFACTURER.contains("Genymotion")
            || buildHardware == "goldfish"
            || Build.BRAND.contains("google")
            || buildHardware == "vbox86"
            || buildProduct == "sdk"
            || buildProduct == "google_sdk"
            || buildProduct == "sdk_x86"
            || buildProduct == "vbox86p"
            || Build.BOARD.lowercase(Locale.getDefault()).contains("nox")
            || Build.BOOTLOADER.lowercase(Locale.getDefault()).contains("nox")
            || buildHardware.lowercase(Locale.getDefault()).contains("nox")
            || buildProduct.lowercase(Locale.getDefault()).contains("nox"))
    if (result) return true
    result = result or (Build.BRAND.startsWith("generic") &&
            Build.DEVICE.startsWith("generic"))
    if (result) return true
    result = result or ("google_sdk" == buildProduct)
    return result
}

@Composable
fun WebView(url : String){

    var webView by remember { mutableStateOf<WebView?>(null) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                // Настройка WebView
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        },
        update = { view ->
            webView = view
        }
    )

    DisposableEffect(Unit) {
        // Очистка WebView при уничтожении Composable-компонента
        onDispose {
            webView?.destroy()
        }
    }
}

@Composable
fun ReallyApp(){

}

