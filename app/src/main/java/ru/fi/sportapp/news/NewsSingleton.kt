package ru.fi.sportapp.news

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object NewsSingleton {
    private fun buildRetrofit(gson : Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://newsapi.org/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun provideNewsApi(): NewsApi{
        val gson = GsonBuilder().create()
        return buildRetrofit(gson).create()
    }
}