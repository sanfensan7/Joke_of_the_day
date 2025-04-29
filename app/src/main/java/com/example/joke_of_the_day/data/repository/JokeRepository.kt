package com.example.joke_of_the_day.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.joke_of_the_day.data.database.JokeDao
import com.example.joke_of_the_day.data.model.Joke
import com.example.joke_of_the_day.data.util.JokeDataImporter
import com.example.joke_of_the_day.util.formatDate
import java.util.Date

class JokeRepository(private val jokeDao: JokeDao, private val context: Context) {
    
    private val jokeDataImporter = JokeDataImporter(context)
    
    // 导入预设笑话数据
    suspend fun importJokesIfNeeded() {
        jokeDataImporter.importJokesToDatabase(jokeDao)
    }
    
    suspend fun getTodayJoke(): Joke {
        val today = Date()
        val formattedToday = formatDate(today)
        
        // 尝试从数据库获取今日笑话
        val cachedJoke = jokeDao.getJokeForDate(formattedToday)
        
        // 如果数据库中已有今日笑话，直接返回
        if (cachedJoke != null) {
            return cachedJoke
        }
        
        // 否则获取随机笑话并将其日期设为今天
        val randomJoke = jokeDao.getRandomJoke() ?: throw Exception("数据库中没有笑话数据")
        val todayJoke = randomJoke.copy(id = "${randomJoke.id}_${today.time}", date = formattedToday)
        jokeDao.insert(todayJoke)
        return todayJoke
    }
    
    suspend fun getRandomJoke(): Joke {
        return jokeDao.getRandomJoke() ?: throw Exception("数据库中没有笑话数据")
    }
    
    suspend fun getJokeByCategory(category: String): Joke {
        return jokeDao.getRandomJokeByCategory(category) ?: throw Exception("找不到该分类的笑话")
    }
    
    fun getFavoriteJokes(): LiveData<List<Joke>> {
        return jokeDao.getFavoriteJokes()
    }
    
    fun getJokesByCategory(category: String): LiveData<List<Joke>> {
        return jokeDao.getJokesByCategory(category)
    }
    
    suspend fun toggleFavorite(joke: Joke) {
        jokeDao.updateFavoriteStatus(joke.id, !joke.isFavorite)
    }
    
    suspend fun getJokeCategories(): List<String> {
        return jokeDao.getAllCategories()
    }
} 