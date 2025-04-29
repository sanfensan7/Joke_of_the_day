package com.example.joke_of_the_day.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("joke_preferences", Application.MODE_PRIVATE)
    
    private val _notificationsEnabled = MutableLiveData<Boolean>()
    val notificationsEnabled: LiveData<Boolean> = _notificationsEnabled
    
    private val _notificationHour = MutableLiveData<Int>()
    val notificationHour: LiveData<Int> = _notificationHour
    
    private val _notificationMinute = MutableLiveData<Int>()
    val notificationMinute: LiveData<Int> = _notificationMinute
    
    private val _darkModeEnabled = MutableLiveData<Boolean>()
    val darkModeEnabled: LiveData<Boolean> = _darkModeEnabled
    
    private val _fontSize = MutableLiveData<Int>()
    val fontSize: LiveData<Int> = _fontSize
    
    // 字体大小常量
    companion object {
        const val FONT_SIZE_SMALL = 0
        const val FONT_SIZE_MEDIUM = 1
        const val FONT_SIZE_LARGE = 2
    }

    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        _notificationsEnabled.value = sharedPreferences.getBoolean("notifications_enabled", false)
        _notificationHour.value = sharedPreferences.getInt("notification_hour", 9)
        _notificationMinute.value = sharedPreferences.getInt("notification_minute", 0)
        _darkModeEnabled.value = sharedPreferences.getBoolean("dark_mode_enabled", false)
        _fontSize.value = sharedPreferences.getInt("font_size", FONT_SIZE_MEDIUM)
    }
    
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _notificationsEnabled.value = enabled
            sharedPreferences.edit().putBoolean("notifications_enabled", enabled).apply()
            
            // 如果启用了通知，设置通知任务
            if (enabled) {
                scheduleNotification()
            } else {
                cancelNotification()
            }
        }
    }
    
    fun setNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            _notificationHour.value = hour
            _notificationMinute.value = minute
            
            sharedPreferences.edit()
                .putInt("notification_hour", hour)
                .putInt("notification_minute", minute)
                .apply()
            
            // 如果通知已启用，更新通知时间
            if (_notificationsEnabled.value == true) {
                scheduleNotification()
            }
        }
    }
    
    fun setDarkModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _darkModeEnabled.value = enabled
            sharedPreferences.edit().putBoolean("dark_mode_enabled", enabled).apply()
            
            // 应用深色模式
            applyDarkMode(enabled)
        }
    }
    
    fun setFontSize(size: Int) {
        viewModelScope.launch {
            _fontSize.value = size
            sharedPreferences.edit().putInt("font_size", size).apply()
            
            // 应用字体大小
            applyFontSize(size)
        }
    }
    
    // 实际实现这些函数需要更多的工作，这里只是框架
    private fun scheduleNotification() {
        // 使用WorkManager设置每日通知
    }
    
    private fun cancelNotification() {
        // 取消通知任务
    }
    
    private fun applyDarkMode(enabled: Boolean) {
        // 设置深色模式
    }
    
    private fun applyFontSize(size: Int) {
        // 设置字体大小
    }
} 