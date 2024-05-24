package com.habitbuilder.habit.data

import java.time.LocalDate

data class ScheduledHabitsEvent(val habitList: List<Habit>, val scheduledDate:LocalDate)
