package ru.fi.sportapp.viewModels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.BuildConfig
import ru.fi.sportapp.R
import java.util.Locale

class MainViewModel(private val context: Context) : ViewModel() {

    var stateAlertDialog by mutableStateOf(false)
    var url by mutableStateOf("")
    var isLoading by mutableStateOf(true)
    val localUrl = checkLocalUrl()
    val phone = checkIsEmu()
    val listImage : MutableList<Bitmap> = mutableListOf()
    val image = BitmapFactory.decodeResource(context.resources, R.drawable.cat)
    init {
        listImage.addAll(splitImage(image, 3, 3))
    }
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

    fun splitImage(image: Bitmap, rows: Int, columns: Int) : List<Bitmap>{
        val imageWidth = image.width
        val imageHeight = image.height
        val tileWidth = imageWidth / columns
        val tileHeight = imageHeight / rows

        val tiles = mutableListOf<Bitmap>()

        for (row in 0 until rows) {
            for (col in 0 until columns) {
                val x = col * tileWidth
                val y = row * tileHeight
                val tile = Bitmap.createBitmap(image, x, y, tileWidth, tileHeight)
                tiles.add(tile)
            }
        }

        return tiles
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
