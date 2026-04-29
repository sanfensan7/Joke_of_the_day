package com.example.joke_of_the_day.ui.pet

/**
 * 用 DiceBear 的不同“风格/物种”来模拟不同小动物。
 * 这里用本地枚举即可扩展，不依赖网络列表。
 */
enum class PetSpecies(
    val displayName: String,
    val dicebearStyle: String,
    val supportsHappyMouth: Boolean
) {
    Robot("机器人", "bottts", true),
    PixelPet("像素宠物", "pixel-art", false),
    EmojiBall("表情球", "fun-emoji", false),
    DoodleCritter("涂鸦小怪", "croodles", false),
    ThumbBuddy("拇指伙伴", "thumbs", false);

    fun idleUrl(seed: String): String =
        "https://api.dicebear.com/9.x/$dicebearStyle/svg?seed=$seed"

    fun happyUrl(seed: String): String {
        return if (supportsHappyMouth) {
            "https://api.dicebear.com/9.x/$dicebearStyle/svg?seed=$seed&mouth=smile01,smile02"
        } else {
            // 对不支持 mouth 的风格，用“轻微变体”实现瞬切效果
            "https://api.dicebear.com/9.x/$dicebearStyle/svg?seed=$seed&flip=true"
        }
    }
}

