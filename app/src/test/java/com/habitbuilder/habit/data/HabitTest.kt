package com.habitbuilder.habit.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID

class HabitTest {
    private val highPriorityWeekendHabit = Habit(
        UUID.randomUUID(), Type.TIMER, Category.FINANCE, Priority.HIGH,
        title = "Testing", description = "description", location = "location", startTime = "13:00", endTime = "15:00",
        targetDuration = 100L, createdDate = "2024-05-30", scheduledDays = arrayListOf(false, false, false, false, false, true, true)
    )
    private val mediumPriorityEverydayHabit = Habit(
        UUID.randomUUID(), Type.COUNTER, Category.FITNESS, Priority.MEDIUM,
        title = "Drink water", description = "description 2", location = "location", startTime = "01:00", endTime = "02:00",
        targetCount = 10, createdDate = "2024-05-30", scheduledDays = arrayListOf(true, true, true, true, true, true, true)
    )
    private val lowPriorityWeekdayHabit = Habit(
        UUID.randomUUID(), Type.COUNTER, Category.FITNESS, Priority.LOW,
        title = "Read article", description = "description 3", location = "location", startTime = "01:00", endTime = "02:00",
        targetCount = 1, createdDate = "2024-05-31", scheduledDays = arrayListOf(true, true, true, true, true, false, false)
    )
    private val monday:LocalDate = LocalDate.parse("2024-05-27")
    private val tuesday:LocalDate = LocalDate.parse("2024-05-28")
    private val wednesday:LocalDate = LocalDate.parse("2024-05-29")
    private val thursday:LocalDate = LocalDate.parse("2024-05-30")
    private val friday:LocalDate = LocalDate.parse("2024-05-31")
    private val saturday:LocalDate = LocalDate.parse("2024-06-01")
    private val sunday:LocalDate = LocalDate.parse("2024-06-02")
    @Test
    fun constructor_setsCorrectExperiencePoints(){
        assertThat(highPriorityWeekendHabit.experiencePoints).isEqualTo(10L)
        assertThat(mediumPriorityEverydayHabit.experiencePoints).isEqualTo(5L)
        assertThat(lowPriorityWeekdayHabit.experiencePoints).isEqualTo(1L)
    }
    @Test
    fun isScheduled_scheduled_returnsTrue(){
        assertThat(highPriorityWeekendHabit.isScheduled(saturday)).isTrue()
        assertThat(highPriorityWeekendHabit.isScheduled(sunday)).isTrue()
        assertThat(mediumPriorityEverydayHabit.isScheduled(monday)).isTrue()
        assertThat(mediumPriorityEverydayHabit.isScheduled(tuesday)).isTrue()
        assertThat(mediumPriorityEverydayHabit.isScheduled(wednesday)).isTrue()
        assertThat(mediumPriorityEverydayHabit.isScheduled(thursday)).isTrue()
        assertThat(mediumPriorityEverydayHabit.isScheduled(friday)).isTrue()
        assertThat(mediumPriorityEverydayHabit.isScheduled(saturday)).isTrue()
        assertThat(mediumPriorityEverydayHabit.isScheduled(sunday)).isTrue()
        assertThat(lowPriorityWeekdayHabit.isScheduled(monday)).isTrue()
        assertThat(lowPriorityWeekdayHabit.isScheduled(tuesday)).isTrue()
        assertThat(lowPriorityWeekdayHabit.isScheduled(wednesday)).isTrue()
        assertThat(lowPriorityWeekdayHabit.isScheduled(thursday)).isTrue()
        assertThat(lowPriorityWeekdayHabit.isScheduled(friday)).isTrue()
    }

    @Test
    fun isScheduled_notScheduled_returnsFalse(){
        assertThat(highPriorityWeekendHabit.isScheduled(monday)).isFalse()
        assertThat(highPriorityWeekendHabit.isScheduled(tuesday)).isFalse()
        assertThat(highPriorityWeekendHabit.isScheduled(wednesday)).isFalse()
        assertThat(highPriorityWeekendHabit.isScheduled(thursday)).isFalse()
        assertThat(highPriorityWeekendHabit.isScheduled(friday)).isFalse()
        assertThat(lowPriorityWeekdayHabit.isScheduled(saturday)).isFalse()
        assertThat(lowPriorityWeekdayHabit.isScheduled(sunday)).isFalse()
    }
}