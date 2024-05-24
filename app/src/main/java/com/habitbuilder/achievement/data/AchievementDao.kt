package com.habitbuilder.achievement.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import java.util.UUID

@Dao
interface AchievementDao {
    @Transaction
    @Query("SELECT * FROM achievement_table")
    fun getAchievementDetails(): LiveData<List<AchievementDetail>>

    @Transaction
    @Query("SELECT habit_id FROM achievement_table")
    fun getAchievementHabitIds(): LiveData<List<UUID>>
    @Insert
    suspend fun insert(vararg achievements: Achievement)
    @Query("UPDATE achievement_table SET experience_points = experience_points + :experiencePoints WHERE habit_id = :habitId ")
    suspend fun update(habitId: UUID, experiencePoints: Long)

    @Query("DELETE FROM achievement_table WHERE habit_id = :habitId ")
    fun deleteByHabitId(habitId: UUID)
}