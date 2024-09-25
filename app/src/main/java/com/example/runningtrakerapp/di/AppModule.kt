package com.example.runningtrakerapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.navigation.Navigator
import androidx.room.Room
import com.example.runningtrakerapp.db.RunDatabase
import com.example.runningtrakerapp.utill.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runningtrakerapp.utill.Constants.KEY_NAME
import com.example.runningtrakerapp.utill.Constants.KEY_WEIGHT
import com.example.runningtrakerapp.utill.Constants.RUNNING_DATABASE_NAME
import com.example.runningtrakerapp.utill.Constants.SHARED_PREFERENCES_NAME
import com.example.runningtrakerapp.utill.Constants.WEEKLY_GOAL_PREFERENCES
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext app: Context): RunDatabase {
        return Room.databaseBuilder(
            app,
            RunDatabase::class.java,
            RUNNING_DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideRunDao(db: RunDatabase) = db.getRunDao()


    //    @Singleton
//    @Provides
//    fun provideSharedPreference(@ApplicationContext context: Context) =
//        context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
    @Singleton
    @Provides
    @Named("defaultPreferences")
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    // Weekly Goal SharedPreferences
    @Singleton
    @Provides
    @Named("weeklyGoalPreferences")
    fun provideWeeklyGoalSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(WEEKLY_GOAL_PREFERENCES, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideName(@Named("defaultPreferences") sharedPre: SharedPreferences) =
        sharedPre.getString(KEY_NAME, "") ?: ""

    @Singleton
    @Provides
    fun provideWeight(@Named("defaultPreferences") sharedPre: SharedPreferences) =
        sharedPre.getFloat(KEY_WEIGHT, 80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(@Named("defaultPreferences") sharedPre: SharedPreferences) =
        sharedPre.getBoolean(KEY_FIRST_TIME_TOGGLE, true)


}