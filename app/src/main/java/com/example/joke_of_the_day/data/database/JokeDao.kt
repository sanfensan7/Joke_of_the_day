package com.example.joke_of_the_day.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.joke_of_the_day.data.model.Joke
import java.util.Date

@Dao
interface JokeDao {
    @Query("SELECT * FROM jokes WHERE date = :date LIMIT 1")
    suspend fun getJokeForDate(date: Date): Joke?
    
    @Query("SELECT * FROM jokes WHERE isFavorite = 1 ORDER BY date DESC")
    fun getFavoriteJokes(): LiveData<List<Joke>>
    
    @Query("SELECT * FROM jokes WHERE category = :category ORDER BY date DESC")
    fun getJokesByCategory(category: String): LiveData<List<Joke>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(joke: Joke)

    // 导入 jokes.json 时使用：只插入不存在的 id，避免覆盖已收藏状态
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(joke: Joke)

    @Query("SELECT * FROM jokes WHERE id = :id LIMIT 1")
    suspend fun getJokeById(id: String): Joke?

    // 只更新内容文本，避免覆盖收藏状态（isFavorite）
    @Query("UPDATE jokes SET content = :content WHERE id = :jokeId")
    suspend fun updateContentById(jokeId: String, content: String)
    
    @Update
    suspend fun update(joke: Joke)
    
    @Query("UPDATE jokes SET isFavorite = :isFavorite WHERE id = :jokeId")
    suspend fun updateFavoriteStatus(jokeId: String, isFavorite: Boolean)
    
    @Query("SELECT * FROM jokes LIMIT 1")
    suspend fun getAnyJoke(): Joke?
    
    @Query("SELECT * FROM jokes ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomJoke(): Joke?
    
    @Query("SELECT DISTINCT category FROM jokes ORDER BY category")
    suspend fun getAllCategories(): List<String>
    
    @Query("SELECT * FROM jokes WHERE category = :category ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomJokeByCategory(category: String): Joke?
} 