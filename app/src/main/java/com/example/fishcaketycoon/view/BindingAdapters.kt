package com.example.fishcaketycoon.view

import android.annotation.SuppressLint
import android.widget.Button
import android.widget.TextView
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

        view.foreground = view.resources.getDrawable(doneness.drawable, null)
    }
}

val Doneness.drawable
    get(): Int {
        return when (this) {
            Doneness.Rare -> R.drawable.rare
            Doneness.Medium -> R.drawable.medium
            Doneness.WellDone -> R.drawable.well_done
            Doneness.Overcooked -> R.drawable.overcooked
        }
    }

@SuppressLint("SetTextI18n")
@BindingAdapter("money")
fun setMoney(view: TextView, money: Int?) {
    val string = money?.toString() ?: "--"
    view.text = "$string₩"
}