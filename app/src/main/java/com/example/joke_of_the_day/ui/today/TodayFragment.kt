package com.example.joke_of_the_day.ui.today

import android.os.Bundle
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModelProvider
import kotlin.random.Random
import com.example.joke_of_the_day.databinding.FragmentTodayBinding
import com.example.joke_of_the_day.ui.pet.PetSpecies

class TodayFragment : Fragment() {

    private var _binding: FragmentTodayBinding? = null
    private val binding get() = _binding!!

    private val prefs by lazy { requireContext().getSharedPreferences("joke_preferences", 0) }

    private var exp: Int = 0
    private var seed: String = "MyPet"
    private var species: PetSpecies = PetSpecies.Robot
    private var touchStartX = 0f
    private var touchStartY = 0f
    private lateinit var viewModel: TodayViewModel
    private var hasLoadedJokeOnce = false
    private var petBreathingAnimator: AnimatorSet? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.jokeText.text = "正在加载笑话..."

        // 使用 ViewModel 接入 Room + jokes.json 数据源
        val factory = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[TodayViewModel::class.java]

        viewModel.todayJoke.observe(viewLifecycleOwner) { joke ->
            if (joke == null) return@observe

            if (hasLoadedJokeOnce) {
                showJokeWithAnimation(joke.content)
            } else {
                binding.jokeText.text = joke.content
                hasLoadedJokeOnce = true
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrBlank()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
            }
        }

        exp = prefs.getInt(KEY_EXP, 0)
        species = prefs.getString(KEY_SPECIES, null)
            ?.let { saved -> PetSpecies.entries.firstOrNull { it.name == saved } }
            ?: PetSpecies.Robot

        seed = prefs.getString(KEY_SEED, null) ?: generateSeed(6)

        // 确保持久化有值（第一次启动）
        prefs.edit()
            .putString(KEY_SPECIES, species.name)
            .putString(KEY_SEED, seed)
            .apply()

