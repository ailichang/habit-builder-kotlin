package com.habitbuilder

import android.app.Application
import com.habitbuilder.achievement.data.AchievementDao
import com.habitbuilder.habit.data.HabitDao
import com.habitbuilder.habit.data.HabitRepository
import com.habitbuilder.mission.data.MissionDao
import com.habitbuilder.mission.data.MissionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppDatabaseModule {

    @Singleton
    @Provides
    fun providesCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
    @Singleton
    @Provides
    fun providesAppDatabase(application: Application, scope: CoroutineScope): AppDatabase {
        return AppDatabase.getInstance(application, scope)
    }

    @Singleton
    @Provides
    fun providesMissionDao(appDatabase: AppDatabase): MissionDao {
        return appDatabase.getMissionDao()
    }

    @Singleton
    @Provides
    fun providesHabitDao(appDatabase: AppDatabase): HabitDao {
        return appDatabase.getHabitDao()
    }

    @Singleton
    @Provides
    fun providesMissionRepository(missionDao: MissionDao): MissionRepository {
        return MissionRepository(missionDao)
    }


    @Singleton
    @Provides
    fun providesHabitRepository(habitDao: HabitDao): HabitRepository {
        return HabitRepository(habitDao)
    }

    @Singleton
    @Provides
    fun providesAchievementDao(appDatabase: AppDatabase): AchievementDao {
        return appDatabase.getAchievementDao()
    }
}
