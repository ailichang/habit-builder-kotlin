package com.habitbuilder.mission.data

import androidx.lifecycle.LiveData
import com.habitbuilder.habit.data.Habit
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

class MissionRepository @Inject constructor(private val missionDao: MissionDao) {
    suspend fun isDailyMissionCompleted(habitId: UUID, localDate:LocalDate):Boolean?{
        return missionDao.isDailyMissionCompleted(habitId, localDate.year, localDate.monthValue, localDate.dayOfMonth)
    }

    suspend fun getDailyMissionHabitIds(localDate: LocalDate): List<UUID>?{
        return missionDao.getDailyMissions(localDate.year, localDate.monthValue, localDate.dayOfMonth)?.map { it.mission.habitId }
    }

    suspend fun getDailyMissions(localDate: LocalDate): List<MissionDetail>?{
        return missionDao.getDailyMissions(localDate.year, localDate.monthValue, localDate.dayOfMonth)
    }

    fun getIncompleteDailyMissions(localDate: LocalDate): LiveData<List<MissionDetail>> {
        return missionDao.getIncompleteDailyMissions(localDate.year, localDate.monthValue, localDate.dayOfMonth)
    }

    fun getCompletedDailyMissions(localDate: LocalDate): LiveData<List<MissionDetail>>{
        return missionDao.getCompletedDailyMissions(localDate.year, localDate.monthValue, localDate.dayOfMonth)
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

    suspend fun updateCondition(habitId: UUID, localDate:LocalDate, targetCount: Int, duration: Long) {
        missionDao.updateCondition(habitId, localDate.year, localDate.monthValue, localDate.dayOfMonth, targetCount, duration)
    }

    suspend fun delete(vararg missions: Mission) {
        missionDao.delete(*missions)
    }

    suspend fun delete(habitIds: List<UUID>, localDate:LocalDate){
        missionDao.delete(habitIds, localDate.year, localDate.monthValue, localDate.dayOfMonth)
    }

}