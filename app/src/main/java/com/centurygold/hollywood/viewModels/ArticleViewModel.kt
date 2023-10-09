package com.centurygold.hollywood.viewModels

import android.content.Context
import android.content.res.Resources
import android.content.res.XmlResourceParser
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.centurygold.hollywood.R
import com.centurygold.hollywood.model.Actor
import com.centurygold.hollywood.model.Question
import com.centurygold.hollywood.model.SubTopic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser

class ArticleViewModel(private val context : Context) : ViewModel() {

    val subTopics = mutableStateListOf<SubTopic>()
    val actors = mutableStateListOf<Actor>()
    val questions = mutableStateListOf<Question>()
    val variantsAnswers = mutableStateListOf<String>()
    var nextQuestion = MutableTransitionState(initialState = false).apply {
        targetState = true
    }
    var targetQuestion by mutableStateOf(0)
    var countCorrectAnswer by mutableStateOf(0)
    var isShowActors by mutableStateOf(false)
    var isStartQuiz by mutableStateOf(false)

    init {
        getArticle(context.resources)
        getActors(context.resources)
    }

    private fun refreshVariantAnswers(){
        if(variantsAnswers.isNotEmpty()) variantsAnswers.clear()

        variantsAnswers.addAll((questions[targetQuestion].variantsAnswers + questions[targetQuestion].correctAnswer).shuffled())

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

    fun checkAnswer(answer : String){
        viewModelScope.launch {
            nextQuestion.targetState = false
            delay(500)

            if(answer == questions[targetQuestion].correctAnswer){
                countCorrectAnswer++
            }
            targetQuestion++
            if(targetQuestion < questions.size - 1){
                nextQuestion.targetState = true
                refreshVariantAnswers()
            }else{
                variantsAnswers.clear()
            }
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
        var currentTextQuestion: String = ""
        var currentCorrectAnswer: String = ""
        val currentOtherAnswers: MutableList<String> = mutableListOf()

        val parser : XmlResourceParser = resources.getXml(R.xml.quiz)
        var eventType = parser.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when(parser.name) {
                        "textQuestion" -> currentTextQuestion = parser.nextText() ?: ""
                        "correctAnswer" -> currentCorrectAnswer = parser.nextText() ?: ""
                        "otherVariantsAnswer" -> {
                            while (parser.next() != XmlPullParser.END_TAG){
                                if (parser.eventType == XmlPullParser.START_TAG && parser.name == "answer") {
                                    currentOtherAnswers.add(parser.nextText() ?: "")
                                }
                            }
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if(
                        parser.name == "question" && currentOtherAnswers.isNotEmpty()
                        ) {
                        val question = Question(
                            textQuest = currentTextQuestion,
                            correctAnswer = currentCorrectAnswer,
                            variantsAnswers = currentOtherAnswers.toList()
                        )
                        questions.add(question)
                        currentOtherAnswers.clear()
                    }
                }
            }
            eventType = parser.next()
        }
    }

    fun showActors(){
        isShowActors = true
    }

    fun hideActors() {
        isShowActors = false
    }

    fun startQuiz(){
        getQuestions(context.resources)
        isStartQuiz = true
        refreshVariantAnswers()
    }

    fun completeQuiz(){
        countCorrectAnswer = 0
        targetQuestion = 0
        questions.clear()
        isStartQuiz = false
        nextQuestion.targetState = true
    }
}