package com.example

import kotlinx.coroutines.flow.MutableStateFlow

object TimerStateManager {
    val totalDurationSeconds = MutableStateFlow(0L)
    val remainingSeconds = MutableStateFlow(0L)
    val isRunning = MutableStateFlow(false)
    val isAlarmActive = MutableStateFlow(false)
    
    // Original input states to persist in memory
    val inputDays = MutableStateFlow("0")
    val inputHours = MutableStateFlow("12")
    
    // Volume adjustment option
    val gradualVolumeIncrease = MutableStateFlow(false)
    
    // Timer calculation offset
    val inputOffsetMinutes = MutableStateFlow("1")
}
