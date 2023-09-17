package ru.fi.sportapp

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.BuildConfig
import kotlinx.coroutines.launch
import ru.fi.sportapp.news.News
import ru.fi.sportapp.news.NewsSingleton
import java.util.Locale

class ViewModel(private val context: Context) : ViewModel() {


    private val repository = NewsSingleton.provideNewsApi()
    var stateAlertDialog by mutableStateOf(false)
    var url by mutableStateOf("")
    val localUrl = checkLocalUrl()
    val listNews = mutableStateListOf<News>()
    val phone = checkIsEmu()

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


    fun getNews() {
        viewModelScope.launch {
            val result = repository.getTopHeadlines()

            if (result.totalResults != null) {
                result.articles.forEach { articles ->

                    val title = articles.title ?: ""
                    val description = articles.description ?: "No description."
                    val imageUrl = articles.urlToImage ?: ""
                    val positiveNews = title.isNotEmpty() &&
                            imageUrl.isNotEmpty()

                    if(positiveNews){
                        val news = News(
                            title = title,
                            description = description,
                            url = articles.url ?: "",
                            urlImage = imageUrl
                        )
                        listNews.add(news)
                    }
                }
            } else {
                stateAlertDialog = true
            }
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
