package ru.fi.sportapp.model

data class Question(
    val textQuest : String,
    val correctAnswer : String,
    val variantsAnswers : List<String>
)
