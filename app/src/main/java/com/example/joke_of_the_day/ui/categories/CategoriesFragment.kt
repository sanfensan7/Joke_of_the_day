package com.example.joke_of_the_day.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.joke_of_the_day.R
import com.example.joke_of_the_day.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: CategoriesViewModel
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[CategoriesViewModel::class.java]
        
        setupRecyclerView()
        setupObservers()
    }
    
    private fun setupRecyclerView() {
        adapter = CategoryAdapter { category ->
            // 选择分类并保存
            viewModel.selectCategory(category)
            Toast.makeText(context, "已选择: $category", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerCategories.adapter = adapter
    }
    
    private fun setupObservers() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.submitList(categories)
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.recyclerCategories.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(context, getString(R.string.error_loading), Toast.LENGTH_SHORT).show()
            }
        }
        
        viewModel.selectedCategory.observe(viewLifecycleOwner) { category ->
            // 可以在这里更新UI以显示当前选中的分类
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 