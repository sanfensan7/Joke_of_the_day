package com.example.joke_of_the_day.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.joke_of_the_day.MainActivity
import com.example.joke_of_the_day.R
import com.example.joke_of_the_day.data.repository.JokeRepository
import com.example.joke_of_the_day.data.database.JokeDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DailyJokeWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "joke_notification_channel"
        const val NOTIFICATION_ID = 1
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val jokeDao = JokeDatabase.getDatabase(context).jokeDao()
            val repository = JokeRepository(jokeDao, context)
            
            // 获取一条随机笑话
            val joke = repository.getRandomJoke()
            
            // 创建通知
            if (joke != null) {
                createNotificationChannel()
                showNotification(joke.content, joke.category)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "每日笑话通知"
            val descriptionText = "每日笑话推送"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(jokeContent: String, category: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notificationTitle = "今日笑话【$category】"
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 使用应用图标
            .setContentTitle(notificationTitle)
            .setContentText(jokeContent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(jokeContent))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
} 