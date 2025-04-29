package com.example.joke_of_the_day.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.joke_of_the_day.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var adapter: JokeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]
        
        setupRecyclerView()
        setupObservers()
    }
    
    private fun setupRecyclerView() {
        adapter = JokeAdapter(
            onShareClick = { joke ->
                shareJoke(viewModel.shareJoke(joke))
            },
            onFavoriteClick = { joke ->
                viewModel.toggleFavorite(joke)
            }
        )
        binding.recyclerFavorites.adapter = adapter
    }
    
    private fun setupObservers() {
        viewModel.favoriteJokes.observe(viewLifecycleOwner) { jokes ->
            adapter.submitList(jokes)
            
            // 如果没有收藏的笑话，显示提示文本
            if (jokes.isEmpty()) {
                binding.textNoFavorites.visibility = View.VISIBLE
                binding.recyclerFavorites.visibility = View.GONE
            } else {
                binding.textNoFavorites.visibility = View.GONE
                binding.recyclerFavorites.visibility = View.VISIBLE
            }
        }
    }
    
    private fun shareJoke(jokeText: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, jokeText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "分享笑话"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 