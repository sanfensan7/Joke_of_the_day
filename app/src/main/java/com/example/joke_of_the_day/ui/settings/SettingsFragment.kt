package com.example.joke_of_the_day.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.joke_of_the_day.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        
        setupObservers()
        setupListeners()
    }
    
    private fun setupObservers() {
        viewModel.notificationsEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.switchNotifications.isChecked = enabled
            binding.timePicker.isEnabled = enabled
        }
        
        viewModel.notificationHour.observe(viewLifecycleOwner) { hour ->
            binding.timePicker.hour = hour
        }
        
        viewModel.notificationMinute.observe(viewLifecycleOwner) { minute ->
            binding.timePicker.minute = minute
        }
        
        viewModel.darkModeEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.switchDarkMode.isChecked = enabled
        }
        
        viewModel.fontSize.observe(viewLifecycleOwner) { size ->
            when (size) {
                SettingsViewModel.FONT_SIZE_SMALL -> binding.radioSmall.isChecked = true
                SettingsViewModel.FONT_SIZE_MEDIUM -> binding.radioMedium.isChecked = true
                SettingsViewModel.FONT_SIZE_LARGE -> binding.radioLarge.isChecked = true
            }
        }
    }
    
    private fun setupListeners() {
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNotificationsEnabled(isChecked)
        }
        
        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            viewModel.setNotificationTime(hourOfDay, minute)
        }
        
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkModeEnabled(isChecked)
        }
        
        binding.radioGroupFontSize.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.radioSmall.id -> viewModel.setFontSize(SettingsViewModel.FONT_SIZE_SMALL)
                binding.radioMedium.id -> viewModel.setFontSize(SettingsViewModel.FONT_SIZE_MEDIUM)
                binding.radioLarge.id -> viewModel.setFontSize(SettingsViewModel.FONT_SIZE_LARGE)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 