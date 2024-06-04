package com.habitbuilder.achievement.data

import androidx.lifecycle.LiveData
import com.habitbuilder.habit.data.Habit
import java.util.UUID
import javax.inject.Inject

enum class ExperienceUpdateType(val value: Int){
    INCREASE(1), DECREASE(-1), NONE(0)
}
class AchievementRepository @Inject constructor(private val achievementDao: AchievementDao) {

    val achievementDetails: LiveData<List<AchievementDetail>> = achievementDao.getAchievementDetails()

    suspend fun update(habit: Habit, experienceUpdateType:ExperienceUpdateType){
        achievementDao.update(habit.habitId, experienceUpdateType.value * habit.experiencePoints)
    }

    suspend fun insert(vararg achievements: Achievement){
        achievementDao.insert(*achievements)
    }

    suspend fun delete(habitId:UUID){
        achievementDao.deleteByHabitId(habitId)
    }
}