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
            // 读取预定义的笑话（res/raw/jokes.json）
            val jokes = loadJokesFromJson()
            if (jokes.isEmpty()) return@withContext

            // 只要最后一条数据已存在，就认为导入完成（避免每次启动都重跑大量 insert）。
            // 但：为了避免“冷笑话”部分出现同一模板反复（你要求不要重复），
            // 我们仍会对 id >= 152 的冷笑话在数据库中更新内容为“按编号变化”的版本。
            val lastId = jokes.last().id
            val lastInDb = jokeDao.getJokeById(lastId)

            if (lastInDb == null) {
                // 首次导入：使用 IGNORE 只补齐不存在的 id，避免覆盖用户收藏（favorites）。
                for (jokeData in jokes) {
                    val newContent = maybeRegenerateColdJokeContent(jokeData) ?: jokeData.content
                    val joke = Joke(
                        id = jokeData.id,
                        content = newContent,
                        category = jokeData.category,
                        date = getRandomDateForLastYear(),
                        isFavorite = false
                    )
                    jokeDao.insertIgnore(joke)
                }
            }

            // 无论是不是首次导入：都把 id >= 152 的“冷笑话”更新成不重复的变体
            // （只更新 content，不会动 isFavorite）。
            for (jokeData in jokes) {
                val newContent = maybeRegenerateColdJokeContent(jokeData) ?: continue
                jokeDao.updateContentById(jokeData.id, newContent)
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

    // 如果是“冷笑话”且编号 >= 152，则生成“按编号变化”的正文，避免模板重复。
    private fun maybeRegenerateColdJokeContent(jokeData: JokeData): String? {
        val idNum = extractJokeNumber(jokeData.id) ?: return null
        if (jokeData.category != "冷笑话") return null
        if (idNum < 152) return null
        return generateColdJokeByNumber(idNum)
    }

    private fun extractJokeNumber(id: String): Int? {
        // id 形如 joke152 / joke1151
        val num = id.removePrefix("joke").toIntOrNull() ?: return null
        return num
    }

    private fun generateColdJokeByNumber(n: Int): String {
        val subjects = listOf(
            "闹钟", "被子", "咖啡", "Wi-Fi", "鼠标", "键盘", "电梯", "路由器",
            "手机", "外卖", "枕头", "日历", "充电器", "键盘灯", "午睡"
        )
        val modes = listOf(
            "梦境模式", "静音加速", "托管给明天", "节能模式", "加速冷却",
            "刷新一次", "重启冷静", "自动续命", "沉默加载", "夜跑模式"
        )
        val replies = listOf(
            "时间说", "闹钟说", "手机说", "咖啡说", "路由器说", "键盘说",
            "被子说", "电梯说", "外卖说", "枕头说"
        )
        val punchlines = listOf(
            "你太自信了", "正在加载笑点…", "明天再聊", "先睡再说", "我也没办法",
            "别装了", "已读不回", "再等等", "快点实现", "睡醒再笑"
        )

        val s = subjects[n % subjects.size]
        val m = modes[(n * 3 + 1) % modes.size]
        val r = replies[(n * 7 + 2) % replies.size]
        val p = punchlines[(n * 11 + 3) % punchlines.size]

        return "冷笑话$n：我把${s}设成${m}，结果${r}：${p}。"
    }
    
    data class JokeData(
        val id: String,
        val content: String,
        val category: String
    )
} 