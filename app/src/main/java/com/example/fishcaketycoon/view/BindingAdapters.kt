package com.example.fishcaketycoon.view

import android.annotation.SuppressLint
import android.widget.Button
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import com.example.fishcaketycoon.R
import com.example.fishcaketycoon.model.Doneness
import com.example.fishcaketycoon.model.Fishcake

@SuppressLint("UseCompatLoadingForDrawables")
@BindingAdapter("app:fishcakeState")
fun setFishcakeState(view: Button, state: Fishcake.State?) {
    if (state == null) {
        view.foreground = view.resources.getDrawable(R.drawable.mold, null)
        view.scaleX = 1f
    } else {
        val doneness = if (state.isFront) {
            state.frontDoneness
        } else {
            view.scaleX = -1f
            state.backDoneness
        }

        when (doneness) {
            Doneness.Rare -> {
                view.foreground = view.resources.getDrawable(R.drawable.rare, null)
            }
            Doneness.Medium -> {
                view.foreground = view.resources.getDrawable(R.drawable.medium, null)
            }
            Doneness.WellDone -> {
                view.foreground = view.resources.getDrawable(R.drawable.well_done, null)
            }
            Doneness.Overcooked -> {
                view.foreground = view.resources.getDrawable(R.drawable.overcooked, null)
            }
        }
    }
}