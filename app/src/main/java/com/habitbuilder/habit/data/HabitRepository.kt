package com.habitbuilder.habit.data

import androidx.lifecycle.LiveData
import java.time.LocalDate
import javax.inject.Inject


class HabitRepository @Inject constructor(private val habitDao:HabitDao){

    fun getOrderedHabitList():LiveData<List<Habit>>{
        return habitDao.getHabitsOrderByPriority()
    }
    suspend fun getHabits():List<Habit>?{
        return habitDao.getHabits()
    }
    suspend fun getScheduledHabits(localDate: LocalDate): List<Habit>?{
        return habitDao.getHabits()?.filter { it.isScheduled(localDate)}
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