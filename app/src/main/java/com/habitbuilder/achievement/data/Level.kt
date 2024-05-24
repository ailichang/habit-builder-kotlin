package com.habitbuilder.achievement.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Level(
    val levelNumber: Long = 0,
    val minValue: Long = 0,
    val maxValue: Long = 0
):Parcelable
