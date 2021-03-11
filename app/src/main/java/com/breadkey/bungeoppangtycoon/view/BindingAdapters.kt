package com.breadkey.bungeoppangtycoon.view

import android.annotation.SuppressLint
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.breadkey.bungeoppangtycoon.R
import com.breadkey.bungeoppangtycoon.model.*

@SuppressLint("UseCompatLoadingForDrawables")
@BindingAdapter("app:bungeoppangState")
fun setBungeoppangState(view: ImageButton, state: Bungeoppang.State?) {
    if (state == null) {
        view.setImageResource(R.drawable.mold)

        view.scaleX = 1f
    } else {
        val doneness = if (state.isFront) {
            state.frontDoneness
        } else {
            view.scaleX = -1f
            state.backDoneness
        }

        view.setImageResource(doneness.drawable)

        if (state.cream != null) {
            if (state.isFront) {
                view.foreground = view.resources.getDrawable(state.cream.drwable, null)
            } else {
                view.foreground = null
            }
        }
    }
}

val Doneness.drawable
    get(): Int = when (this) {
        Doneness.Rare -> R.drawable.rare
        Doneness.Medium -> R.drawable.medium
        Doneness.WellDone -> R.drawable.well_done
        Doneness.Overcooked -> R.drawable.overcooked

    }

val Cream.drwable
    get(): Int = when (this) {
        Cream.RedBean -> R.drawable.red_bean_cream
        else -> R.drawable.chou_cream
    }

@SuppressLint("SetTextI18n")
@BindingAdapter("money")
fun setMoney(view: TextView, money: Int?) {
    val string = money?.toString() ?: "--"
    view.text = "$string₩"
}

@BindingAdapter("mood")
fun setMood(view: ImageView, mood: Mood?) {
    if (mood == null) {
        view.setImageDrawable(null)
    } else {
        view.setImageResource(mood.drawable)
    }
}

val Mood.drawable get(): Int = when(this) {
    Mood.VeryHappy -> R.drawable.very_happy
    Mood.Happy -> R.drawable.happy
    Mood.Satisfied -> R.drawable.satisfied
    Mood.Neutral -> R.drawable.neutral
    Mood.Disappointed -> R.drawable.disappointed
    Mood.Grumpy -> R.drawable.grumpy
    Mood.VeryGrumpy -> R.drawable.very_grumpy
    Mood.Furious -> R.drawable.furious
}