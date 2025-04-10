package com.sakthi.tickly

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MainViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private var initialTimerValue = 0

    private val _timerValue = MutableStateFlow(initialTimerValue)

    val timerValue: StateFlow<Int> = _timerValue.asStateFlow()

    private var timerJob: Job? = null

    private var _timerMode = MutableStateFlow<Map<TimerMode, Int>>(emptyMap())
    val timerMode: StateFlow<Map<TimerMode, Int>> = _timerMode.asStateFlow()

    private val _selectedTimerMode = MutableStateFlow(TimerMode.Pomodoro)
    val selectedTimerMode: StateFlow<TimerMode> = _selectedTimerMode.asStateFlow()

    private val _isTimerStarted = MutableStateFlow(false)
    val isTimerStarted: StateFlow<Boolean> = _isTimerStarted.asStateFlow()


    init {
        viewModelScope.launch {
            dataStoreManager.readDurations().collect { durations ->
                if (durations.isEmpty()) {
                    // Default durations
                    dataStoreManager.saveDuration(TimerMode.Pomodoro, 25)
                    dataStoreManager.saveDuration(TimerMode.ShortBreak, 5)
                    dataStoreManager.saveDuration(TimerMode.LongBreak, 15)
                } else {
                    _timerMode.value = durations
                    // Set initial timer value based on selected mode
                    val initialDuration = durations[_selectedTimerMode.value] ?: 25
                    _timerValue.value = initialDuration * 60 // assuming seconds
                }
                Log.d("MainViewModel", "Initialized timerMode: ${timerMode.value}")
            }
        }
    }


    fun setSelectedTimerMode(mode: TimerMode) {
        _selectedTimerMode.value = mode
        initialTimerValue = (timerMode.value[mode] ?: 25) * 60
        _timerValue.value = initialTimerValue
        timerJob?.cancel()
        timerJob = null
        _isTimerStarted.value = false
    }

    fun changeTimerStartedState() {
        _isTimerStarted.value = !_isTimerStarted.value
    }


    fun saveDuration(mode: TimerMode, duration: Int) {
        viewModelScope.launch {
            dataStoreManager.saveDuration(mode, duration)
        }
    }


    fun startTimer() {

        timerJob?.cancel()

        if (_timerValue.value == 0) {
            _timerValue.value = initialTimerValue
        }

        timerJob = viewModelScope.launch {
            while (_timerValue.value > 0) {
                delay(1000)
                _timerValue.value--
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun resumeTimer() {
        if (timerJob == null && _timerValue.value > 0) {
            timerJob = viewModelScope.launch {
                while (_timerValue.value > 0) {
                    delay(1000)
                    _timerValue.value--
                }
            }
        }
    }

    fun restartTimer() {
        timerJob?.cancel()
        _timerValue.value = initialTimerValue
    }
}


