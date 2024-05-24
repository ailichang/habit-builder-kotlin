package com.habitbuilder.util

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.habitbuilder.R
import com.habitbuilder.habit.data.Category
import com.habitbuilder.habit.data.Habit
import com.habitbuilder.habit.data.Priority
import com.habitbuilder.habit.data.Type
import com.google.android.material.chip.Chip
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale


class WidgetFormatter {
    companion object{
        fun setTime(timeText: TextView, startTime: String?, endTime: String?) {
            val stringBuilder = StringBuilder()
            if (TextUtils.isEmpty(startTime)) {
                stringBuilder.append(timeText.context.getString(R.string.time_any))
            } else {
                stringBuilder.append(startTime)
            }
            if (!TextUtils.isEmpty(endTime)) {
                stringBuilder.append(timeText.context.getString(R.string.time_to)).append(endTime)
            }
            timeText.text = stringBuilder.toString()
        }

        fun setScheduledDays(scheduledDays: TextView, isScheduled: ArrayList<Boolean>) {
            val context = scheduledDays.context
            var weekdayCount = 0
            var weekendCount = 0
            var lastDay: DayOfWeek? = null
            var secondLastDay: DayOfWeek? = null
            for (i in isScheduled.indices) {
                if (isScheduled[i]) {
                    secondLastDay = lastDay
                    lastDay = DayOfWeek.entries[i]
                    if (DayOfWeek.SATURDAY.value - 1 == i || DayOfWeek.SUNDAY.value - 1 == i) {
                        weekendCount++
                    } else {
                        weekdayCount++
                    }
                }
            }
            val everyWeekday = weekdayCount == 5
            val everyWeekend = weekendCount == 2
            if (everyWeekday && everyWeekend) {
                scheduledDays.setText(R.string.schedule_every_day)
            } else if (everyWeekday && weekendCount == 0) {
                scheduledDays.setText(R.string.schedule_every_weekday)
            } else if (everyWeekend && weekdayCount == 0) {
                scheduledDays.setText(R.string.schedule_every_weekend)
            } else if (weekdayCount + weekendCount == 1) {
                scheduledDays.text =
                    context.getString(
                        R.string.schedule_list_week_of_day_one, lastDay?.getDisplayName(
                            TextStyle.FULL, Locale.getDefault()
                        )
                    )
            } else {
                val stringBuilder = StringBuilder()
                for (i in 0 until checkNotNull(secondLastDay).value - 1) {
                    if (isScheduled[i]) {
                        stringBuilder.append(
                            DayOfWeek.entries[i].getDisplayName(
                                TextStyle.FULL,
                                Locale.getDefault()
                            )
                        ) .append(context.getString(R.string.day_delimiter))
                    }
                }
                stringBuilder.append(
                    secondLastDay.getDisplayName(
                        TextStyle.FULL,
                        Locale.getDefault()
                    )
                )
                scheduledDays.text = context.getString(
                    R.string.schedule_list_week_of_day_multiple,
                    stringBuilder.toString(),
                    lastDay?.getDisplayName(
                        TextStyle.FULL, Locale.getDefault()
                    )
                )
            }
        }

        fun setCondition(condition: TextView, habit: Habit) {
            when (habit.type) {
                Type.COUNTER -> {
                    val count: Int = habit.targetCount
                    if (count == 1) {
                        condition.setText(R.string.counter_no_repeat)
                    } else {
                        condition.text =
                            condition.resources.getQuantityString(
                                R.plurals.counter_repeat,
                                count,
                                count
                            )
                    }
                    condition.setCompoundDrawablesWithIntrinsicBounds(
                        AppCompatResources.getDrawable(
                            condition.context,
                            R.drawable.ic_baseline_repeat_24
                        ), null, null, null
                    )
                }

                Type.TIMER -> {
                    condition.setCompoundDrawablesWithIntrinsicBounds(
                        AppCompatResources.getDrawable(
                            condition.context,
                            R.drawable.ic_baseline_timelapse_24
                        ), null, null, null
                    )
                    condition.text = condition.resources.getString(
                        R.string.timer_duration,
                        TimeUtil.secondsToHMSString(habit.targetDuration, TimeUtil.DurationStyle.HMS)
                    )
                }
                else -> {}
            }
        }

        fun setPriorityTag(priorityTag: Chip, priority: Priority, enabled: Boolean) {
            val colorRes: Int
            when (priority) {
                Priority.HIGH -> {
                    colorRes = if (enabled) R.color.tagRed else R.color.colorOnSurfaceDim
                    priorityTag.setChipStrokeColorResource(colorRes)
                    priorityTag.setText(R.string.priority_high)
                    priorityTag.setTextColor(ContextCompat.getColor(priorityTag.context, colorRes))
                }

                Priority.MEDIUM -> {
                    colorRes = if (enabled) R.color.tagYellow else R.color.colorOnSurfaceDim
                    priorityTag.setChipStrokeColorResource(colorRes)
                    priorityTag.setText(R.string.priority_medium)
                    priorityTag.setTextColor(ContextCompat.getColor(priorityTag.context, colorRes))
                }

                Priority.LOW -> {
                    colorRes = if (enabled) R.color.tagBlue else R.color.colorOnSurfaceDim
                    priorityTag.setChipStrokeColorResource(colorRes)
                    priorityTag.setText(R.string.priority_low)
                    priorityTag.setTextColor(ContextCompat.getColor(priorityTag.context, colorRes))
                }
            }
        }

        @ColorRes
        fun getCategoryColor(category: Category): Int {
            return when (category) {
                Category.FINANCE -> R.color.tagPurple
                Category.FITNESS -> R.color.tagGreen
                Category.HEALTH -> R.color.tagPink
                Category.HOBBY -> R.color.tagRed
                Category.HOUSE_CHORE -> R.color.tagYellow
                Category.LEARNING -> R.color.tagOrange
                Category.WORK -> R.color.tagBlue
                else -> R.color.tagNavy
            }
        }

        @DrawableRes
        fun getCategoryIcon(category: Category): Int {
            return when (category) {
                Category.FINANCE -> R.drawable.baseline_monetization_on_24
                Category.FITNESS -> R.drawable.ic_exercise_24
                Category.HEALTH -> R.drawable.ic_health_24
                Category.HOBBY -> R.drawable.baseline_interests_24
                Category.HOUSE_CHORE -> R.drawable.ic_house_chore_24
                Category.LEARNING -> R.drawable.ic_learning_24
                Category.WORK -> R.drawable.ic_work_24
                else -> R.drawable.ic_baseline_assignment_24
            }
        }

        fun setCategoryFilterTag(categoryTag: Chip, category: Category) {
            categoryTag.visibility = View.VISIBLE
            val chipColor = getCategoryColor(category)
            categoryTag.setChipStrokeColorResource(chipColor)
            categoryTag.setChipBackgroundColorResource(chipColor)
            categoryTag.setChipIconResource(getCategoryIcon(category))
            when (category) {
                Category.FINANCE -> categoryTag.setText(R.string.category_finance)
                Category.FITNESS -> categoryTag.setText(R.string.category_fitness)
                Category.HEALTH -> categoryTag.setText(R.string.category_health)
                Category.HOBBY -> categoryTag.setText(R.string.category_hobby)
                Category.HOUSE_CHORE -> categoryTag.setText(R.string.category_house_chore)
                Category.LEARNING -> categoryTag.setText(R.string.category_learning)
                Category.WORK -> categoryTag.setText(R.string.category_work)
                else -> categoryTag.visibility = View.GONE
            }
        }
    }
}
