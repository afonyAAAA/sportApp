package ru.fi.sportapp.models

import android.graphics.drawable.Drawable


data class Casino(
    val nameCasino: String,
    val shortDescription : String,
    val locationName: String,
    val yearOfCreation: String,
    val articles: List<Article>,
    val interestingFacts: List<String>,
    val urlImageCasino : Drawable?
)

data class Article(
    val nameArticle: String,
    val textArticle: String,
    val urlImage : List<Drawable?>
)