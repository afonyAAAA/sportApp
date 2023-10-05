package ru.fi.sportapp.viewModels

import android.content.Context
import android.content.res.Resources
import android.content.res.XmlResourceParser
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.xmlpull.v1.XmlPullParser
import ru.fi.sportapp.R
import ru.fi.sportapp.model.Actor
import ru.fi.sportapp.model.Question
import ru.fi.sportapp.model.SubTopic

class ArticleViewModel(context : Context) : ViewModel() {

    val subTopics = mutableStateListOf<SubTopic>()
    val actors = mutableStateListOf<Actor>()
    val questions = mutableStateListOf<Question>()
    var isShowActors by mutableStateOf(false)
    var isStartQuiz by mutableStateOf(false)

    init {
        getArticle(context.resources)
        getActors(context.resources)
    }

    private fun getArticle(resources: Resources){
        var currentSubTopic: String = ""
        var currentSubTopicText: String = ""

        val parser : XmlResourceParser = resources.getXml(R.xml.article)
        var eventType = parser.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when(parser.name) {
                        "subTopicName" -> currentSubTopic = parser.nextText() ?: ""
                        "subTopicText" -> currentSubTopicText = parser.nextText() ?: ""
                    }
                }
                XmlPullParser.END_TAG -> {
                    if(parser.name == "subTopic") {
                        val subTopic = SubTopic(
                            name = currentSubTopic,
                            text = currentSubTopicText
                        )
                        subTopics.add(subTopic)
                    }

                }
            }
            eventType = parser.next()
        }
    }

    private fun getActors(resources: Resources) {
        var currentActorName: String = ""
        var currentDescriptionActor: String = ""
        var currentUrlToImage: String = ""

        val parser : XmlResourceParser = resources.getXml(R.xml.actors)
        var eventType = parser.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when(parser.name) {
                        "name" -> currentActorName = parser.nextText() ?: ""
                        "description" -> currentDescriptionActor = parser.nextText() ?: ""
                        "urlToImage" -> currentUrlToImage = parser.nextText() ?: ""
                    }
                }
                XmlPullParser.END_TAG -> {
                    if(parser.name == "actor") {
                        val actor = Actor(
                            name = currentActorName,
                            description = currentDescriptionActor,
                            urlToImage = currentUrlToImage
                        )
                        actors.add(actor)
                    }
                }
            }
            eventType = parser.next()
        }
    }

    private fun getQuestions(resources: Resources) {
        var currentActorName: String = ""
        var currentDescriptionActor: String = ""
        var currentUrlToImage: String = ""

        val parser : XmlResourceParser = resources.getXml(R.xml.quiz)
        var eventType = parser.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when(parser.name) {
                        "name" -> currentActorName = parser.nextText() ?: ""
                        "description" -> currentDescriptionActor = parser.nextText() ?: ""
                        "urlToImage" -> currentUrlToImage = parser.nextText() ?: ""
                    }
                }
                XmlPullParser.END_TAG -> {
                    if(parser.name == "actor") {
                        val actor = Actor(
                            name = currentActorName,
                            description = currentDescriptionActor,
                            urlToImage = currentUrlToImage
                        )
                        actors.add(actor)
                    }
                }
            }
            eventType = parser.next()
        }
    }



}