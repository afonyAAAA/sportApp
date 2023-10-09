package com.centurygold.hollywood.model

data class Question(
    val textQuest : String,
    val correctAnswer : String,
    val variantsAnswers : List<String>
)
