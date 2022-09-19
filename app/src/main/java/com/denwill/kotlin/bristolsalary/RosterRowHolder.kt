package com.denwill.kotlin.bristolsalary

import androidx.recyclerview.widget.RecyclerView
import com.denwill.kotlin.bristolsalary.databinding.SalaryRowBinding
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class RosterRowHolder (
    private val binding:SalaryRowBinding,
    private val onRowClick:(SalaryModel)->Unit
    ):RecyclerView.ViewHolder(binding.root)
{
    fun bind(model:SalaryModel){
        val PATTERN_FORMAT = "MM.dd.yyyy"
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
            .withZone(ZoneId.systemDefault())
        binding.apply {
            root.setOnClickListener{onRowClick(model)}
            date.text = formatter.format(model.date)
            revenue.text = model.revenue.toString()
            countWorkers.text = model.countWorkers.toString()
            baseSalary.text = model.baseSalary.toString()
            salary.text = model.salary.toString()
        }
    }
}