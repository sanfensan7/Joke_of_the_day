package com.example.joke_of_the_day.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 将日期格式化为标准格式，用于比较两个日期是否为同一天
 */
fun formatDate(date: Date): Date {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dateString = formatter.format(date)
    return formatter.parse(dateString) ?: date
}

/**
 * 格式化日期为显示格式
 */
fun formatDateForDisplay(date: Date): String {
    val formatter = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
    return formatter.format(date)
} 