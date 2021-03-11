package com.breadkey.bungeoppangtycoon.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.breadkey.bungeoppangtycoon.R
import com.breadkey.bungeoppangtycoon.databinding.FragmentCustomersBinding
import com.breadkey.bungeoppangtycoon.viewmodel.BungeoppangTycoonViewModel

class CustomersFragment: Fragment(R.layout.fragment_customers) {
    val viewModel: BungeoppangTycoonViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentCustomersBinding.bind(view).viewModel = viewModel
    }
}