package com.habitbuilder.mission.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.habitbuilder.habit.data.Habit
import java.util.UUID

@Dao
interface MissionDao {

    @Query("SELECT is_completed FROM mission_table WHERE habit_id =:habitId AND mission_year= :year AND mission_month = :month AND mission_day = :day")
    suspend fun isDailyMissionCompleted(habitId: UUID, year: Int, month: Int, day: Int): Boolean?

    @Query("SELECT * FROM mission_table")
    suspend fun getMissions(): List<Mission>?

    @Transaction
    @Query("SELECT * FROM mission_table WHERE mission_year= :year AND mission_month = :month AND mission_day = :day")
    suspend fun getDailyMissions(year: Int, month: Int, day: Int): List<MissionDetail>?

    @Transaction
    @Query("SELECT * FROM mission_table WHERE is_completed = 1 AND mission_year= :year AND mission_month = :month AND mission_day = :day")
    fun getCompletedDailyMissions(year: Int, month: Int, day: Int): LiveData<List<MissionDetail>>

    @Transaction
    @Query("SELECT * FROM mission_table WHERE is_completed = 0 AND mission_year= :year AND mission_month = :month AND mission_day = :day")
    fun getIncompleteDailyMissions(year: Int, month: Int, day: Int): LiveData<List<MissionDetail>>

    @Transaction
    @Query("SELECT * FROM habit_table as h JOIN mission_table as m ON h.habit_id = m.habit_id WHERE mission_year = :year AND mission_month = :month ORDER BY category")
    suspend fun getMonthlyMissions(year: Int, month: Int): Map<Habit, List<Mission>>?

    @Query(
        "UPDATE mission_table SET target_count=:targetCount, duration=:duration " +
                "WHERE habit_id=:habitId AND mission_year=:year AND mission_month=:month AND mission_day=:day AND is_completed= 0"
    )
    suspend fun updateCondition(habitId: UUID, year: Int, month: Int, day: Int, targetCount: Int, duration: Long)

    @Update
    suspend fun update(vararg mission: Mission)

    @Insert
    suspend fun insert(vararg mission: Mission)

    @Delete
    suspend fun delete(vararg mission: Mission)

    @Query("DELETE FROM mission_table WHERE habit_id IN (:habitIds) AND mission_year =:year AND mission_month=:month AND mission_day=:day")
    suspend fun delete(habitIds: List<UUID>, year: Int, month: Int, day: Int)
}