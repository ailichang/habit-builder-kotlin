package com.habitbuilder.habit.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitbuilder.IoDispatcher
import com.habitbuilder.achievement.data.Achievement
import com.habitbuilder.achievement.data.AchievementRepository
import com.habitbuilder.achievement.data.ExperienceUpdateType
import com.habitbuilder.habit.data.Habit
import com.habitbuilder.habit.data.HabitRepository
import com.habitbuilder.mission.data.Mission
import com.habitbuilder.mission.data.MissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HabitEditorViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val missionRepository: MissionRepository,
    private val achievementRepository: AchievementRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher): ViewModel() {

    fun insert(habit: Habit) {
        viewModelScope.launch(dispatcher){
            habitRepository.insert(habit)
            missionRepository.insert(Mission(habit, LocalDate.now()))
            achievementRepository.insert(Achievement(habitId = habit.habitId))
        }
    }

    fun update(oldHabit:Habit, newHabit: Habit) {
        viewModelScope.launch(dispatcher){
            val today = LocalDate.now()
            if(oldHabit.isScheduled(today.dayOfWeek) && !newHabit.isScheduled(today.dayOfWeek)){
                missionRepository.isDailyMissionCompleted(oldHabit.habitId, today)?.let {
                    if (it) {
                        achievementRepository.update(oldHabit, ExperienceUpdateType.DECREASE)
                    }
                }
                missionRepository.delete(oldHabit.habitId, today)
            } else if(!oldHabit.isScheduled(today.dayOfWeek) && newHabit.isScheduled(today.dayOfWeek)){
                missionRepository.insert(Mission(newHabit, today))
            }
            habitRepository.update(newHabit)
            missionRepository.updateCondition(newHabit.habitId, today, newHabit.targetCount, newHabit.targetDuration)
        }
    }

    fun delete(habit: Habit) {
        viewModelScope.launch(dispatcher) {
            habitRepository.delete(habit)
        }
    }

}