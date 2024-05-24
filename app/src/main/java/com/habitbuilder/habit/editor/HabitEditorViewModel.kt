package com.habitbuilder.habit.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitbuilder.achievement.data.Achievement
import com.habitbuilder.achievement.data.AchievementRepository
import com.habitbuilder.habit.data.Habit
import com.habitbuilder.habit.data.HabitRepository
import com.habitbuilder.mission.data.Mission
import com.habitbuilder.mission.data.MissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HabitEditorViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val missionRepository: MissionRepository,
    private val achievementRepository: AchievementRepository): ViewModel() {

    fun insert(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO){
            val deferredHabitInsertion = async{habitRepository.insert(habit)}
            deferredHabitInsertion.await()
            missionRepository.insert(Mission(habit, LocalDate.now()))
            achievementRepository.insert(Achievement(habitId = habit.habitId))
        }
    }

    fun update(oldHabit:Habit, newHabit: Habit) {
        viewModelScope.launch (Dispatchers.IO){
            habitRepository.update(newHabit)
            val today = LocalDate.now()
            if(oldHabit.isScheduled(today.dayOfWeek) && !newHabit.isScheduled(today.dayOfWeek)){
                missionRepository.delete(oldHabit.habitId, today)
            } else if(!oldHabit.isScheduled(today.dayOfWeek) && newHabit.isScheduled(today.dayOfWeek)){
                missionRepository.insert(Mission(newHabit, today))
            }
            if(oldHabit.isScheduled(today.dayOfWeek)){
                achievementRepository.update(oldHabit, false)
            }
            missionRepository.updateCondition(newHabit.habitId, today, newHabit.targetCount, newHabit.targetDuration)
        }
    }

    fun delete(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            habitRepository.delete(habit)
        }
    }

}