        binding.petAvatar.setPet(species, seed)
        binding.petNameText.text = species.displayName
        binding.petAvatar.setOnPetPokedListener {
            onPetPoked()
        }
        binding.btnRefreshPet.setOnClickListener {
            animateRefreshButtonThenRefresh()
        }
        setupGestureParallax()
        updateLevelText()
        startBreathingAnimation()
    }

    private fun refreshPet() {
        val newSpecies = PetSpecies.entries[Random.nextInt(PetSpecies.entries.size)]
        val newSeed = generateSeed(6)
        species = newSpecies
        seed = newSeed
        prefs.edit()
            .putString(KEY_SPECIES, newSpecies.name)
            .putString(KEY_SEED, newSeed)
            .apply()
        binding.petAvatar.setPet(newSpecies, newSeed)
        binding.petNameText.text = newSpecies.displayName
    }

    private fun animateRefreshButtonThenRefresh() {
        binding.btnRefreshPet.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        binding.btnRefreshPet.animate().cancel()
        binding.btnRefreshPet.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .alpha(0.86f)
            .setDuration(80L)
            .setInterpolator(FastOutSlowInInterpolator())
            .withEndAction {
                binding.btnRefreshPet.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .rotationBy(80f)
                    .setDuration(360L)
                    .setInterpolator(OvershootInterpolator(2.2f))
                    .withEndAction {
                        binding.btnRefreshPet.rotation = 0f
                    }
                    .start()
            }
            .start()

        refreshPet()
        // 让“换宠物”也同步换一条真实笑话
        viewModel.getNextJoke()
    }

    private fun onPetPoked() {
        // 从数据库随机抽取下一条笑话（可按分类；未选分类时随机）
        viewModel.getNextJoke()

        // 每次点击经验 +1
        exp += 1
        prefs.edit().putInt(KEY_EXP, exp).apply()
        updateLevelText()
    }

    private fun showJokeWithAnimation(text: String) {
        playJokeCardPopIn()
        binding.jokeText.animate().cancel()
        binding.jokeText.animate()
            .alpha(0f)
            .setDuration(90L)
            .withEndAction {
                binding.jokeText.text = text
                binding.jokeText.animate()
                    .alpha(1f)
                    .setDuration(200L)
                    .start()
            }
            .start()
    }

    private fun playJokeCardPopIn() {
        binding.jokeCard.animate().cancel()
        binding.jokeCard.translationY = 10f * resources.displayMetrics.density
        binding.jokeCard.scaleX = 0.992f
        binding.jokeCard.scaleY = 0.992f
        binding.jokeCard.alpha = 0.97f
        binding.jokeCard.animate()
            .translationY(0f)
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(260L)
            .setInterpolator(OvershootInterpolator(0.75f))
            .start()
    }

    private fun setupGestureParallax() {
        val gestureListener = View.OnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    stopBreathingAnimation()
                    touchStartX = event.rawX
                    touchStartY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - touchStartX
                    val dy = event.rawY - touchStartY

                    // 纵向下拉：大标题动态缩放
                    val pullDown = dy.coerceAtLeast(0f)
                    val titleScale = (1f + pullDown / 700f).coerceAtMost(1.14f)
                    binding.titleToday.scaleX = titleScale
                    binding.titleToday.scaleY = titleScale

                    // 卡片微弱视差（头像更明显，笑话卡更轻）
                    val xOffset = (dx / 24f).coerceIn(-10f, 10f)
                    val yOffset = (dy / 30f).coerceIn(-12f, 12f)
                    binding.petAvatar.translationX = xOffset
                    binding.petAvatar.translationY = yOffset
                    binding.jokeCard.translationX = xOffset * 0.55f
                    binding.jokeCard.translationY = yOffset * 0.55f
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    resetMicroInteractionState()
                    startBreathingAnimation()
                }
            }
            false
        }

        binding.root.setOnTouchListener(gestureListener)
        binding.petAvatar.setOnTouchListener(gestureListener)
        binding.jokeCard.setOnTouchListener(gestureListener)
    }

    private fun resetMicroInteractionState() {
        val interp = FastOutSlowInInterpolator()
        binding.titleToday.animate().scaleX(1f).scaleY(1f).setDuration(240L).setInterpolator(interp).start()
        binding.petAvatar.animate().translationX(0f).translationY(0f).setDuration(260L).setInterpolator(interp).start()
        binding.jokeCard.animate().translationX(0f).translationY(0f).setDuration(260L).setInterpolator(interp).start()
    }

    private fun updateLevelText() {
        val level = (exp / EXP_PER_LEVEL) + 1
        binding.levelText.text = "Lv. $level  EXP $exp"
    }

    private fun startBreathingAnimation() {
        if (_binding == null || petBreathingAnimator?.isRunning == true) return

        val floatDistance = 6f * resources.displayMetrics.density
        val floatAnim = ObjectAnimator.ofFloat(binding.petAvatar, View.TRANSLATION_Y, 0f, -floatDistance).apply {
            duration = 2400L
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
        val scaleXAnim = ObjectAnimator.ofFloat(binding.petAvatar, View.SCALE_X, 1f, 1.016f).apply {
            duration = 2400L
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
        val scaleYAnim = ObjectAnimator.ofFloat(binding.petAvatar, View.SCALE_Y, 1f, 1.016f).apply {
            duration = 2400L
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
        }

        petBreathingAnimator = AnimatorSet().apply {
            playTogether(floatAnim, scaleXAnim, scaleYAnim)
            start()
        }
    }

    private fun stopBreathingAnimation() {
        petBreathingAnimator?.cancel()
        petBreathingAnimator = null
        if (_binding != null) {
            binding.petAvatar.animate().cancel()
            binding.petAvatar.translationY = 0f
            binding.petAvatar.scaleX = 1f
            binding.petAvatar.scaleY = 1f
        }
    }

    override fun onResume() {
        super.onResume()
        startBreathingAnimation()
    }

    override fun onPause() {
        stopBreathingAnimation()
        super.onPause()
    }

    private fun generateSeed(length: Int): String {
        val alphabet = "abcdefghijklmnopqrstuvwxyz"
        return buildString(length) {
            repeat(length) {
                append(alphabet[Random.nextInt(alphabet.length)])
            }
        }
    }

    override fun onDestroyView() {
        stopBreathingAnimation()
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val KEY_SEED = "pet_seed"
        const val KEY_SPECIES = "pet_species"
        const val KEY_EXP = "pet_exp"
        const val EXP_PER_LEVEL = 10
    }
} 