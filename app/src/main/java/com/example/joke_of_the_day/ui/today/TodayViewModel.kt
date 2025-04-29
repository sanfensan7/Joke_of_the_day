package com.example.joke_of_the_day.ui.today

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.joke_of_the_day.data.database.JokeDatabase
import com.example.joke_of_the_day.data.model.Joke
import com.example.joke_of_the_day.data.repository.JokeRepository
import kotlinx.coroutines.launch
import java.io.IOException

class TodayViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JokeRepository
    private val sharedPreferences = application.getSharedPreferences("joke_preferences", Application.MODE_PRIVATE)
    
    private val _todayJoke = MutableLiveData<Joke>()
    val todayJoke: LiveData<Joke> = _todayJoke
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        val jokeDao = JokeDatabase.getDatabase(application).jokeDao()
        repository = JokeRepository(jokeDao, application)
        initializeData()
    }
    
    private fun initializeData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // 确保数据库中有预设笑话
                repository.importJokesIfNeeded()
                // 加载今日笑话
                loadTodayJoke()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun loadTodayJoke() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val joke = repository.getTodayJoke()
                _todayJoke.value = joke
            } catch (e: IOException) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refreshJoke() {
        loadTodayJoke()
    }
    
    // 获取下一个笑话（根据选择的分类）
    fun getNextJoke() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                // 获取保存的分类
                val selectedCategory = sharedPreferences.getString("selected_category", null)
                
                val joke = if (selectedCategory != null) {
                    // 根据分类获取笑话
                    repository.getJokeByCategory(selectedCategory)
                } else {
                    // 如果没有选择分类，获取随机笑话
                    repository.getRandomJoke()
                }
                
                _todayJoke.value = joke
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun toggleFavorite() {
        _todayJoke.value?.let { joke ->
            viewModelScope.launch {
                try {
                    repository.toggleFavorite(joke)
                    // 更新本地的joke对象以立即反映收藏状态变化
                    _todayJoke.value = joke.copy(isFavorite = !joke.isFavorite)
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
        }
    }
} 