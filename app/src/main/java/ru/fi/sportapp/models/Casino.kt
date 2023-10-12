package ru.fi.sportapp.models

import kotlinx.serialization.Serializable

@Serializable
data class Casino(
    val nameCasino: String,
    val locationName: String,
    val yearOfCreation: Int,
    val articles: List<Article>,
    val interestingFacts: List<String>,
    val urlToImageCasino: String
)

@Serializable
data class Article(
    val nameArticle: String,
    val textArticle: String,
    val urlImage: UrlImage
)

@Serializable
data class UrlImage(
    val url: String
)