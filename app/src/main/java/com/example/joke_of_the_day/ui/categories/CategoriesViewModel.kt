package com.example.joke_of_the_day.ui.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.joke_of_the_day.data.database.JokeDatabase
import com.example.joke_of_the_day.data.repository.JokeRepository
import kotlinx.coroutines.launch
import java.io.IOException

class CategoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JokeRepository
    private val sharedPreferences = application.getSharedPreferences("joke_preferences", Application.MODE_PRIVATE)
    
    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String> = _selectedCategory

    init {
        val jokeDao = JokeDatabase.getDatabase(application).jokeDao()
        repository = JokeRepository(jokeDao, application)
        initializeData()
        
        // 从SharedPreferences加载上次选择的分类
        val savedCategory = sharedPreferences.getString("selected_category", null)
        if (savedCategory != null) {
            _selectedCategory.value = savedCategory
        }
    }
    
    private fun initializeData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // 确保数据库中有预设笑话
                repository.importJokesIfNeeded()
                // 加载笑话分类
                loadCategories()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val jokeCategories = repository.getJokeCategories()
                _categories.value = jokeCategories
                
                // 如果没有选择过分类，默认选择第一个分类
                if (_selectedCategory.value == null && jokeCategories.isNotEmpty()) {
                    selectCategory(jokeCategories[0])
                }
            } catch (e: IOException) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun selectCategory(category: String) {
        _selectedCategory.value = category
        // 保存选择的分类到SharedPreferences
        sharedPreferences.edit().putString("selected_category", category).apply()
    }
} 