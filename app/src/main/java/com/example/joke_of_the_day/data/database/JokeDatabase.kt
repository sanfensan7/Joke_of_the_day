package com.example.joke_of_the_day.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.joke_of_the_day.data.model.Joke
import com.example.joke_of_the_day.util.DateConverter

@Database(entities = [Joke::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class JokeDatabase : RoomDatabase() {
    
    abstract fun jokeDao(): JokeDao
    
    companion object {
        @Volatile
        private var INSTANCE: JokeDatabase? = null
        
        fun getDatabase(context: Context): JokeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JokeDatabase::class.java,
                    "joke_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 