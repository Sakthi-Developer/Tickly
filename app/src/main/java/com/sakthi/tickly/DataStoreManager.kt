package com.sakthi.tickly

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val context: Context) {

    private val dataStore = context.dataStore

    suspend fun saveDuration(mode: TimerMode, duration: Int) {
        dataStore.edit { preferences ->
            when (mode) {
                TimerMode.Pomodoro -> preferences[TimerPreferencesKeys.POMODORO_DURATION] = duration
                TimerMode.ShortBreak -> preferences[TimerPreferencesKeys.SHORT_BREAK_DURATION] =
                    duration

                TimerMode.LongBreak -> preferences[TimerPreferencesKeys.LONG_BREAK_DURATION] =
                    duration
            }
        }
    }


    fun readDurations(): Flow<Map<TimerMode, Int>> {
        return dataStore.data.map { preferences ->
            mapOf(
                TimerMode.Pomodoro to (preferences[TimerPreferencesKeys.POMODORO_DURATION] ?: 25),
                TimerMode.ShortBreak to (preferences[TimerPreferencesKeys.SHORT_BREAK_DURATION]
                    ?: 5),
                TimerMode.LongBreak to (preferences[TimerPreferencesKeys.LONG_BREAK_DURATION] ?: 15)
            )
        }
    }


}
