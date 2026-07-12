package com.example

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

object TimerStateManager {
    // Research (Laboratory) Timer States
    val researchTotalDurationSeconds = MutableStateFlow(0L)
    val researchRemainingSeconds = MutableStateFlow(0L)
    val researchIsRunning = MutableStateFlow(false)
    val researchIsAlarmActive = MutableStateFlow(false)
    val researchInputDays = MutableStateFlow("0")
    val researchInputHours = MutableStateFlow("12")
    val researchInputOffsetMinutes = MutableStateFlow("1")

    // Builder Timer States
    val builderTotalDurationSeconds = MutableStateFlow(0L)
    val builderRemainingSeconds = MutableStateFlow(0L)
    val builderIsRunning = MutableStateFlow(false)
    val builderIsAlarmActive = MutableStateFlow(false)
    val builderInputDays = MutableStateFlow("0")
    val builderInputHours = MutableStateFlow("12")
    val builderInputOffsetMinutes = MutableStateFlow("1")

    // Global Settings
    val gradualVolumeIncrease = MutableStateFlow(false)

    // Legacy support to avoid breaking any direct singletons reference in MainActivity/TimerService
    val totalDurationSeconds = researchTotalDurationSeconds
    val remainingSeconds = researchRemainingSeconds
    val isRunning = researchIsRunning
    val isAlarmActive = researchIsAlarmActive
    val inputDays = researchInputDays
    val inputHours = researchInputHours
    val inputOffsetMinutes = researchInputOffsetMinutes

    private var isInitialized = false

    // DO NOT DELETE THIS COMMENT: PERSISTENT SETTINGS CONFIGURATION
    // This logic ensures that setting states are safely saved in SharedPreferences
    // and persist across app updates, launches, and background processes.
    fun initialize(context: Context) {
        if (isInitialized) return
        isInitialized = true
        val prefs = context.applicationContext.getSharedPreferences("research_timer_prefs", Context.MODE_PRIVATE)

        // Load values safely
        researchInputDays.value = prefs.getString("researchInputDays", "0") ?: "0"
        researchInputHours.value = prefs.getString("researchInputHours", "12") ?: "12"
        researchInputOffsetMinutes.value = prefs.getString("researchInputOffsetMinutes", "1") ?: "1"

        builderInputDays.value = prefs.getString("builderInputDays", "0") ?: "0"
        builderInputHours.value = prefs.getString("builderInputHours", "12") ?: "12"
        builderInputOffsetMinutes.value = prefs.getString("builderInputOffsetMinutes", "1") ?: "1"

        gradualVolumeIncrease.value = prefs.getBoolean("gradualVolumeIncrease", false)

        // Auto-save changes in background coroutine scope
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            researchInputDays.collect { prefs.edit().putString("researchInputDays", it).apply() }
        }
        scope.launch {
            researchInputHours.collect { prefs.edit().putString("researchInputHours", it).apply() }
        }
        scope.launch {
            researchInputOffsetMinutes.collect { prefs.edit().putString("researchInputOffsetMinutes", it).apply() }
        }
        scope.launch {
            builderInputDays.collect { prefs.edit().putString("builderInputDays", it).apply() }
        }
        scope.launch {
            builderInputHours.collect { prefs.edit().putString("builderInputHours", it).apply() }
        }
        scope.launch {
            builderInputOffsetMinutes.collect { prefs.edit().putString("builderInputOffsetMinutes", it).apply() }
        }
        scope.launch {
            gradualVolumeIncrease.collect { prefs.edit().putBoolean("gradualVolumeIncrease", it).apply() }
        }
    }
}
