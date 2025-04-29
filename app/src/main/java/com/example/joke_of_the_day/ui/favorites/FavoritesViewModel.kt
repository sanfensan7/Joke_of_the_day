package com.example.joke_of_the_day.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.joke_of_the_day.data.database.JokeDatabase
import com.example.joke_of_the_day.data.model.Joke
import com.example.joke_of_the_day.data.repository.JokeRepository
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JokeRepository
    val favoriteJokes: LiveData<List<Joke>>

    init {
        val jokeDao = JokeDatabase.getDatabase(application).jokeDao()
        repository = JokeRepository(jokeDao, application)
        favoriteJokes = repository.getFavoriteJokes()
        initializeData()
    }
    
    private fun initializeData() {
        viewModelScope.launch {
            try {
                // 确保数据库中有预设笑话
                repository.importJokesIfNeeded()
            } catch (e: Exception) {
                // 处理错误（如有必要）
            }
        }
    }
    
    fun toggleFavorite(joke: Joke) {
        viewModelScope.launch {
            repository.toggleFavorite(joke)
        }
    }
    
    fun shareJoke(joke: Joke): String {
        return "【${joke.category}】\n${joke.content}\n\n——来自 每日笑话 APP"
    }
} 