package com.breadkey.bungeoppangtycoon.view

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.breadkey.bungeoppangtycoon.R
import com.breadkey.bungeoppangtycoon.databinding.CookedBungeoppangBinding
import com.breadkey.bungeoppangtycoon.model.Bungeoppang
import com.breadkey.bungeoppangtycoon.viewmodel.BungeoppangTycoonViewModel

class CookedBungeoppangsAdapter(private val viewModel: BungeoppangTycoonViewModel) :
    RecyclerView.Adapter<CookedBungeoppangsAdapter.ViewHolder>() {
    private var cookedBungeoppangs = listOf<Bungeoppang>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        CookedBungeoppangBinding.inflate(LayoutInflater.from(parent.context)).apply {
            viewModel = this@CookedBungeoppangsAdapter.viewModel
        }
    )

    override fun getItemCount(): Int = cookedBungeoppangs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bungeoppang = cookedBungeoppangs[position]
        with(holder) {
            binding.bungeoppang = bungeoppang
            binding.cookedBungeoppang.clearColorFilter()
            if (viewModel.isSelected(bungeoppang)) {
                highlight()
            }
            binding.cookedBungeoppang.setOnClickListener {
                if (viewModel.isSelected(bungeoppang)) {
                    viewModel.deselect(bungeoppang)
                    binding.cookedBungeoppang.clearColorFilter()
                } else {
                    viewModel.select(bungeoppang)
                    highlight()
                }
            }
        }
    }

    fun setCookedBungeoppangs(cookedBungeoppangs: List<Bungeoppang>) {
        this.cookedBungeoppangs = cookedBungeoppangs
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: CookedBungeoppangBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val highlightColor = binding.root.resources.getColor(
            R.color.material_on_primary_emphasis_high_type,
            null
        )

        fun highlight() {
            binding.cookedBungeoppang.clearColorFilter()
            binding.cookedBungeoppang.setColorFilter(
                highlightColor, PorterDuff.Mode.MULTIPLY
            )
        }
    }
}