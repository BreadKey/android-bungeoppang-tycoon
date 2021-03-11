package com.breadkey.bungeoppangtycoon.view

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.breadkey.bungeoppangtycoon.R
import com.breadkey.bungeoppangtycoon.model.*

@SuppressLint("UseCompatLoadingForDrawables")
@BindingAdapter("app:bungeoppangState")
fun setBungeoppangState(view: ImageButton, state: Bungeoppang.State?) {
    if (state == null) {
        view.setImageResource(R.drawable.mold)
        view.foreground = null

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
                view.foreground = view.resources.getDrawable(state.cream.pointDrawable, null)
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

val Cream.pointDrawable
    get(): Int = when (this) {
        Cream.RedBean -> R.drawable.red_bean_point
        else -> R.drawable.chou_point
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

val Mood.drawable
    get(): Int = when (this) {
        Mood.VeryHappy -> R.drawable.very_happy
        Mood.Happy -> R.drawable.happy
        Mood.Satisfied -> R.drawable.satisfied
        Mood.Neutral -> R.drawable.neutral
        Mood.Disappointed -> R.drawable.disappointed
        Mood.Grumpy -> R.drawable.grumpy
        Mood.VeryGrumpy -> R.drawable.very_grumpy
        Mood.Furious -> R.drawable.furious
    }

@SuppressLint("SetTextI18n")
@BindingAdapter("order")
fun setOrder(view: CardView, order: Customer.Order?) {
    if (order == null) {
        view.visibility = View.GONE
    } else {
        view.visibility = View.VISIBLE
        val redBeanInfo =
            view.findViewById<View>(R.id.red_bean_info).also { it.visibility = View.VISIBLE }
        val chouInfo = view.findViewById<View>(R.id.chou_info).also { it.visibility = View.VISIBLE }

        if (order.redBean == 0) {
            redBeanInfo.visibility = View.GONE
        } else if (order.chou == 0) {
            chouInfo.visibility = View.GONE
        }

        view.findViewById<TextView>(R.id.red_bean_count).text = "x${order.redBean}"
        view.findViewById<TextView>(R.id.chou_count).text = "x${order.chou}"
    }
}

@BindingAdapter("cookedBungeoppangs")
fun setCookedBungeoppangs(view: RecyclerView, bungeoppangs: List<Bungeoppang>) {
    (view.adapter as? CookedBungeoppangsAdapter)?.setCookedBungeoppangs(bungeoppangs)
}

@SuppressLint("UseCompatLoadingForDrawables")
@BindingAdapter("cookedBungeoppang")
fun setCookedBungeoppang(view: ImageButton, state: Bungeoppang.State) {
    if (state.frontDoneness == Doneness.WellDone && state.backDoneness == Doneness.WellDone) {
        view.setImageResource(R.drawable.well_done_cooked)
    } else {
        val largestDoneness: Doneness
        val smallerDoneness: Doneness

        if (state.frontDoneness >= state.backDoneness) {
            largestDoneness = state.frontDoneness
            smallerDoneness = state.backDoneness
        } else {
            largestDoneness = state.backDoneness
            smallerDoneness = state.frontDoneness
        }

        val cookedDrawable = when(largestDoneness) {
            Doneness.WellDone -> {
                R.drawable.medium_cooked
            }
            Doneness.Medium -> {
                when(smallerDoneness) {
                    Doneness.Rare -> R.drawable.rare_cooked
                    else -> R.drawable.medium_cooked
                }
            }
            Doneness.Overcooked -> {
                R.drawable.overcooked_cooked
            }
            else -> R.drawable.rare_cooked
        }

        view.setImageResource(cookedDrawable)
    }
    when(state.cream) {
        Cream.Chou -> view.foreground = view.resources.getDrawable(R.drawable.chou_point_cooked, null)
        Cream.RedBean -> view.foreground = view.resources.getDrawable(R.drawable.red_bean_point_cooked, null)
    }
}


val Cream.color: Int
    get() = when (this) {
        Cream.RedBean -> R.color.read_bean
        else -> R.color.chou
    }