package com.habitbuilder.achievement

import androidx.lifecycle.ViewModel
import com.habitbuilder.achievement.data.AchievementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AchievementViewModel @Inject constructor(private val achievementRepository: AchievementRepository): ViewModel() {
    fun getAchievementDetails() = achievementRepository.achievementDetails
}