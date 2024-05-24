package com.habitbuilder.tracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.habitbuilder.R

class TrackerAdapter :
    ListAdapter<MonthlyHabitRecord, TrackerViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackerViewHolder {
        return TrackerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_tracker_item, parent, false))
    }

    override fun onBindViewHolder(holder: TrackerViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    class DiffCallback: DiffUtil.ItemCallback<MonthlyHabitRecord>() {
        override fun areItemsTheSame(
            oldItem: MonthlyHabitRecord,
            newItem: MonthlyHabitRecord
        ): Boolean {
            return oldItem.year == newItem.year && oldItem.month == newItem.month && oldItem.habit.habitId == newItem.habit.habitId
        }

        override fun areContentsTheSame(
            oldItem: MonthlyHabitRecord,
            newItem: MonthlyHabitRecord
        ): Boolean {
            return oldItem == newItem
        }

    }
}
