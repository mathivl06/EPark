package com.example.eparkprogram.notifications

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.eparkprogram.MainActivity
import com.example.eparkprogram.R

class NotificationService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 999
        private const val EXTRA_SESSION_ID = "sessionId"
        private const val EXTRA_START_TIME = "startTime"
        private const val EXTRA_DURATION = "duration"

        fun startService(context: Context, sessionId: Long, startTimeMillis: Long, durationMinutes: Long) {
            val intent = Intent(context, NotificationService::class.java).apply {
                putExtra(EXTRA_SESSION_ID, sessionId)
                putExtra(EXTRA_START_TIME, startTimeMillis)
                putExtra(EXTRA_DURATION, durationMinutes)
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, NotificationService::class.java)
            context.stopService(intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sessionId = intent?.getLongExtra(EXTRA_SESSION_ID, -1L) ?: -1L

        if (sessionId != -1L) {
            NotificationHelper.createNotificationChannel(this)
            startForeground(NOTIFICATION_ID, createForegroundNotification())

            // Programamos la alarma de aviso de vencimiento usando el Helper
            val startTime = intent?.getLongExtra(EXTRA_START_TIME, System.currentTimeMillis()) ?: System.currentTimeMillis()
            val duration = intent?.getLongExtra(EXTRA_DURATION, 180L) ?: 180L

            NotificationHelper.scheduleSessionExpiration(
                context = this,
                sessionId = sessionId,
                startTimeMillis = startTime,
                maxDurationMinutes = duration
            )
        } else {
            stopSelf()
        }

        return START_STICKY
    }

    private fun createForegroundNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("openActiveSession", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
            .setContentTitle("Sesión de parqueo activa")
            .setContentText("Estamos monitoreando tu tiempo para avisarte antes de vencer.")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
