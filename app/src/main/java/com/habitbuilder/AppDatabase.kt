package com.habitbuilder

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.habitbuilder.achievement.data.Achievement
import com.habitbuilder.achievement.data.AchievementDao
import com.habitbuilder.habit.data.Category
import com.habitbuilder.habit.data.Frequency
import com.habitbuilder.habit.data.Habit
import com.habitbuilder.habit.data.HabitDao
import com.habitbuilder.habit.data.Priority
import com.habitbuilder.habit.data.Type
import com.habitbuilder.mission.data.Mission
import com.habitbuilder.mission.data.MissionDao
import com.habitbuilder.util.Converter
import com.habitbuilder.util.JsonFileReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.Executors

@Database (entities = [Habit::class, Mission::class, Achievement::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase(){
    companion object{
        private const val DATA_BASE_NAME = "habit_db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context, scope:CoroutineScope): AppDatabase {
            return instance?:
            synchronized(this){
                instance = databaseBuilder(context.applicationContext, AppDatabase::class.java, DATA_BASE_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(object:Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Executors.newSingleThreadExecutor().execute {
                                fillInitData(context, scope)
                            }
                        }
                    }).build()
                return instance as AppDatabase
            }
        }

        fun fillInitData(context: Context, scope: CoroutineScope){
            val habitJsonArray:JSONArray? = JsonFileReader.loadJsonArray(context, R.raw.habit_examples, "habits")
            val habitList:ArrayList<Habit> = ArrayList()
            habitJsonArray?.let {
                try {
                    for (i in 0 until it.length()) {
                        habitList.add(readHabit(it.getJSONObject(i)))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            val achievementJsonArray: JSONArray? =
                JsonFileReader.loadJsonArray(context, R.raw.achievement_init, "achievements")
            val achievementList:ArrayList<Achievement> = ArrayList()
            achievementJsonArray?.let {
                try {
                    for (i in 0 until it.length()) {
                        achievementList.add(readAchievement(it.getJSONObject(i)))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            instance?.run {
                scope.launch {
                    val deferred = async { this@run.getHabitDao().insert(*habitList.toTypedArray()) }
                    deferred.await()
                    this@run.getAchievementDao().insert(*achievementList.toTypedArray())
                }
            }
        }

        private fun readHabit(jsonObject:JSONObject):Habit {
            val title = jsonObject.getString("title")
            val description = jsonObject.getString("description")
            val location = jsonObject.getString("location")
            val converter = Converter()
            val type: Type = converter.fromStringToHabitType(jsonObject.getString("type"))
            val priority: Priority = converter.fromIntToPriority(jsonObject.getInt("priority"))
            val category: Category = converter.fromStringToCategory(jsonObject.getString("category"))
            val frequency: Frequency = converter.fromStringToFrequency(jsonObject.getString("frequency"))
            val arr: JSONArray = jsonObject.getJSONArray("scheduled_days")
            val length = arr.length()
            val scheduledDays = ArrayList<Boolean>()
            for (j in 0 until length) {
                scheduledDays.add(arr.getBoolean(j))
            }
            val startTime = jsonObject.getString("start_time")
            var endTime = ""
            if (!jsonObject.isNull("end_time")) {
                endTime = jsonObject.getString("end_time")
            }
            var habitId: UUID = UUID.randomUUID()
            if (!jsonObject.isNull("id")) {
                habitId = UUID.fromString(jsonObject.getString("id"))
            }
            when (type) {
                Type.COUNTER -> {
                    val targetCount = jsonObject.getInt("target_count")
                    return Habit(
                        habitId = habitId,
                        title = title,
                        type = type,
                        targetCount = targetCount,
                        description = description,
                        location = location,
                        frequency = frequency,
                        category = category,
                        priority = priority,
                        scheduledDays = scheduledDays,
                        startTime = startTime,
                        endTime = endTime
                    )

                }

                Type.TIMER -> {
                    val targetDuration = jsonObject.getLong("target_duration")
                    return Habit(
                        habitId = habitId,
                        title = title,
                        type = type,
                        targetDuration = targetDuration,
                        description = description,
                        location = location,
                        frequency = frequency,
                        category = category,
                        priority = priority,
                        scheduledDays = scheduledDays,
                        startTime = startTime,
                        endTime = endTime
                    )
                }
            }
        }

        private fun readAchievement(jsonObject:JSONObject):Achievement{
            val habitId = UUID.fromString(jsonObject.getString("habit_id"))
            val experiencePoints = jsonObject.getLong("experience_points")
            return Achievement(habitId = habitId, experiencePoints = experiencePoints)
        }


    }
    abstract fun getHabitDao(): HabitDao
    abstract fun getMissionDao(): MissionDao
    abstract fun getAchievementDao(): AchievementDao

}
