package com.lucky.glo

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AppFirebase(){

    private val remoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    suspend fun getUrl(): Pair<String, Boolean> {
        return suspendCoroutine {contination ->
            try {
                remoteConfig.fetchAndActivate()
                    .addOnCompleteListener {
                        if(it.isComplete){
                            contination.resume(Pair(remoteConfig.getString("url"), false))
                        }
                    }
            }catch (e : Exception){
                Pair(e.message.toString(), true)
            }
        }
    }
}