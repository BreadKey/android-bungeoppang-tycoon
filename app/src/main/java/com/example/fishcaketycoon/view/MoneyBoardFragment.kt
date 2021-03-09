package com.example.fishcaketycoon.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.fishcaketycoon.R
import com.example.fishcaketycoon.databinding.FragmentMoneyBoardBinding
import com.example.fishcaketycoon.viewmodel.FishcakeTycoonViewModel

class MoneyBoardFragment : Fragment(R.layout.fragment_money_board) {
    private val viewModel: FishcakeTycoonViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentMoneyBoardBinding.bind(view).viewModel = viewModel
    }
}