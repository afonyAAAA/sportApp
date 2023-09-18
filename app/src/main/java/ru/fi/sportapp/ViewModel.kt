package ru.fi.sportapp

import android.content.Context
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.net.ConnectivityManager
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.BuildConfig
import org.xmlpull.v1.XmlPullParser
import java.util.Locale

class ViewModel(private val context: Context) : ViewModel() {

    var stateAlertDialog by mutableStateOf(false)
    var openedDescriptionNews by mutableStateOf(false)
    var url by mutableStateOf("")
    val localUrl = checkLocalUrl()
    val listNews = mutableStateListOf<News>()
    val phone = checkIsEmu()
    var chooseNews = News("", "", "", emptyList())

    private fun checkIsEmu(): Boolean {
        if (BuildConfig.DEBUG) return false
        val phoneModel = Build.MODEL
        val buildProduct = Build.PRODUCT
        val buildHardware = Build.HARDWARE
        val brand = Build.BRAND;
        val buildFingerprint = Build.FINGERPRINT

        val isEmulator = (buildFingerprint.startsWith("generic")
                || buildFingerprint.startsWith("unknown")
                || phoneModel.contains("google_sdk")
                || phoneModel.toLowerCase(Locale.ROOT) == "sdk"
                || phoneModel.contains("Emulator")
                || phoneModel.contains("Android SDK built for x86")
                || buildHardware == "goldfish"
                || brand.contains("google")
                || buildHardware == "vbox86"
                || buildProduct == "sdk"
                || buildProduct == "google_sdk"
                || buildProduct == "sdk_x86"
                || buildProduct == "vbox86p"
                || Build.MANUFACTURER.contains("Genymotion"))
                || Build.BOARD.lowercase(Locale.getDefault()).contains("nox")
                || Build.BOOTLOADER.lowercase(Locale.getDefault()).contains("nox")
                || buildHardware.lowercase(Locale.getDefault()).contains("nox")
                || buildProduct.lowercase(Locale.getDefault()).contains("nox")

        return !isEmulator
    }


    fun getNews(resources : Resources) {
        var currentTitle: String = ""
        var currentDescription: String = ""
        var currentUrlImages: MutableList<String> = mutableListOf()
        var currentUrlNews: String = ""

        val parser: XmlResourceParser = resources.getXml(R.xml.news)
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when(parser.name) {
                        "title" -> currentTitle = parser.nextText() ?: ""
                        "description" -> currentDescription = parser.nextText() ?: ""
                        "urlImages" -> {
                            while (parser.next() != XmlPullParser.END_TAG) {
                                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "urlImage") {
                                    currentUrlImages.add(parser.nextText() ?: "")
                                }
                            }
                        }
                        "urlNews" -> currentUrlNews = parser.nextText() ?: ""
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "item" &&
                        currentTitle.isNotEmpty() &&
                        currentDescription.isNotEmpty() &&
                        currentUrlNews.isNotEmpty() &&
                        currentUrlImages.isNotEmpty()
                    ) {
                        val news = News(
                            title = currentTitle.trim(),
                            description = currentDescription.trim(),
                            url = currentUrlNews.trim(),
                            urlImages = currentUrlImages.map { it.trim() }.toList()
                        )
                        listNews.add(news)
                        currentTitle = ""
                        currentDescription = ""
                        currentUrlNews = ""
                        currentUrlImages.clear()
                    }
                }
            }
            eventType = parser.next()
        }
    }
    fun saveUrl(){
        val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("url", url).apply()
    }

    fun checkLocalUrl() : String{
        val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return sharedPreferences.getString("url", "")!!
    }

    fun isInternetAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting == true
    }

}
