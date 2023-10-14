package com.lucky.glo.viewModels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.lucky.glo.R
import org.xmlpull.v1.XmlPullParser
import com.lucky.glo.models.Article
import com.lucky.glo.models.Casino


class MainViewModel(val context : Context) : ViewModel() {

    var isFirstLaunch by mutableStateOf(false)
    var casinos = mutableStateListOf<Casino>()
    var mainArticle = mutableStateListOf<Article>()

    init {
        checkFirstLaunch()
        getCasinos()
        getMainArticle()
    }

    fun checkFirstLaunch(){
        isFirstLaunch = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
            .getBoolean("first_launch", true)
    }

    private fun getMainArticle(){
        var nameArticle: String = ""
        var textArticle: String = ""
        val urlImage: MutableList<String> = mutableListOf()

        val parser = context.resources.getXml(R.xml.main_article)
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when(parser.name){
                        "name-article" -> nameArticle = parser.nextText() ?: ""
                        "text-article" -> textArticle = parser.nextText() ?: ""
                        "url-image" -> {
                            while (parser.next() != XmlPullParser.END_TAG) {
                                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "url") {
                                    urlImage.add(parser.nextText() ?: "")
                                }
                            }
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (
                        parser.name == "article"
                    ) {
                        val article = Article(
                            nameArticle = nameArticle,
                            textArticle = textArticle,
                            urlImage = urlImage.toList()
                        )
                        mainArticle.add(article)
                        urlImage.clear()
                    }
                }
            }
            eventType = parser.next()
        }
    }

    private fun getCasinos() {

        var nameCasino: String = ""
        var locationName: String = ""
        var yearOfCreation: String = ""
        var shortDescription : String = ""
        val articles: MutableList<Article> = mutableListOf()
        val interestingFacts: MutableList<String> = mutableListOf()
        var urlImageCasino: String = ""

        var nameArticle: String = ""
        var textArticle: String = ""
        val urlImage: MutableList<String> = mutableListOf()

        val parser = context.resources.getXml(R.xml.casinos)
        var eventType = parser.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "name-casino" -> nameCasino = parser.nextText() ?: ""
                        "location-name" -> locationName = parser.nextText() ?: ""
                        "year-of-creation" -> yearOfCreation = parser.nextText() ?: ""
                        "articles" -> {
                            do {
                                parser.next()
                                if(parser.eventType == XmlPullParser.START_TAG && parser.name != "articles"){
                                    when (parser.name) {
                                        "name-article" -> nameArticle = parser.nextText() ?: ""
                                        "text-article" -> textArticle = parser.nextText() ?: ""
                                        "url-image" -> {
                                            while (parser.next() != XmlPullParser.END_TAG) {
                                                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "url") {
                                                    val url = parser.nextText() ?: ""
                                                    if(url.isNotEmpty()){
                                                        urlImage.add(url)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if(parser.eventType == XmlPullParser.END_TAG && parser.name == "article"){
                                    if(nameArticle.isNotEmpty() && textArticle.isNotEmpty()){
                                        val article = Article(nameArticle, textArticle, urlImage.toList())
                                        articles.add(article)
                                        urlImage.clear()
                                    }
                                }
                            }
                            while (parser.name != "articles")

                        }
                        "short-description" -> shortDescription = parser.nextText() ?: ""
                        "interesting-facts" -> {
                            while (parser.next() != XmlPullParser.END_TAG) {
                                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "fact") {
                                    interestingFacts.add(parser.nextText() ?: "")
                                }
                            }
                        }
                        "url-to-image-casino" -> urlImageCasino = parser.nextText() ?: ""

                    }
                }
                XmlPullParser.END_TAG -> {
                    if (
                        parser.name == "casino"
                    ) {
                        val casino = Casino(
                            nameCasino = nameCasino,
                            interestingFacts = interestingFacts.toList(),
                            locationName = locationName,
                            yearOfCreation = yearOfCreation,
                            articles = articles.toList(),
                            urlImageCasino = urlImageCasino,
                            shortDescription = shortDescription
                        )
                        casinos.add(casino)
                        interestingFacts.clear()
                        articles.clear()
                    }
                }
            }
            eventType = parser.next()
        }
    }
}

