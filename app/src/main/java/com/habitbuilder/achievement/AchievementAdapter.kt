package com.habitbuilder.achievement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.habitbuilder.R
import com.habitbuilder.achievement.data.AchievementDetail
import com.habitbuilder.achievement.data.ExperienceLevel

class AchievementAdapter: ListAdapter<AchievementDetail, com.habitbuilder.achievement.AchievementViewHolder>(
    com.habitbuilder.achievement.AchievementAdapter.DiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.habitbuilder.achievement.AchievementViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_achievement_badge, parent, false)
        return com.habitbuilder.achievement.AchievementViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: com.habitbuilder.achievement.AchievementViewHolder, position: Int) {
        val achievementDetails:AchievementDetail = currentList[position]
        val level = ExperienceLevel.getLevel(achievementDetails.achievement.experiencePoints)
        holder.onBind(achievementDetails, level)
    }

    class DiffCallback: DiffUtil.ItemCallback<AchievementDetail>(){
        override fun areItemsTheSame(oldItem: AchievementDetail, newItem: AchievementDetail): Boolean {
            return oldItem.achievement.id == newItem.achievement.id && oldItem.habit.habitId == newItem.habit.habitId
        }

        override fun areContentsTheSame(oldItem: AchievementDetail, newItem: AchievementDetail): Boolean {
            return oldItem == newItem
        }

    }
}