package com.example.eparkprogram.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.eparkprogram.MainActivity

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val sessionId = intent.getLongExtra("sessionId", -1L)
        val title = intent.getStringExtra("title") ?: "Sesión por vencer"
        val message = intent.getStringExtra("message") ?: "Tu tiempo de parqueo está por expirar."
        val notificationId = intent.getIntExtra("notificationId", sessionId.toInt())

        val openIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("openActiveSession", true)
            putExtra("sessionId", sessionId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        NotificationHelper.showNotification(context, title, message, notificationId, openIntent)
    }
}