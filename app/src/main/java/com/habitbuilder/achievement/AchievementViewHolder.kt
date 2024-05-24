package com.habitbuilder.achievement

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.habitbuilder.R
import com.habitbuilder.achievement.data.AchievementDetail
import com.habitbuilder.achievement.data.Level
import com.habitbuilder.util.WidgetFormatter.Companion.getCategoryIcon
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.util.Locale

class AchievementViewHolder (itemView: View): ViewHolder(itemView) {

    private val titleText:TextView
    private val levelText: TextView
    private val xpText: TextView
    private val currentXpText: TextView
    private val targetXpText: TextView
    private val icon: ImageView
    private val progressBar: LinearProgressIndicator
    init{
        itemView.isEnabled = false
        titleText = itemView.findViewById(R.id.achievement_title)
        levelText = itemView.findViewById(R.id.achievement_level)
        icon = itemView.findViewById(R.id.achievement_icon)
        xpText = itemView.findViewById(R.id.achievement_xp)
        currentXpText = itemView.findViewById(R.id.achievement_current_xp)
        targetXpText = itemView.findViewById(R.id.achievement_target_xp)
        progressBar = itemView.findViewById(R.id.achievement_progress_indicator)
    }

    fun onBind(achievementDetail:AchievementDetail, level: Level){
        titleText.text = achievementDetail.habit.title
        icon.setImageResource(getCategoryIcon(achievementDetail.habit.category))
        levelText.text = levelText.resources.getString(R.string.level, level.levelNumber)
        val xp = achievementDetail.achievement.experiencePoints
        val minXp = level.minValue
        val targetXp = level.maxValue - minXp
        val currentXp = xp - minXp
        xpText.text = String.format(Locale.getDefault(), "%d", xp)
        currentXpText.text = String.format(Locale.getDefault(), "%d", currentXp)
        targetXpText.text = String.format(Locale.getDefault(), "%d", targetXp)
        progressBar.max = targetXp.toInt()
        progressBar.progress = currentXp.toInt()
    }
}