package com.habitbuilder.mission.data

import android.os.CountDownTimer
import android.os.Parcelable
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.habitbuilder.util.TimeUtil
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

@Parcelize
data class Timer(
    @ColumnInfo(name = "duration")
    val duration: Long = 0,

    @ColumnInfo(name = "elapsed_time")
    var elapsedTime:Long = 0,

): Parcelable {
    companion object{
        private const val countDownIntervalInMillis: Long = 1000
        private const val oneSecondInMillis: Long = 1000
    }

    @IgnoredOnParcel
    @Ignore
    val remainingTime: MutableLiveData<Long> = MutableLiveData<Long>()

    @VisibleForTesting
    @IgnoredOnParcel
    @Ignore
    var countDownTimer: CountDownTimer? = null

    @IgnoredOnParcel
    @Ignore
    var isTimerInitialized:Boolean = false

    @IgnoredOnParcel
    @Ignore
    var isTimerFinished:Boolean = false

    @IgnoredOnParcel
    @Ignore
    var progress: Double = 0.0
        get() = if (duration == 0L) 0.0 else min(elapsedTime / duration.toDouble(), 1.0)
        private set

    @IgnoredOnParcel
    @Ignore
    var progressText: String = "0%"
        get() = String.format(Locale.getDefault(), "%d%%", (progress * 100.0).toInt())
        private set

    @IgnoredOnParcel
    @Ignore
    var remainingTimeText: String = "00:00"
        get() = TimeUtil.secondsToHMSString(remainingTime.value?: 0L, TimeUtil.DurationStyle.COLON)
        private set

    @IgnoredOnParcel
    @Ignore
    var isTargetReached:Boolean = false
        get() = elapsedTime >= duration
        private set

    init {
        setTime(elapsedTime)
        isTimerInitialized = false
    }

    fun setTime(elapsedTime: Long){
        this.elapsedTime = elapsedTime
        remainingTime.postValue(max(0, duration - elapsedTime))
    }

    fun start(){
        this.countDownTimer = object : CountDownTimer((remainingTime.value?:0L) * oneSecondInMillis, countDownIntervalInMillis) {
            override fun onTick(millisUntilFinished: Long) {
                setTime(duration - millisUntilFinished/oneSecondInMillis)
            }
            override fun onFinish() {
                isTimerFinished = true
            }
        }.start()
        isTimerInitialized = true
    }

    fun pause(){
        countDownTimer?.cancel()
    }

    fun reset(){
        setTime(0)
    }

    fun cancel(){
        setTime(0)
        countDownTimer?.cancel()
        isTimerFinished = false
        isTimerInitialized = false
    }
}


