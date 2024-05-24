package com.habitbuilder.mission.data

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.room.ColumnInfo
import androidx.room.Ignore
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.Locale
import kotlin.math.min


@Parcelize
data class Counter (
    @ColumnInfo(name = "target_count")
    val targetCount: Int,

    @ColumnInfo(name = "current_count")
    var currentCount: Int

): Parcelable{

    @IgnoredOnParcel
    @Ignore
    val currentCountLiveData: MutableLiveData<Int> = MutableLiveData(currentCount)

    constructor(targetCount: Int): this(targetCount, 0)

    fun add() {
        if (currentCount < targetCount) {
            currentCount++
            currentCountLiveData.postValue(currentCount)
        }
    }

    fun minus() {
        if (currentCount == 0) return
        currentCount--
        currentCountLiveData.postValue(currentCount)
    }

    fun reset() {
        currentCount = 0
        currentCountLiveData.postValue(currentCount)
    }

    val isTargetReached: Boolean
        get() = currentCount >= targetCount

    val progressText: String
        get() = String.format(
            Locale.getDefault(),
            "%d/%d",
            min(currentCount, targetCount),
            targetCount
        )

}

