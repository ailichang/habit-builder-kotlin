package com.habitbuilder.util

import java.util.Locale

class TimeUtil {
    enum class DurationStyle {
        COLON,
        HMS_ABBR,
        HMS
    }

    companion object{
        fun secondsToHMSString(seconds: Long, style: DurationStyle): String {
            val hour = seconds / 3600
            val minute = (seconds - hour * 3600) / 60
            val sec = seconds - hour * 3600 - minute * 60
            val stringBuilder = StringBuilder()
            return when (style) {
                DurationStyle.COLON -> {
                    if (hour == 0L) String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        minute,
                        sec
                    ) else String.format(
                        Locale.getDefault(), "%02d:%02d:%02d", hour, minute, sec
                    )
                }

                DurationStyle.HMS_ABBR -> {
                    if (hour != 0L) stringBuilder.append(hour).append(" hr ")
                    if (minute != 0L) stringBuilder.append(minute).append(" min ")
                    if (sec != 0L) stringBuilder.append(sec).append(" sec")
                    stringBuilder.toString()
                }

                DurationStyle.HMS -> {
                    if (hour != 0L) {
                        stringBuilder.append(hour)
                        if (hour > 1) stringBuilder.append(" hours ") else stringBuilder.append(" hour ")
                    }
                    if (minute != 0L) {
                        stringBuilder.append(minute)
                        if (minute > 1) stringBuilder.append(" minutes ") else stringBuilder.append(" minute ")
                    }
                    if (sec != 0L) {
                        stringBuilder.append(sec)
                        if (sec > 1) stringBuilder.append(" seconds") else stringBuilder.append(" second")
                    }
                    stringBuilder.toString()
                }
            }
        }

        fun stringHMSToSeconds(str: String): Long{
            val numbers = str.trim { it <= ' ' }.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val length = numbers.size
            val hour = if (length == 2) 0 else numbers[0].toLong()
            val minute = if (length == 2) numbers[0].toLong() else numbers[1].toLong()
            var sec = if (length == 2) numbers[1].toLong() else numbers[2].toLong()
            sec += 60L * minute + 3600L * hour
            return sec
        }
    }
}