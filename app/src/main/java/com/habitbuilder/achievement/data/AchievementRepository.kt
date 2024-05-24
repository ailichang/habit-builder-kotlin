package com.habitbuilder.achievement.data

import androidx.lifecycle.LiveData
import com.habitbuilder.habit.data.Habit
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class AchievementRepository @Inject constructor(private val achievementDao: AchievementDao) {
    val achievementDetails: LiveData<List<AchievementDetail>> = achievementDao.getAchievementDetails()

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    suspend fun update(habit: Habit, increase:Boolean){
        achievementDao.update(habit.habitId, if(increase) habit.experiencePoints else -1 * habit.experiencePoints)
    }

    suspend fun insert(vararg achievements: Achievement){
        achievementDao.insert(*achievements)
    }

    fun delete(habitId:UUID){
        executorService.execute { achievementDao.deleteByHabitId(habitId) }
    }
}