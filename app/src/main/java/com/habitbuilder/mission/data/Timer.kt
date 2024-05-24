package com.habitbuilder.mission.data

import android.os.CountDownTimer
import android.os.Parcelable
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
    val remainingTime: MutableLiveData<Long> = MutableLiveData<Long>(duration)

    @IgnoredOnParcel
    @Ignore
    private var countDownTimer: CountDownTimer? = null

    @IgnoredOnParcel
    @Ignore
    var isTimerInitialized:Boolean = false

    @IgnoredOnParcel
    @Ignore
    var isTimerFinished:Boolean = false

    @IgnoredOnParcel
    @Ignore
    var progress: Double =
        if (duration == 0L) 0.0 else min(elapsedTime / duration.toDouble(), 1.0)


    @IgnoredOnParcel
    @Ignore
    var progressText: String =
        String.format(Locale.getDefault(), "%d%%", (progress * 100.0).toInt())


    @IgnoredOnParcel
    @Ignore
    var remainingTimeText: String = TimeUtil.secondsToHMSString(remainingTime.value?: 0L, TimeUtil.DurationStyle.COLON)


    @IgnoredOnParcel
    @Ignore
    var isTargetReached:Boolean = elapsedTime >= duration

    init {
        setRemainingTimeInMillis(elapsedTime)
        isTimerInitialized = false
    }

    fun setRemainingTimeInMillis(elapsedTime: Long){
        this.elapsedTime = elapsedTime
        remainingTime.postValue(max(0, duration - elapsedTime))
    }

    fun start(){
        this.countDownTimer = object : CountDownTimer((remainingTime.value?:0L) * oneSecondInMillis, countDownIntervalInMillis) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime.value = millisUntilFinished/oneSecondInMillis
                elapsedTime = duration - (remainingTime.value?:0L).toInt()
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

    fun cancel(){
        countDownTimer?.cancel()
        isTimerFinished = false
        isTimerInitialized = false
        remainingTime.value = duration
        elapsedTime = 0
    }
}


