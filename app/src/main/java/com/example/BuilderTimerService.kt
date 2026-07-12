package com.example

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BuilderTimerService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var timerJob: Job? = null
    private var alarmTimeoutJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null
    private var volumeJob: Job? = null
    private var vibrator: Vibrator? = null
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        const val CHANNEL_ID_ONGOING = "BuilderTimerOngoingChannel"
        const val CHANNEL_ID_ALARM = "BuilderTimerAlarmChannel"
        const val NOTIFICATION_ID = 1338

        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_STOP_ALARM = "ACTION_STOP_ALARM"
        
        const val EXTRA_DURATION = "EXTRA_DURATION"

        fun startService(context: Context, durationSeconds: Long) {
            val intent = Intent(context, BuilderTimerService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_DURATION, durationSeconds)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, BuilderTimerService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        fun stopAlarm(context: Context) {
            val intent = Intent(context, BuilderTimerService::class.java).apply {
                action = ACTION_STOP_ALARM
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val duration = intent.getLongExtra(EXTRA_DURATION, 0L)
                startTimer(duration)
            }
            ACTION_STOP -> {
                stopTimerAndAlarm()
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.cancel(NOTIFICATION_ID)
                stopSelf()
            }
            ACTION_STOP_ALARM -> {
                stopAlarmAndVibration()
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.cancel(NOTIFICATION_ID)
                if (TimerStateManager.builderRemainingSeconds.value <= 0) {
                    stopSelf()
                } else {
                    updateNotification(TimerStateManager.builderRemainingSeconds.value, isComplete = false)
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun startTimer(durationSeconds: Long) {
        timerJob?.cancel()
        stopAlarmAndVibration()

        TimerStateManager.builderTotalDurationSeconds.value = durationSeconds
        TimerStateManager.builderRemainingSeconds.value = durationSeconds
        TimerStateManager.builderIsRunning.value = true
        TimerStateManager.builderIsAlarmActive.value = false

        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
            wakeLock = powerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BuilderTimer::TimerWakeLock")?.apply {
                acquire(durationSeconds * 1000L + 60000L)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        startForeground(NOTIFICATION_ID, buildNotification(durationSeconds, isComplete = false))

        val endTimeMillis = SystemClock.elapsedRealtime() + durationSeconds * 1000L

        timerJob = serviceScope.launch {
            while (true) {
                val currentTime = SystemClock.elapsedRealtime()
                val remainingMillis = endTimeMillis - currentTime
                val remaining = maxOf(0L, (remainingMillis + 999L) / 1000L)
                
                TimerStateManager.builderRemainingSeconds.value = remaining
                
                if (remaining <= 0) {
                    break
                }
                
                updateNotification(remaining, isComplete = false)
                delay(1000)
            }
            triggerAlarm()
        }
    }

    private fun triggerAlarm() {
        TimerStateManager.builderRemainingSeconds.value = 0
        TimerStateManager.builderIsRunning.value = false
        TimerStateManager.builderIsAlarmActive.value = true

        updateNotification(0, isComplete = true)
        playAlarmSound()
        vibratePhone()

        alarmTimeoutJob?.cancel()
        alarmTimeoutJob = serviceScope.launch {
            delay(30000L)
            stopAlarmAndVibration()
        }
    }

    private fun playAlarmSound() {
        try {
            stopAlarmSound()
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val mp = MediaPlayer().apply {
                setDataSource(applicationContext, alarmUri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                prepare()
            }
            mediaPlayer = mp

            if (TimerStateManager.gradualVolumeIncrease.value) {
                mp.setVolume(0.01f, 0.01f)
                mp.start()
                volumeJob?.cancel()
                volumeJob = serviceScope.launch {
                    val steps = 15
                    val durationMs = 15000L
                    val stepInterval = durationMs / steps
                    for (i in 1..steps) {
                        delay(stepInterval)
                        val vol = i.toFloat() / steps.toFloat()
                        try {
                            mediaPlayer?.setVolume(vol, vol)
                        } catch (e: Exception) {
                            break
                        }
                    }
                }
            } else {
                mp.setVolume(1.0f, 1.0f)
                mp.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun vibratePhone() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(android.os.VibrationEffect.createWaveform(longArrayOf(0, 500, 500, 500), 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 500, 500, 500), 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAlarmSound() {
        try {
            volumeJob?.cancel()
            volumeJob = null
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAlarmAndVibration() {
        alarmTimeoutJob?.cancel()
        alarmTimeoutJob = null
        stopAlarmSound()
        try {
            vibrator?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        TimerStateManager.builderIsAlarmActive.value = false
    }

    private fun stopTimerAndAlarm() {
        timerJob?.cancel()
        alarmTimeoutJob?.cancel()
        alarmTimeoutJob = null
        stopAlarmAndVibration()
        TimerStateManager.builderIsRunning.value = false
        TimerStateManager.builderRemainingSeconds.value = 0
        try {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(NOTIFICATION_ID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
            wakeLock = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ongoingChannel = NotificationChannel(
                CHANNEL_ID_ONGOING,
                "Builder Timer Ongoing",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows progress of active Clash of Clans builder timers."
                enableVibration(false)
                enableLights(false)
                setSound(null, null)
            }

            val alarmChannel = NotificationChannel(
                CHANNEL_ID_ALARM,
                "Builder Timer Completed Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarm alerts when builder upgrade is completed."
                enableVibration(true)
                setBypassDnd(true)
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(ongoingChannel)
            manager.createNotificationChannel(alarmChannel)
        }
    }

    private var lastNotificationText: String? = null

    private fun buildNotification(remainingSeconds: Long, isComplete: Boolean): Notification {
        val title = if (isComplete) "⚔️ Builder Upgrade Completed!" else "🔨 Builders Upgrading..."
        val text = if (isComplete) "Your Clash builder upgrade is done! Tap to dismiss." else "Time Remaining: ${formatDuration(remainingSeconds)}"

        val openActivityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openActivityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(this, BuilderTimerService::class.java).apply {
            action = ACTION_STOP_ALARM
        }
        val dismissPendingIntent = PendingIntent.getService(
            this,
            1,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cancelIntent = Intent(this, BuilderTimerService::class.java).apply {
            action = ACTION_STOP
        }
        val cancelPendingIntent = PendingIntent.getService(
            this,
            2,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = if (isComplete) CHANNEL_ID_ALARM else CHANNEL_ID_ONGOING
        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_hammer)
            .setContentIntent(pendingIntent)
            .setOngoing(!isComplete)
            .setAutoCancel(isComplete)
            .setPriority(if (isComplete) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_LOW)
            .setCategory(if (isComplete) NotificationCompat.CATEGORY_ALARM else NotificationCompat.CATEGORY_PROGRESS)
            .setOnlyAlertOnce(true)

        if (isComplete) {
            builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "DISMISS ALARM", dismissPendingIntent)
        } else {
            builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "CANCEL TIMER", cancelPendingIntent)
        }

        return builder.build()
    }

    private fun updateNotification(remainingSeconds: Long, isComplete: Boolean) {
        val currentText = if (isComplete) "complete" else formatDuration(remainingSeconds)
        if (currentText == lastNotificationText && !isComplete) {
            return
        }
        lastNotificationText = currentText

        val notification = buildNotification(remainingSeconds, isComplete)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun formatDuration(seconds: Long): String {
        val d = seconds / 86400
        val h = (seconds % 86400) / 3600
        val m = (seconds % 3600) / 60
        
        val displayMin = if (seconds > 0 && d == 0L && h == 0L && m == 0L) 1L else m
        
        return if (d > 0) {
            "${d}d ${h}h ${displayMin}m"
        } else if (h > 0) {
            "${h}h ${displayMin}m"
        } else {
            "${displayMin}m"
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        stopTimerAndAlarm()
        super.onDestroy()
    }
}
