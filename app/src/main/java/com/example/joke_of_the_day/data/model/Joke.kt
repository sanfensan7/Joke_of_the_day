package com.example.joke_of_the_day.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "jokes")
data class Joke(
    @PrimaryKey
    val id: String,
    val content: String,
    val category: String,
    val date: Date = Date(),
    val isFavorite: Boolean = false
) 