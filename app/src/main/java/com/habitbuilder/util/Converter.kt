package com.habitbuilder.util

import androidx.room.TypeConverter
import com.habitbuilder.habit.data.Category
import com.habitbuilder.habit.data.Frequency
import com.habitbuilder.habit.data.Priority
import com.habitbuilder.habit.data.Type

class Converter {
    @TypeConverter
    fun fromBooleanArraylist(booleans: ArrayList<Boolean>): String {
        val stringBuilder = StringBuilder()
        booleans.forEach { boolean ->
            stringBuilder.append(boolean).append(",")
        }
        return stringBuilder.toString()
    }

    @TypeConverter
    fun toBooleanArrayList(string: String): ArrayList<Boolean> {
        val result = ArrayList<Boolean>()
        val list: List<String> = string.split(",")
        list.map { result.add(it.toBoolean()) }
        return result
    }

    @TypeConverter
    fun fromPriorityToInt(priority: Priority): Int = priority.value

    @TypeConverter
    fun fromIntToPriority(priority: Int): Priority {
        return when (priority) {
            0 -> Priority.HIGH
            1 -> Priority.MEDIUM
            else -> Priority.LOW
        }
    }

    @TypeConverter
    fun fromHabitTypeToString(type: Type): String = type.toString()

    @TypeConverter
    fun fromStringToHabitType(type: String): Type {
        return when (type) {
            "TIMER" -> Type.TIMER
            else -> Type.COUNTER
        }
    }

    @TypeConverter
    fun fromStringToFrequency(frequency: String?): Frequency {
        return when (frequency) {
            "EVERY_MONTH" -> Frequency.EVERY_MONTH
            "EVERY_THREE_WEEKS" -> Frequency.EVERY_THREE_WEEKS
            "EVERY_TWO_WEEKS" -> Frequency.EVERY_TWO_WEEKS
            "EVERY_WEEK" -> Frequency.EVERY_WEEK
            "EVERY_SIX_DAYS" -> Frequency.EVERY_SIX_DAYS
            "EVERY_FIVE_DAYS" -> Frequency.EVERY_FIVE_DAYS
            "EVERY_FOUR_DAYS" -> Frequency.EVERY_FOUR_DAYS
            "EVERY_THREE_DAYS" -> Frequency.EVERY_THREE_DAYS
            "EVERY_TWO_DAYS" -> Frequency.EVERY_TWO_DAYS
            "EVERYDAY" -> Frequency.EVERYDAY
            else -> Frequency.NONE
        }
    }

    @TypeConverter
    fun fromFrequencyToString(frequency: Frequency): String = frequency.toString()

    @TypeConverter
    fun fromStringToCategory(category: String): Category {
        return when (category) {
            "HOUSE_CHORE" -> Category.HOUSE_CHORE
            "WORK" -> Category.WORK
            "HOBBY" -> Category.HOBBY
            "LEARNING" -> Category.LEARNING
            "FITNESS" -> Category.FITNESS
            "FINANCE" -> Category.FINANCE
            "HEALTH" -> Category.HEALTH
            "OTHER" -> Category.OTHER
            "NONE" -> Category.NONE
            else -> Category.NONE
        }
    }

    @TypeConverter
    fun fromCategoryToString(category: Category): String = category.toString()
}