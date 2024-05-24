package com.habitbuilder.habit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.habitbuilder.R
import com.habitbuilder.habit.data.Habit
import com.google.android.material.card.MaterialCardView

class HabitAdapter: ListAdapter<Habit,HabitViewHolder>(DiffCallback()) {
    private var expandedPosition = -1
    private var previousExpandedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder{
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_habit_item, parent, false)
        return HabitViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit:Habit = getItem(holder.bindingAdapterPosition)
        holder.onBind(habit)
        /*  https://stackoverflow.com/Questions/27203817/recyclerview-Expand-collapse-ITems
            fixes the issue of overlapping card when collapsing
         */
        val isExpanded = holder.bindingAdapterPosition == expandedPosition
        if(isExpanded) previousExpandedPosition = holder.bindingAdapterPosition
        holder.expandableView.visibility = if(isExpanded) View.VISIBLE else View.GONE
        holder.itemView.isActivated = isExpanded
        holder.itemView.setOnClickListener{
            v ->
            expandedPosition = if (isExpanded) -1 else holder.bindingAdapterPosition
            TransitionManager.beginDelayedTransition((v as MaterialCardView), AutoTransition())
            notifyItemChanged(previousExpandedPosition) //collapse other open cards, only allow one card to expand
            notifyItemChanged(holder.bindingAdapterPosition)
        }
    }
    class DiffCallback:DiffUtil.ItemCallback<Habit>(){
        override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem.habitId == newItem.habitId
        }

        override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem == newItem
        }

    }
}