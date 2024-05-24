package com.habitbuilder.habit.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.UUID

@Dao
interface HabitDao {

    @Query("SELECT * FROM habit_table ORDER BY priority")
    fun getHabitList(): LiveData<List<Habit>>

    @Query("SELECT habit_id FROM habit_table")
    fun getHabitIdsList(): LiveData<List<UUID>>

    @Insert
    suspend fun insert(vararg habit: Habit)

    @Delete
    suspend fun delete(vararg habit: Habit)

    @Update
    suspend fun update(vararg habit: Habit)
}