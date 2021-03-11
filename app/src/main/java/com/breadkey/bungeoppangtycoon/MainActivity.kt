package com.breadkey.bungeoppangtycoon

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.breadkey.bungeoppangtycoon.viewmodel.BungeoppangTycoonViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: BungeoppangTycoonViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.gameOver.observe(this, Observer { gameOver ->
            if (gameOver) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)

                builder.setMessage("Game Over!")
                builder.setPositiveButton("Restart?") { _, _ ->
                    viewModel.restart()
                }

                builder.show()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onResume() {
        super.onResume()
        viewModel.resume()
    }
}