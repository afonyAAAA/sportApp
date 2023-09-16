package ru.fi.sportapp

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.fi.sportapp.news.NewsSingl

class viewModel : ViewModel() {

    var listNews = mutableStateListOf<News>()
    private val repository = NewsSingl.provideNewsApi()

    suspend fun getNews(){
        viewModelScope.launch {
            val result = repository.getTopHeadlines()

            val resultListNews = mutableListOf<News>()
            if(result.totalResults != null){
                result.articles.forEach{articles ->
                    val news = News(
                        title = articles.title!!,
                        description = articles.description!!,
                        url = articles.url!!
                    )
                    resultListNews.add(news)
                }
                listNews = resultListNews
            }else{

            }

        }
    }
}