package com.example.joke_of_the_day.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.joke_of_the_day.data.model.Joke
import com.example.joke_of_the_day.databinding.ItemJokeBinding
import com.example.joke_of_the_day.util.formatDateForDisplay

class JokeAdapter(
    private val onShareClick: (Joke) -> Unit,
    private val onFavoriteClick: (Joke) -> Unit
) : ListAdapter<Joke, JokeAdapter.JokeViewHolder>(JokeDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JokeViewHolder {
        val binding = ItemJokeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return JokeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JokeViewHolder, position: Int) {
        val joke = getItem(position)
        holder.bind(joke)
    }

    inner class JokeViewHolder(private val binding: ItemJokeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(joke: Joke) {
            binding.textJokeCategory.text = "分类: ${joke.category}"
            binding.textJokeContent.text = joke.content
            binding.textJokeDate.text = formatDateForDisplay(joke.date)
            
            binding.btnShare.setOnClickListener {
                onShareClick(joke)
            }
            
            binding.btnFavorite.setOnClickListener {
                onFavoriteClick(joke)
            }
            
            // 根据收藏状态设置图标，这里假设要有两个不同的收藏图标
            // 实际应用中可能需要设置不同的图标或颜色
        }
    }

    object JokeDiffCallback : DiffUtil.ItemCallback<Joke>() {
        override fun areItemsTheSame(oldItem: Joke, newItem: Joke): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Joke, newItem: Joke): Boolean {
            return oldItem == newItem
        }
    }
} 