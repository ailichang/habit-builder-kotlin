package com.habitbuilder.habit.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID

enum class Priority(val value: Int) {
    HIGH(0), MEDIUM(1), LOW(2)
}

enum class Type {
    COUNTER, TIMER
}

enum class Frequency {
    EVERY_MONTH, EVERY_THREE_WEEKS, EVERY_TWO_WEEKS, EVERY_WEEK, EVERY_SIX_DAYS, EVERY_FIVE_DAYS, EVERY_FOUR_DAYS, EVERY_THREE_DAYS, EVERY_TWO_DAYS, EVERYDAY, NONE
}

enum class Category {
    HOUSE_CHORE, WORK, HOBBY, LEARNING, FITNESS, FINANCE, HEALTH, OTHER, NONE
}

@Parcelize
@Entity(tableName = "habit_table")
data class Habit(
    @PrimaryKey
    @ColumnInfo(name = "habit_id")
    val habitId: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "type")
    val type: Type,

    @ColumnInfo(name = "category")
    val category: Category,

    @ColumnInfo(name = "priority")
    val priority: Priority,

    @ColumnInfo(name = "experience_points")
    val experiencePoints: Long = getExperiencePoints(priority),

    @ColumnInfo(name = "frequency")
    val frequency: Frequency = Frequency.NONE,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "location")
    val location: String,

    @ColumnInfo(name = "start_time")
    val startTime: String,

    @ColumnInfo(name = "end_time")
    val endTime: String,

    @ColumnInfo(name = "target_duration")
    val targetDuration: Long = 0L,

    @ColumnInfo(name = "target_count")
    val targetCount: Int = 0,

    @ColumnInfo(name = "created_date")
    val createdDate: String = LocalDate.now().toString(),

    @ColumnInfo(name = "scheduled_days")
    val scheduledDays: ArrayList<Boolean>
) : Parcelable {
    companion object{
        fun getExperiencePoints(priority: Priority): Long {
            return when(priority){
                Priority.LOW -> 1L
                Priority.MEDIUM -> 5L
                Priority.HIGH -> 10L
            }
        }
    }
    fun isScheduled(dayOfWeek: DayOfWeek): Boolean {
        return scheduledDays[dayOfWeek.value - 1]
    }

}