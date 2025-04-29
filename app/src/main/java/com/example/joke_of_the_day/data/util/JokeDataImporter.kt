package com.example.joke_of_the_day.data.util

import android.content.Context
import com.example.joke_of_the_day.R
import com.example.joke_of_the_day.data.database.JokeDao
import com.example.joke_of_the_day.data.model.Joke
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Date

class JokeDataImporter(private val context: Context) {

    private val gson = Gson()
    
    suspend fun importJokesToDatabase(jokeDao: JokeDao) {
        withContext(Dispatchers.IO) {
            // 首先检查数据库是否已经有数据
            val existingJoke = jokeDao.getAnyJoke()
            
            // 如果已经有数据，则不导入
            if (existingJoke != null) {
                return@withContext
            }
            
            // 读取预定义的笑话
            val jokes = loadJokesFromJson()
            
            // 导入到数据库
            for (jokeData in jokes) {
                val joke = Joke(
                    id = jokeData.id,
                    content = jokeData.content,
                    category = jokeData.category,
                    date = getRandomDateForLastYear(),
                    isFavorite = false
                )
                jokeDao.insert(joke)
            }
        }
    }

    private fun loadJokesFromJson(): List<JokeData> {
        val inputStream = context.resources.openRawResource(R.raw.jokes)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val jsonString = reader.readText()
        reader.close()
        
        val listType = object : TypeToken<List<JokeData>>() {}.type
        return gson.fromJson(jsonString, listType)
    }
    
    // 为笑话生成过去一年内的随机日期
    private fun getRandomDateForLastYear(): Date {
        val now = Date().time
        val oneYearAgo = now - 365 * 24 * 60 * 60 * 1000L
        val randomTime = oneYearAgo + (Math.random() * (now - oneYearAgo)).toLong()
        return Date(randomTime)
    }
    
    data class JokeData(
        val id: String,
        val content: String,
        val category: String
    )
} 