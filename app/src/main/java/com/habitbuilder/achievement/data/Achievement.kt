package com.habitbuilder.achievement.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.habitbuilder.habit.data.Habit
import kotlinx.parcelize.Parcelize
import java.util.UUID


@Parcelize
@Entity(tableName = "achievement_table",
    foreignKeys = [
        ForeignKey(
        entity = Habit::class,
        parentColumns = ["habit_id"],
        childColumns = ["habit_id"],
        onUpdate = CASCADE,
        onDelete = CASCADE)
    ])
data class Achievement(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "achievement_id")
    val id: Long = 0,
    @ColumnInfo(name = "habit_id", index = true)
    val habitId: UUID,
    @ColumnInfo(name = "experience_points")
    val experiencePoints: Long = 0,
): Parcelable
