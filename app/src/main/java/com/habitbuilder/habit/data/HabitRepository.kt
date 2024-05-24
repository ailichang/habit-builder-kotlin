package com.habitbuilder.habit.data

import androidx.lifecycle.LiveData
import javax.inject.Inject


class HabitRepository @Inject constructor(private val habitDao:HabitDao){

    fun getHabitList():LiveData<List<Habit>>{
        return habitDao.getHabitList()
    }

    suspend fun insert(habit: Habit){
        habitDao.insert(habit)
    }

    suspend fun update(habit: Habit) {
        habitDao.update(habit)
    }

    suspend fun delete(habit: Habit) {
        habitDao.delete(habit)
    }
}