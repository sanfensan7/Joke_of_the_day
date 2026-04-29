package com.example.joke_of_the_day.ui.pet

import android.content.Context
import android.view.HapticFeedbackConstants
import android.view.animation.OvershootInterpolator
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import com.example.joke_of_the_day.R
import kotlin.math.max

class PetAvatar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    enum class State { Idle, Happy }

    private val imageView: ImageView
    private var seed: String = "MyPet"
    private var species: PetSpecies = PetSpecies.Robot
    private var state: State = State.Idle
    private var onPetPoked: (() -> Unit)? = null

    private val svgImageLoader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }

    init {
        inflate(context, R.layout.view_pet_avatar, this)
        imageView = findViewById(R.id.pet_avatar_image)

        isClickable = true
        isFocusable = true

        setOnClickListener {
            poke()
        }
    }

    fun setSeed(newSeed: String) {
        seed = newSeed
        setState(State.Idle)
    }

    fun setSpecies(newSpecies: PetSpecies) {
        species = newSpecies
        setState(State.Idle)
    }

    fun setPet(newSpecies: PetSpecies, newSeed: String) {
        species = newSpecies
        seed = newSeed
        setState(State.Idle)
    }

    fun setOnPetPokedListener(listener: (() -> Unit)?) {
        onPetPoked = listener
    }

    fun setState(newState: State) {
        state = newState
        val url = when (state) {
            State.Idle -> species.idleUrl(seed)
            State.Happy -> species.happyUrl(seed)
        }
        loadUrl(url)
    }

    private fun poke() {
        // 震动反馈（系统默认弱反馈）
        performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

        // 弹跳缩放动画
        playBounce()

        // URL 瞬切笑脸
        setState(State.Happy)

        // 通知外部更新笑话/经验
        onPetPoked?.invoke()

        // 约 1 秒后恢复默认图
        postDelayed({ setState(State.Idle) }, 1000L)
    }

    private fun playBounce() {
        // 取消上一次动画，避免快速连点叠加
        imageView.animate().cancel()

        val start = max(0.85f, imageView.scaleX.takeIf { it > 0f } ?: 1f)
        imageView.scaleX = start
        imageView.scaleY = start

        imageView.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(450L)
            .setInterpolator(OvershootInterpolator(2.2f))
            .start()
    }

    private fun loadUrl(url: String) {
        imageView.load(url, svgImageLoader) {
            crossfade(true)
        }
    }

    fun getSpecies(): PetSpecies = species
}
