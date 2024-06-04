package com.habitbuilder.tracker

import com.habitbuilder.habit.data.Habit
import com.habitbuilder.mission.data.Mission
import java.time.YearMonth

data class MonthlyHabitRecord (
    val year:Int,
    val month:Int,
    val habit: Habit,
    var monthlyCompleteTable:ArrayList<Boolean> = ArrayList(BooleanArray(YearMonth.of(year, month).lengthOfMonth()).toList())
){
    init {
        monthlyCompleteTable.fill(false)
    }

    constructor(year: Int, month: Int, habit: Habit, missions: List<Mission>):this(year,month,habit){
        for (mission in missions) {
            monthlyCompleteTable[mission.day - 1] = mission.isCompleted
        }
    }
}