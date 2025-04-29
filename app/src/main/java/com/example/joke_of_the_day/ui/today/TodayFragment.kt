package com.example.joke_of_the_day.ui.today

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.joke_of_the_day.R
import com.example.joke_of_the_day.databinding.FragmentTodayBinding
import com.example.joke_of_the_day.data.model.Joke
import com.example.joke_of_the_day.util.formatDateForDisplay

class TodayFragment : Fragment() {

    private var _binding: FragmentTodayBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: TodayViewModel

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
        
        viewModel = ViewModelProvider(this)[TodayViewModel::class.java]
        
        setupObservers()
        setupListeners()
    }
    
    private fun setupObservers() {
        viewModel.todayJoke.observe(viewLifecycleOwner) { joke: Joke ->
            binding.textJokeCategory.text = "分类: ${joke.category}"
            binding.textJokeContent.text = joke.content
            
            // 根据收藏状态更新按钮文本
            updateFavoriteButtonState(joke.isFavorite)
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.textJokeContent.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.textJokeCategory.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(context, getString(R.string.error_loading), Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupListeners() {
        binding.btnShare.setOnClickListener {
            shareJoke()
        }
        
        binding.btnFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }
        
        binding.btnNextJoke.setOnClickListener {
            viewModel.getNextJoke()
        }
    }
    
    private fun updateFavoriteButtonState(isFavorite: Boolean) {
        if (isFavorite) {
            binding.btnFavorite.text = getString(R.string.unfavorite)
        } else {
            binding.btnFavorite.text = getString(R.string.favorite)
        }
    }
    
    private fun shareJoke() {
        viewModel.todayJoke.value?.let { joke: Joke ->
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, 
                    "【${joke.category}】\n${joke.content}\n\n——来自 每日笑话 APP")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "分享笑话"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 