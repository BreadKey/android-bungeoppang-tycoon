package com.breadkey.bungeoppangtycoon.view

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.breadkey.bungeoppangtycoon.R
import com.breadkey.bungeoppangtycoon.databinding.FragmentKitchenBinding
import com.breadkey.bungeoppangtycoon.databinding.MoldBinding
import com.breadkey.bungeoppangtycoon.model.Cream
import com.breadkey.bungeoppangtycoon.model.BungeoppangTycoon
import com.breadkey.bungeoppangtycoon.viewmodel.BungeoppangTycoonViewModel

class KitchenFragment : Fragment(R.layout.fragment_kitchen) {
    private val viewModel: BungeoppangTycoonViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentKitchenBinding.bind(view)
        binding.viewModel = viewModel
        setCreamCanDrag(binding.redBeanCream, Cream.RedBean)
        setCreamCanDrag(binding.chouCream, Cream.Chou)
        repeat(BungeoppangTycoon.MOLD_LENGTH) { index ->
            setMoldCanDrop(binding.moldGrid.getChildAt(index), index)
        }
        binding.cookedBungeoppangs.adapter = CookedBungeoppangsAdapter(viewModel)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setCreamCanDrag(view: View, cream: Cream) {
        view.setOnTouchListener { v, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                val item = ClipData.Item(cream.name)
                val dragData = ClipData(
                    cream.name,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item
                )

                val myShadow = CreamShadowBuilder(v, cream)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    v.startDragAndDrop(dragData, myShadow, null, 0)
                } else {
                    v.startDrag(dragData, myShadow, null, 0)
                }
                true
            } else {
                false
            }
        }
    }

    private inner class CreamShadowBuilder(v: View, cream: Cream) : View.DragShadowBuilder(v) {
        @SuppressLint("UseCompatLoadingForDrawables")
        private val shadow = resources.getDrawable(cream.pickDrawable, null)

        override fun onProvideShadowMetrics(size: Point, touch: Point) {
            val width: Int = view.width * 2
            val height: Int = view.height * 2

            shadow.setBounds(0, 0, width, height)
            size.set(width, height)

            touch.set(width / 2, height / 2)
        }

        override fun onDrawShadow(canvas: Canvas) {
            shadow.draw(canvas)
        }
    }

    private fun setMoldCanDrop(view: View, index: Int) {
        fun highlight(v: View, cream: Cream) {
            (v as? ImageButton)?.setColorFilter(
                resources.getColor(
                    if (viewModel.canAddCream(index)) cream.color else R.color.design_default_color_error
                    , null
                ), PorterDuff.Mode.MULTIPLY
            )
            v.invalidate()
        }

        fun cancel(v: View) {
            (v as? ImageButton)?.clearColorFilter()
            v.invalidate()
        }

        view.findViewById<ImageButton>(R.id.mold).setOnDragListener { v, dragEvent ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    dragEvent.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    val cream = Cream.valueOf(dragEvent.clipDescription.label.toString())
                    highlight(v, cream)

                    true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    cancel(v)
                    true
                }

                DragEvent.ACTION_DROP -> {
                    val cream = Cream.valueOf(dragEvent.clipData.getItemAt(0).text.toString())
                    viewModel.addCream(index, cream)
                    cancel(v)
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private val Cream.pickDrawable: Int
        get() = when (this) {
            Cream.RedBean -> R.drawable.pick_red_bean_cream
            else -> R.drawable.pick_chou_cream
        }
}