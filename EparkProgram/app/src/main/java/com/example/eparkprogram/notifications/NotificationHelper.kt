
package com.example.eparkprogram.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.eparkprogram.R

object NotificationHelper {

    const val CHANNEL_ID = "parking_expiration_channel"
    private const val CHANNEL_NAME = "Recordatorio de sesión de parqueo"
    private const val CHANNEL_DESCRIPTION = "Notificaciones cuando tu sesión está por vencer"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int,
        intent: Intent? = null
    ) {
        val pendingIntent = intent?.let {
            PendingIntent.getActivity(
                context,
                notificationId,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(notificationId, builder.build())
    }

    fun scheduleSessionExpiration(
        context: Context,
        sessionId: Long,
        startTimeMillis: Long,
        maxDurationMinutes: Long = 180,
        marginMinutes: Int = 10
    ) {
        // MODO PRUEBA: La notificación se disparará en 10 segundos
        val alarmTime = System.currentTimeMillis() + (10 * 1000)
        
        /* Lógica real (comentada para pruebas):
        val expirationTime = startTimeMillis + (maxDurationMinutes * 60 * 1000)
        val alarmTime = expirationTime - (marginMinutes * 60 * 1000)
        */

        if (alarmTime <= System.currentTimeMillis()) return

        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("sessionId", sessionId)
            putExtra("title", "Tu sesión de parqueo está por vencer")
            putExtra("message", "En $marginMinutes minutos expirará. Finaliza para evitar multas.")
            putExtra("notificationId", sessionId.toInt())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            sessionId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
            }
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
        }
    }

    fun cancelSessionExpiration(context: Context, sessionId: Long) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            sessionId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}