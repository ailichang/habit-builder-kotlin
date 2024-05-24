package com.habitbuilder.mission.data

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.habitbuilder.habit.data.Habit
import kotlinx.parcelize.Parcelize

@Parcelize
data class MissionDetail(
    @Embedded
    var mission: Mission,
    @Relation(entity = Habit::class, parentColumn = "habit_id", entityColumn = "habit_id")
    var habit: Habit
):Parcelable
