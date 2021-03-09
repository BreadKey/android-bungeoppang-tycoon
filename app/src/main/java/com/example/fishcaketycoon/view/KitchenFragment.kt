package com.example.fishcaketycoon.view

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.fishcaketycoon.MainActivity
import com.example.fishcaketycoon.R
import com.example.fishcaketycoon.databinding.FragmentKitchenBinding
import com.example.fishcaketycoon.model.FishcakeTycoon
import com.example.fishcaketycoon.viewmodel.FishcakeTycoonViewModel

class KitchenFragment : Fragment(R.layout.fragment_kitchen) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentKitchenBinding.bind(view)
        val viewModel = (requireActivity() as MainActivity).obtainViewModel()
        binding.viewModel = viewModel
    }
}