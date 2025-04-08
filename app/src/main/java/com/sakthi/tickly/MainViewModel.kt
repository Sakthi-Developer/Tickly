package com.sakthi.tickly

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

            dataStoreManager.readDurations().collect {
                _timerMode.value = it
            }
        }
        setSelectedTimerMode(TimerMode.Pomodoro)
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


