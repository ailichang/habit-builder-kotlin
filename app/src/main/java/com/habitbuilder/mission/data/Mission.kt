package com.habitbuilder.mission.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.habitbuilder.habit.data.Habit
import com.habitbuilder.habit.data.Type
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.util.UUID

@Parcelize
@Entity(
    tableName = "mission_table",
    foreignKeys = [
        ForeignKey(
        entity = Habit::class,
        parentColumns = ["habit_id"],
        childColumns = ["habit_id"],
        onUpdate = CASCADE,
        onDelete = CASCADE)
    ],
    indices = [Index("mission_id")]
)
data class Mission(
    @PrimaryKey
    @ColumnInfo(name = "mission_id")
    val missionId: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "habit_id", index = true)
    val habitId: UUID,

    @ColumnInfo(name = "is_completed")
    var isCompleted: Boolean = false,

    @ColumnInfo(name = "mission_year")
    val year:Int = 0,

    @ColumnInfo(name = "mission_month")
    val month:Int = 0,

    @ColumnInfo(name = "mission_day")
    val day:Int = 0,

    @Embedded
    var timer: Timer? = null,

    @Embedded
    var counter: Counter? = null
) : Parcelable{
    constructor(habit: Habit, localDate: LocalDate): this(habitId = habit.habitId, isCompleted = false, year= localDate.year, month = localDate.monthValue, day = localDate.dayOfMonth){
        when(habit.type){
            Type.COUNTER -> counter = Counter(habit.targetCount)
            Type.TIMER -> timer = Timer(duration = habit.targetDuration)
        }
    }

    fun setMissionCompleted(completed: Boolean) {
        isCompleted = completed
        timer?.let {
            it.setTime(if (isCompleted) it.duration else 0L)
        }
        counter?.let{
            it.setCount(if (isCompleted) it.targetCount else 0)
        }
    }

}
