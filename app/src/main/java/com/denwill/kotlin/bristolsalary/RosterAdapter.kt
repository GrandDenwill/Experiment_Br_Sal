package com.denwill.kotlin.bristolsalary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.denwill.kotlin.bristolsalary.databinding.SalaryRowBinding

class RosterAdapter (
    private val inflater: LayoutInflater,
    private val onRowClick:(SalaryModel)->Unit
        ):ListAdapter<SalaryModel,RosterRowHolder>(DiffCallback) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RosterRowHolder(
            SalaryRowBinding.inflate(inflater, parent,  false),
            onRowClick = onRowClick
        )

    override fun onBindViewHolder(holder: RosterRowHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private object DiffCallback : DiffUtil.ItemCallback<SalaryModel>() {
    override fun areItemsTheSame(oldItem: SalaryModel, newItem: SalaryModel) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: SalaryModel, newItem: SalaryModel) =
        oldItem.date == newItem.date
}