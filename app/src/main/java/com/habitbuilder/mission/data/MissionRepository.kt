package com.habitbuilder.mission.data

import androidx.lifecycle.LiveData
import com.habitbuilder.habit.data.Habit
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

class MissionRepository @Inject constructor(private val missionDao: MissionDao) {
    fun getDailyMissions(year: Int, month: Int, day: Int): LiveData<List<Mission>>{
        return missionDao.getDailyMissions(year, month, day)
    }

    fun getIncompleteDailyMissions(year: Int, month: Int, day: Int): LiveData<List<MissionDetail>> {
        return missionDao.getIncompleteDailyMissions(year, month, day)
    }

    fun getCompletedDailyMissions(year: Int, month: Int, day: Int): LiveData<List<MissionDetail>>{
        return missionDao.getCompletedDailyMissions(year, month, day)
    }

    fun getMonthlyMissions(year: Int, month: Int): LiveData<Map<Habit, List<Mission>>> {
        return missionDao.getMonthlyMissions(year, month)
    }

    fun getNonExistMissions(): LiveData<Map<Habit, List<Mission>>> {
        return missionDao.getNonExistMissions()
    }

    suspend fun insert(vararg missions: Mission){
        missionDao.insert(*missions)
    }

    suspend fun update(vararg missions: Mission) {
        missionDao.update(*missions)
    }

    suspend fun updateCondition(habitId: UUID, today:LocalDate, targetCount: Int, duration: Long) {
        missionDao.updateCondition(habitId, today.year, today.monthValue, today.dayOfMonth, targetCount, duration)
    }

    suspend fun delete(vararg missions: Mission) {
        missionDao.delete(*missions)
    }

    suspend fun delete(habitId: UUID, localDate:LocalDate){
        missionDao.delete(habitId, localDate.year, localDate.monthValue, localDate.dayOfMonth)
    }

}