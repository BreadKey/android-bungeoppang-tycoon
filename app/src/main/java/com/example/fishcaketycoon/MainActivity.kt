package com.example.fishcaketycoon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.example.fishcaketycoon.databinding.FishcakeBinding
import com.example.fishcaketycoon.model.Fishcake
import com.example.fishcaketycoon.model.FishcakeViewModel
import com.trello.rxlifecycle4.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val testFishcake = Fishcake()
        val testViewModel: FishcakeViewModel by viewModels()
        testViewModel.setFishcake(testFishcake)
        val binding = FishcakeBinding.bind(findViewById(R.id.test_fishcake))
        binding.viewModel = testViewModel

        Observable.interval(1, TimeUnit.SECONDS).bindToLifecycle(this).subscribe {
            testFishcake.bake(1.0)
        }
    }
}