package ru.fi.sportapp.firebase

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AppFirebase : FirebaseRepository{
    val remoteConfig = Firebase.remoteConfig
    var url = ""

   init{
       val configSettings = remoteConfigSettings {
           minimumFetchIntervalInSeconds = 3600
       }
       remoteConfig.setConfigSettingsAsync(configSettings)

   }

    override suspend fun getUrl(): String {
        return suspendCoroutine{
            try {
                url = remoteConfig.getString("url")
            }catch (e : Exception){
                e.message.toString()
            }
            it.resume(url)
        }
    }
}