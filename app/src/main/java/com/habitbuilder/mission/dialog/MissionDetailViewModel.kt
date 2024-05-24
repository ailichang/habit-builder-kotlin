package com.habitbuilder.mission.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitbuilder.achievement.data.AchievementRepository
import com.habitbuilder.mission.data.MissionDetail
import com.habitbuilder.mission.data.MissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionDetailViewModel @Inject constructor(private val missionRepository: MissionRepository, private val achievementRepository: AchievementRepository): ViewModel(){
    fun update(missionDetail: MissionDetail){
        viewModelScope.launch (Dispatchers.IO) {
            missionRepository.update(missionDetail.mission)
            achievementRepository.update(missionDetail.habit, missionDetail.mission.isCompleted)
        }
    }
}