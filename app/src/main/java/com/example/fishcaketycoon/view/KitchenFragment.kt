package com.example.fishcaketycoon.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.fishcaketycoon.R
import com.example.fishcaketycoon.databinding.FragmentKitchenBinding
import com.example.fishcaketycoon.viewmodel.FishcakeTycoonViewModel

class KitchenFragment : Fragment(R.layout.fragment_kitchen) {
    private val viewModel: FishcakeTycoonViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentKitchenBinding.bind(view)
        binding.viewModel = viewModel
    }
}