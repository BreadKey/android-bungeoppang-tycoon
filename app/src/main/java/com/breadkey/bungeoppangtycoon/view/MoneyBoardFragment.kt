package com.breadkey.bungeoppangtycoon.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.breadkey.bungeoppangtycoon.R
import com.breadkey.bungeoppangtycoon.databinding.FragmentMoneyBoardBinding
import com.breadkey.bungeoppangtycoon.viewmodel.BungeoppangTycoonViewModel

class MoneyBoardFragment : Fragment(R.layout.fragment_money_board) {
    private val viewModel: BungeoppangTycoonViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentMoneyBoardBinding.bind(view).viewModel = viewModel
    }
}